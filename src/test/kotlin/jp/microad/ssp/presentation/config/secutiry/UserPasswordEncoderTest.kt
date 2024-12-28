package jp.mangaka.ssp.presentation.config.secutiry

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("UserPasswordEncoderのテスト")
private class UserPasswordEncoderTest {
    val sut = spy(UserPasswordEncoder())

    @Test
    @DisplayName("encodeのテスト")
    fun testEncode() {
        assertEquals(
            "2bbe0c48b91a7d1b8a6753a8b9cbe1db16b84379f3f91fe115621284df7a48f1cd71e9beb90ea614c7bd924250aa9e446a866725e685a65df5d139a5cd180dc9",
            sut.encode("test1234")
        )
    }

    @Nested
    @DisplayName("matchesのテスト")
    inner class MatchesTest {
        @ParameterizedTest
        @CsvSource(value = ["'',aaa", "aaa,''", "'',''"])
        @DisplayName("引数のいずれかが空文字列の場合")
        fun isAnyOfArgumentEmpty(rawPassword: String, encodedPassword: String) {
            assertFalse(sut.matches(rawPassword, encodedPassword))
        }

        @Test
        @DisplayName("入力パスワードと不一致")
        fun isMismatch() {
            doReturn("ngPassword").whenever(sut).encode(any())

            assertFalse(sut.matches("inputPassword", "encodedPassword"))
            verify(sut, times(1)).encode("inputPassword")
        }

        @Test
        @DisplayName("入力パスワードと一致")
        fun isMatch() {
            doReturn("encodedPassword").whenever(sut).encode(any())

            assertTrue(sut.matches("inputPassword", "encodedPassword"))
            verify(sut, times(1)).encode("inputPassword")
        }
    }
}
