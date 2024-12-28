package jp.mangaka.ssp.application.valueobject.aspectratio

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio16to5
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio16to9
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio32to5
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@DisplayName("AspectRatioIdのテスト")
private class AspectRatioIdTest {
    companion object {
        val otherAspectRatioId = AspectRatioId(9999)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("isWipeVideoのテスト")
    inner class IsWipeVideoTest {
        @Test
        @DisplayName("ワイプ動画に対応するアスペクト比")
        fun isWipe() {
            assertTrue(aspectRatio16to9.isWipeVideo())
        }

        @ParameterizedTest
        @MethodSource("notWipeParams")
        @DisplayName("ワイプ動画に対応しないアスペクト比")
        fun isNotWipe(aspectRatioId: AspectRatioId) {
            assertFalse(aspectRatioId.isWipeVideo())
        }

        private fun notWipeParams() = listOf(aspectRatio32to5, aspectRatio16to5, otherAspectRatioId)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("isFullWideVideoのテスト")
    inner class IsFullWideVideoTest {
        @ParameterizedTest
        @MethodSource("fullWideParams")
        @DisplayName("フルワイド動画に対応するアスペクト比")
        fun isFullWide(aspectRatioId: AspectRatioId) {
            assertTrue(aspectRatioId.isFullWideVideo())
        }

        private fun fullWideParams() = listOf(aspectRatio16to5, aspectRatio32to5)

        @ParameterizedTest
        @MethodSource("notFullWideParams")
        @DisplayName("フルワイド動画に対応しないアスペクト比")
        fun isNotFullWide() {
            assertFalse(aspectRatio16to9.isFullWideVideo())
        }

        private fun notFullWideParams() = listOf(aspectRatio16to9, otherAspectRatioId)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("isInlineVideoのテスト")
    inner class IsInlineVideoTest {
        @Test
        @DisplayName("インライン動画に対応するアスペクト比")
        fun isInline() {
            assertTrue(otherAspectRatioId.isInlineVideo())
        }

        @ParameterizedTest
        @MethodSource("notInlineParams")
        @DisplayName("インライン動画に対応しないアスペクト比")
        fun isNotInline(aspectRatioId: AspectRatioId) {
            assertFalse(aspectRatioId.isInlineVideo())
        }

        private fun notInlineParams() = listOf(aspectRatio16to9, aspectRatio16to5, aspectRatio32to5)
    }
}
