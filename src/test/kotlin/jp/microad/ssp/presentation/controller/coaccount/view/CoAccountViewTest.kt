package jp.mangaka.ssp.presentation.controller.coaccount.view

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("CoAccountViewのテスト")
private class CoAccountViewTest {
    @Nested
    @DisplayName("生成のテスト")
    inner class OfTest {
        val sessionCoAccounts = listOf(
            SessionCoAccount(CoAccountId(1), "Coアカウント1", mock()),
            SessionCoAccount(CoAccountId(2), "Coアカウント2", mock()),
            SessionCoAccount(CoAccountId(3), "Coアカウント3", mock())
        )

        @Test
        @DisplayName("データあり")
        fun isNotEmpty() {
            val actual = CoAccountView.of(sessionCoAccounts)

            assertEquals(
                listOf(
                    CoAccountView(CoAccountId(1), "Coアカウント1"),
                    CoAccountView(CoAccountId(2), "Coアカウント2"),
                    CoAccountView(CoAccountId(3), "Coアカウント3")
                ),
                actual
            )
        }

        @Test
        @DisplayName("データなし")
        fun isEmpty() {
            val actual = CoAccountView.of(emptyList())

            assertTrue(actual.isEmpty())
        }
    }
}
