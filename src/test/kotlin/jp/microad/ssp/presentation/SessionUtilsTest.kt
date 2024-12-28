package jp.mangaka.ssp.presentation

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import jakarta.servlet.http.HttpSession
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@DisplayName("SessionUtilsのテスト")
private class SessionUtilsTest {
    val session: HttpSession = mock()

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getUserTypeのテスト")
    inner class GetUserType {
        init {
            listOf(sessionCoAccount(1, UserType.ma_staff), sessionCoAccount(2, UserType.agency)).let {
                doReturn(it).whenever(session).getAttribute(any())
            }
        }

        @ParameterizedTest
        @MethodSource("existingParams")
        @DisplayName("対象Coアカウントのデータが存在する")
        fun isExisting(coAccountId: CoAccountId, userType: UserType) {
            assertEquals(userType, SessionUtils.getUserType(coAccountId, session))
        }

        private fun existingParams() = listOf(
            Arguments.of(CoAccountId(1), UserType.ma_staff),
            Arguments.of(CoAccountId(2), UserType.agency)
        )

        @Test
        @DisplayName("対象Coアカウントのデータが存在しない")
        fun isNotExisting() {
            assertThrows<Exception> { SessionUtils.getUserType(CoAccountId(99), session) }
        }

        private fun sessionCoAccount(coAccountId: Int, userType: UserType) = SessionCoAccount(
            CoAccountId(coAccountId), "", userType
        )
    }
}
