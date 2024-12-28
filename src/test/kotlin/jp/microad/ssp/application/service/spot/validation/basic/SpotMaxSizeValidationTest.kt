package jp.mangaka.ssp.application.service.spot.validation.basic

import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("SpotMaxSizeValidationのテスト")
private class SpotMaxSizeValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("横幅のテスト")
    inner class WidthTest {
        @ParameterizedTest
        @ValueSource(ints = [0, 65536])
        @DisplayName("範囲外の値")
        fun isOutOfRange(value: Int) {
            validator.validate(SpotMaxSizeValidation(value, null)).run {
                assertTrue(any { it.propertyPath.toString() == "width" })
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(value: Int?) {
            validator.validate(SpotMaxSizeValidation(value, null)).run {
                assertTrue(none { it.propertyPath.toString() == "width" })
            }
        }

        private fun validParams() = listOf(null, 1, 65535)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("縦幅のテスト")
    inner class HeightTest {
        @ParameterizedTest
        @ValueSource(ints = [0, 65536])
        @DisplayName("範囲外の値")
        fun isOutOfRange(value: Int) {
            validator.validate(SpotMaxSizeValidation(null, value)).run {
                assertTrue(any { it.propertyPath.toString() == "height" })
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(value: Int?) {
            validator.validate(SpotMaxSizeValidation(null, value)).run {
                assertTrue(none { it.propertyPath.toString() == "height" })
            }
        }

        private fun validParams() = listOf(null, 1, 65535)
    }
}
