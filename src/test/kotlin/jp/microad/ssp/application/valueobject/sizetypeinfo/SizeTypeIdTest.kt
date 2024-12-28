package jp.mangaka.ssp.application.valueobject.sizetypeinfo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("SizeTypeIdのテスト")
private class SizeTypeIdTest {
    @Nested
    @DisplayName("コンストラクタのテスト")
    inner class ConstructorTest {
        @ParameterizedTest
        @ValueSource(ints = [0, 1, Int.MAX_VALUE])
        @DisplayName("正常")
        fun isCorrect(value: Int) {
            assertEquals(value, SizeTypeId(value).value)
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, Int.MIN_VALUE])
        @DisplayName("負数のとき")
        fun isNegative(value: Int) {
            assertThrows<IllegalArgumentException> { SizeTypeId(value) }
        }
    }

    @Nested
    @DisplayName("isNativeのテスト")
    inner class IsNativeTest {
        @ParameterizedTest
        @ValueSource(ints = [99, 199])
        @DisplayName("ネイティブ用のサイズ種別IDのとき")
        fun isNative(value: Int) {
            assertTrue(SizeTypeId(value).isNative())
        }

        @Test
        @DisplayName("ネイティブ用のサイズ種別IDでないとき")
        fun isNotNative() {
            assertFalse(SizeTypeId(1).isNative())
        }
    }

    @Test
    @DisplayName("ofのテスト")
    fun testOf() {
        assertEquals(234, SizeTypeId.of(234).value)
    }
}
