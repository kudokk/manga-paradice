package jp.mangaka.ssp.presentation.controller.coaccount

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.presentation.config.SessionConfig
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount
import jp.mangaka.ssp.presentation.controller.coaccount.view.CoAccountView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpSession

@DisplayName("CoAccountControllerのテスト")
private class CoAccountControllerTest {
    val session = MockHttpSession()

    val sut = CoAccountController(session)

    @Nested
    @DisplayName("coAccountsのテスト")
    inner class CoAccountsTest {
        init {
            session.setAttribute(
                SessionConfig.SessionKey.CO_ACCOUNT_LIST.name,
                listOf(
                    SessionCoAccount(CoAccountId(1), "Coアカウント1", mock()),
                    SessionCoAccount(CoAccountId(2), "Coアカウント2", mock()),
                    SessionCoAccount(CoAccountId(3), "Coアカウント3", mock())
                )
            )
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.coAccounts()

            assertEquals(
                listOf(
                    CoAccountView(CoAccountId(1), "Coアカウント1"),
                    CoAccountView(CoAccountId(2), "Coアカウント2"),
                    CoAccountView(CoAccountId(3), "Coアカウント3")
                ),
                actual
            )
        }
    }
}
