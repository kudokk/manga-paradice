package com.manga.paradice.application.service.authentication

import jakarta.servlet.http.HttpSession
import com.manga.paradice.infrastructure.datasource.dao.usermaster.UserMaster
import com.manga.paradice.presentation.config.SessionConfig
import com.manga.paradice.presentation.config.valueobject.SessionCoAccount
import org.springframework.stereotype.Service

@Service
class AuthenticationServiceImpl : AuthenticationService {
    override fun addSessionCoAccountInfoList(user: UserMaster, session: HttpSession) {
        val sessionCoAccounts = listOf(
            SessionCoAccount(
                1,
                "test_account",
                "admin"
            )
        )
        session.setAttribute(SessionConfig.SessionKey.CO_ACCOUNT_LIST.name, sessionCoAccounts)
    }
}
