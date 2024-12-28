package jp.mangaka.ssp.application.valueobject.country

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("CountryIdのテスト")
private class CountryIdTest {
    @Nested
    @DisplayName("コンストラクタのテスト")
    inner class ConstructorTest {
        @ParameterizedTest
        @ValueSource(ints = [0, 1, Int.MAX_VALUE])
        @DisplayName("正常")
        fun isCorrect(value: Int) {
            assertEquals(value, CountryId(value).value)
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, Int.MIN_VALUE])
        @DisplayName("負数のとき")
        fun isNegative(value: Int) {
            assertThrows<IllegalArgumentException> { CountryId(value) }
        }
    }

    @Test
    @DisplayName("ofのテスト")
    fun testOf() {
        assertEquals(234, CountryId.of(234).value)
    }
}
