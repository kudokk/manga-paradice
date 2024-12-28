package jp.mangaka.ssp.presentation.config.interceptor

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.presentation.config.SessionConfig.SessionKey
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("CoAccountRequestInterceptorのテスト")
private class CoAccountRequestInterceptorTest {
    val sut = spy(CoAccountRequestInterceptor())

    val outputStream: ServletOutputStream = mock()
    val session: HttpSession = mock()
    val request: HttpServletRequest = mock {
        on { this.session } doReturn session
    }
    val response: HttpServletResponse = mock {
        on { this.outputStream } doReturn outputStream
    }

    @Nested
    @DisplayName("preHandleのテスト")
    inner class PreHandleTest {
        @Test
        @DisplayName("リクエストパラメーターにCoアカウントIDがない")
        fun isNoCoAccountId() {
            doReturn(null).whenever(request).getParameter("coAccountId")

            assertTrue(sut.preHandle(request, response, mock()))
            verify(sut, never()).isValidCoAccount(any(), any())
            verify(response, never()).status = any()
            verify(outputStream, never()).println(any<String>())
        }

        @Test
        @DisplayName("不正なCoアカウントID")
        fun isInvalidCoAccountId() {
            doReturn("1").whenever(request).getParameter("coAccountId")
            doReturn(false).whenever(sut).isValidCoAccount(any(), any())
            doNothing().whenever(outputStream).println(any<String>())

            assertFalse(sut.preHandle(request, response, mock()))
            verify(sut, times(1)).isValidCoAccount("1", request)
            verify(response, times(1)).status = HttpServletResponse.SC_BAD_REQUEST
            verify(outputStream, times(1)).println("""{"message": "Invalid Request Parameter"}""")
        }

        @Test
        @DisplayName("正常なCoアカウントID")
        fun isValidCoAccountId() {
            doReturn("1").whenever(request).getParameter("coAccountId")
            doReturn(true).whenever(sut).isValidCoAccount(any(), any())

            assertTrue(sut.preHandle(request, response, mock()))
            verify(sut, times(1)).isValidCoAccount("1", request)
            verify(response, never()).status = any()
            verify(outputStream, never()).println(any<String>())
        }
    }

    @Nested
    @DisplayName("isValidCoAccountのテスト")
    inner class IsValidCoAccountTest {
        init {
            listOf(mockSessionCoAccount(1), mockSessionCoAccount(2)).let {
                doReturn(it).whenever(session).getAttribute(SessionKey.CO_ACCOUNT_LIST.name)
            }
        }

        @Test
        @DisplayName("CoアカウントIDが数値でない")
        fun isNotNumberCoAccountId() {
            assertFalse(sut.isValidCoAccount("aaa", request))
        }

        @Test
        @DisplayName("CoアカウントIDが0")
        fun isCoAccountIdZero() {
            assertTrue(sut.isValidCoAccount("0", request))
        }

        @Test
        @DisplayName("セッションのCoアカウントリストに存在しない")
        fun isNotAllowedCoAccountId() {
            assertFalse(sut.isValidCoAccount("99", request))
        }

        @Test
        @DisplayName("セッションのCoアカウントリストに存在する")
        fun isAllowedCoAccountId() {
            assertTrue(sut.isValidCoAccount("1", request))
        }

        private fun mockSessionCoAccount(coAccountId: Int): SessionCoAccount = mock {
            on { this.coAccountId } doReturn CoAccountId(coAccountId)
        }
    }
}
