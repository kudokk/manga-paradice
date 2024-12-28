package jp.mangaka.ssp.util.validation

import jp.mangaka.ssp.util.validation.ValidationUtils.DecimalConfig
import jp.mangaka.ssp.util.validation.ValidationUtils.validateBigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal

@DisplayName("ValidationUtilsのテスト")
private class ValidationUtilsTest {
    @Nested
    @DisplayName("validateBigDecimalのテスト")
    inner class ValidateBigDecimalTest {
        val config = DecimalConfig(BigDecimal("5.000"), BigDecimal("15.000"), 2, 3)

        @Test
        @DisplayName("下限値より小さい")
        fun isOverMin() {
            assertEquals(validateBigDecimal(BigDecimal("4.999"), config), "Validation.Number.Min")
        }

        @Test
        @DisplayName("上限値より大きい")
        fun isOverMax() {
            assertEquals(validateBigDecimal(BigDecimal("15.001"), config), "Validation.Number.Max")
        }

        @ParameterizedTest
        @ValueSource(strings = ["100.000", "50.1234"])
        @DisplayName("フォーマット不正")
        fun isInvalidFormat(value: String) {
            // 最大値と整数の桁数が同じ場合、最大値のエラーになるので最大値を大きくしている
            val config = DecimalConfig(BigDecimal("0.000"), BigDecimal("999.999"), 2, 3)

            assertEquals(validateBigDecimal(BigDecimal(value), config), "Validation.Decimal.Format")
        }

        @ParameterizedTest
        @ValueSource(strings = ["5.000", "5.1", "14.9", "15.000"])
        @DisplayName("正常")
        fun isValid(value: String) {
            assertNull(validateBigDecimal(BigDecimal(value), config))
        }
    }
}
