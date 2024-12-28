package jp.mangaka.ssp.presentation.config.secutiry

import jakarta.servlet.http.HttpServletResponse
import jp.mangaka.ssp.application.service.authentication.AuthenticationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.session.ForceEagerSessionCreationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.filter.ForwardedHeaderFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationService: AuthenticationService,
    private val restAuthenticationEntryPoint: RestAuthenticationEntryPoint,
    @Value("\${app.cookie.domain}") private val cookieDomain: String,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        // URLごとの設定
        http.authorizeHttpRequests {
            it
                .requestMatchers("/static/**").permitAll()
                .requestMatchers("/api/csrfToken").permitAll()
                .anyRequest().authenticated()
        }

        // CSRFの設定
        http.csrf { csrf ->
            val csrfTokenRepository = CookieCsrfTokenRepository
                .withHttpOnlyFalse()
                .apply {
                    // 旧画面とCookieを共有する必要があるため、ドメイン名に旧画面との共通部をセット
                    this.setCookieName("CMP-MNG-XSRF-TOKEN")
                    this.setCookieCustomizer { it.domain(cookieDomain) }
                }

            csrf.csrfTokenRepository(csrfTokenRepository)
        }

        // ログイン設定
        http.formLogin {
            it
                .loginProcessingUrl("/api/login")
                .successHandler(authenticationSuccessHandler())
                .failureHandler { _, response, _ -> response.status = HttpServletResponse.SC_UNAUTHORIZED }
                .permitAll()
        }

        // ログアウト
        http.logout {
            it
                .logoutRequestMatcher(AntPathRequestMatcher("/api/logout"))
                .logoutSuccessHandler { _, response, _ -> response.status = HttpServletResponse.SC_OK }
                .invalidateHttpSession(true)
        }

        // 例外ハンドリング
        http.exceptionHandling {
            it.authenticationEntryPoint(restAuthenticationEntryPoint)
        }

        // nginxが設定するX-Forwarded系のヘッダーを有効にするために必要なフィルター
        http.addFilterBefore(ForwardedHeaderFilter(), ForceEagerSessionCreationFilter::class.java)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = UserPasswordEncoder()

    @Bean
    fun authenticationSuccessHandler() = AuthenticationSuccessHandler { request, response, authentication ->
        authenticationService.addSessionCoAccountInfoList(
            (authentication.principal as AccountUserDetails).user,
            request.session
        )

        response.status = HttpServletResponse.SC_OK
    }
}
