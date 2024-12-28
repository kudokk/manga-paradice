package jp.mangaka.ssp.application.service.spot.validation.banner

import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("SizeTypeValidationのテスト")
private class SizeTypeValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @DisplayName("横幅のテスト")
    inner class WidthTest {
        @ParameterizedTest
        @ValueSource(ints = [-1, 65536])
        @DisplayName("範囲外の値")
        fun isOutOfRange(value: Int) {
            validator.validate(SizeTypeValidation(value, 0)).run {
                assertTrue(any { it.propertyPath.toString() == "width" })
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [0, 65535])
        fun isValid(value: Int) {
            validator.validate(SizeTypeValidation(value, 0)).run {
                assertTrue(none { it.propertyPath.toString() == "width" })
            }
        }
    }

    @Nested
    @DisplayName("縦幅のテスト")
    inner class HeightTest {
        @ParameterizedTest
        @ValueSource(ints = [-1, 65536])
        @DisplayName("範囲外の値")
        fun isOutOfRange(value: Int) {
            validator.validate(SizeTypeValidation(0, value)).run {
                assertTrue(any { it.propertyPath.toString() == "height" })
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [0, 65535])
        fun isValid(value: Int) {
            validator.validate(SizeTypeValidation(0, value)).run {
                assertTrue(none { it.propertyPath.toString() == "height" })
            }
        }
    }
}
