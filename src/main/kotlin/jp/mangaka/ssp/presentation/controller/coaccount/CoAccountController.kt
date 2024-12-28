package jp.mangaka.ssp.presentation.controller.coaccount

import jakarta.servlet.http.HttpSession
import jp.mangaka.ssp.presentation.config.SessionConfig
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount
import jp.mangaka.ssp.presentation.controller.coaccount.view.CoAccountView
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CoAccountController(
    private val session: HttpSession
) {
    /**
     * @return Coアカウント一覧のView
     */
    @Suppress("UNCHECKED_CAST")
    @GetMapping("/api/co-accounts")
    fun coAccounts(): List<CoAccountView> = CoAccountView.of(
        session.getAttribute(SessionConfig.SessionKey.CO_ACCOUNT_LIST.name) as List<SessionCoAccount>
    )
}
