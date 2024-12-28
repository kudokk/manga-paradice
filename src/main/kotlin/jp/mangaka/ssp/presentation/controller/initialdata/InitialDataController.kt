package jp.mangaka.ssp.presentation.controller.initialdata

import jakarta.servlet.http.HttpSession
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.presentation.config.SessionConfig
import jp.mangaka.ssp.presentation.config.secutiry.AccountUserDetails
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount
import jp.mangaka.ssp.presentation.controller.initialdata.view.UserRoleView
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class InitialDataController(private val session: HttpSession) {
    /**
     * @param coAccountId CoアカウントID
     * @param userDetails ログインユーザー情報
     * @return ユーザーと権限情報のView
     */
    @Suppress("UNCHECKED_CAST")
    @GetMapping("/api/change-role-data")
    fun changeRoleData(
        @RequestParam("coAccountId") coAccountId: CoAccountId,
        @AuthenticationPrincipal userDetails: AccountUserDetails
    ): UserRoleView = UserRoleView.of(
        userDetails.user,
        (session.getAttribute(SessionConfig.SessionKey.CO_ACCOUNT_LIST.name) as List<SessionCoAccount>)
            .first { it.coAccountId == coAccountId }
            .userType
    )
}
