package jp.mangaka.ssp.presentation

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.config.SessionConfig
import jp.mangaka.ssp.presentation.config.secutiry.AccountUserDetails
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount
import org.mockito.Mockito.mock
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

object MockMvcUtils {
    private val defaultSession = MockHttpSession().apply {
        setAttribute(
            SessionConfig.SessionKey.CO_ACCOUNT_LIST.toString(),
            listOf(
                SessionCoAccount(CoAccountId(1), "Coアカウント1", UserType.ma_staff),
                SessionCoAccount(CoAccountId(2), "Coアカウント2", UserType.agency)
            )
        )
    }

    fun mockGet(
        mockMvc: MockMvc,
        uri: String,
        userDetails: AccountUserDetails = mock(),
        session: MockHttpSession = defaultSession
    ): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .get(uri)
                .with(user(userDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
    }

    fun mockPost(
        mockMvc: MockMvc,
        uri: String,
        content: String,
        userDetails: AccountUserDetails = mock(),
        session: MockHttpSession = defaultSession
    ): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .post(uri)
                .content(content)
                .with(user(userDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
    }
}
