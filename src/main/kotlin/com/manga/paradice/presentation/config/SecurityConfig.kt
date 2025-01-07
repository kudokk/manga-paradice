package com.manga.paradice.presentation.config

import com.manga.paradice.application.service.authentication.AuthenticationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler
import org.springframework.security.web.session.ForceEagerSessionCreationFilter
import org.springframework.web.filter.ForwardedHeaderFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationService: AuthenticationService,
    private val restAuthenticationEntryPoint: RestAuthenticationEntryPoint,
) {
    @Bean
    fun configureHttpSecurity(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity.authorizeHttpRequests {
            it
                .anyRequest().permitAll()
        }
        
        httpSecurity.csrf { 
            it
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(CsrfTokenRequestAttributeHandler())
        }

//        // ログイン設定
//        httpSecurity.formLogin {
//            it
//                .loginProcessingUrl("/login")
//                .successHandler(authenticationSuccessHandler())
//                .failureHandler { _, response, _ -> response.status = HttpServletResponse.SC_UNAUTHORIZED }
//                .permitAll()
//        }
//
//        // ログアウト
//        httpSecurity.logout {
//            it
//                .logoutRequestMatcher(AntPathRequestMatcher("/api/logout"))
//                .logoutSuccessHandler { _, response, _ -> response.status = HttpServletResponse.SC_OK }
//                .invalidateHttpSession(true)
//        }
//
//        // 例外ハンドリング
//        httpSecurity.exceptionHandling {
//            it.authenticationEntryPoint(restAuthenticationEntryPoint)
//        }
//
        return httpSecurity.build()
//    }
//
//    @Bean
//    fun authenticationSuccessHandler() = AuthenticationSuccessHandler { request, response, authentication ->
//        authenticationService.addSessionCoAccountInfoList(
//            (authentication.principal as AccountUserDetails).user,
//            request.session
//        )
//
//        response.status = HttpServletResponse.SC_OK
    }
}
