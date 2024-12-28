package jp.mangaka.ssp.application.service.spot.util

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.spy
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("CryptUtilityのテスト")
private class CryptUtilsTest {
    val sut = spy(CryptUtils("0123456789abcdef", "0123456789abcdef"))

    @Nested
    @DisplayName("cryptForTagのテスト")
    inner class CryptForTagTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn("0123456789abcdef").`when`(sut).encrypt(eq("1"), any(), any())
            assertEquals("0123456789abcdef", sut.encryptForTag(SpotId(1)))
        }
    }

    @Nested
    @DisplayName("encryptのテスト")
    inner class EncryptTest {
        fun actual(id: String) = sut.encrypt(id, "0123456789abcdef", "0123456789abcdef")
        val expectValue = "f0c08ad955e3eba99b9b4882c7af8540"

        @Test
        @DisplayName("正常")
        fun isCorrect() = assertEquals(expectValue, actual("1"))
    }
}
