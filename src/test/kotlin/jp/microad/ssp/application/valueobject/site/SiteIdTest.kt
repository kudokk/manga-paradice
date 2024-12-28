package jp.mangaka.ssp.application.valueobject.site

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("SiteIdのテスト")
private class SiteIdTest {
    @Nested
    @DisplayName("コンストラクタのテスト")
    inner class ConstructorTest {
        @ParameterizedTest
        @ValueSource(ints = [0, 1, Int.MAX_VALUE])
        @DisplayName("正常")
        fun isCorrect(value: Int) {
            assertEquals(value, SiteId(value).value)
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, Int.MIN_VALUE])
        @DisplayName("負数のとき")
        fun isNegative(value: Int) {
            assertThrows<IllegalArgumentException> { SiteId(value) }
        }
    }

    @Test
    @DisplayName("ofのテスト")
    fun testOf() {
        assertEquals(234, SiteId.of(234).value)
    }
}
