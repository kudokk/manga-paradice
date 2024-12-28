package jp.mangaka.ssp.presentation.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.web.http.CookieSerializer
import org.springframework.session.web.http.DefaultCookieSerializer

@Configuration
class SessionConfig(
    @Value("\${app.cookie.domain}") private val cookieDomain: String
) {
    @Bean
    fun cookieSerializer(): CookieSerializer = DefaultCookieSerializer().apply {
        // 旧画面とCookieを共有する必要があるため、ドメイン名に旧画面との共通部をセット
        setCookieName("CMP-MNG-SESSIONID")
        setDomainName(cookieDomain)
    }

    enum class SessionKey {
        CO_ACCOUNT_LIST
    }
}
