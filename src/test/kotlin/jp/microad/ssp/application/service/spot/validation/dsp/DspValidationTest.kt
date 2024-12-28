package jp.mangaka.ssp.application.service.spot.validation.dsp

import jakarta.validation.Validation
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal

@DisplayName("DspValidationのテスト")
private class DspValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @DisplayName("DSPIDのテスト")
    inner class DspIdTest {
        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(DspValidation(null, null, null)).run {
                assertTrue(any { it.propertyPath.toString() == "dspId" })
            }
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            validator.validate(DspValidation(DspId(1), null, null)).run {
                assertTrue(none { it.propertyPath.toString() == "dspId" })
            }
        }
    }

    @Nested
    @DisplayName("入札倍率調整のテスト")
    inner class BidAdjustTest {
        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(DspValidation(null, null, null)).run {
                assertTrue(any { it.propertyPath.toString() == "bidAdjust" })
            }
        }

        @Test
        @DisplayName("下限値より小さいとき")
        fun isOverMin() {
            validator.validate(DspValidation(null, BigDecimal("-0.001"), null)).run {
                assertTrue(any { it.propertyPath.toString() == "bidAdjust" })
            }
        }

        @Test
        @DisplayName("上限値より大きいとき")
        fun isOverMax() {
            validator.validate(DspValidation(null, BigDecimal("1000.000"), null)).run {
                assertTrue(any { it.propertyPath.toString() == "bidAdjust" })
            }
        }

        @Test
        @DisplayName("フォーマット不正")
        fun isInvalidFormat() {
            // 先に最大値の検証が行われるため整数桁数はエラーにならない
            validator.validate(DspValidation(null, BigDecimal("12.3456"), null)).run {
                assertTrue(any { it.propertyPath.toString() == "bidAdjust" })
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["0.000", "0.001", "999.998", "999.999"])
        @DisplayName("正常")
        fun isCorrect(value: String) {
            validator.validate(DspValidation(null, BigDecimal(value), null)).run {
                assertTrue(none { it.propertyPath.toString() == "bidAdjust" })
            }
        }
    }

    @Nested
    @DisplayName("フロアCPMのテスト")
    inner class FloorCpmTest {
        @Test
        @DisplayName("下限値より小さいとき")
        fun isOverMin() {
            validator.validate(DspValidation(null, null, BigDecimal("-0.00000001"))).run {
                assertTrue(any { it.propertyPath.toString() == "floorCpm" })
            }
        }

        @Test
        @DisplayName("上限値より大きいとき")
        fun isOverMax() {
            validator.validate(DspValidation(null, null, BigDecimal("10000000000.00000000"))).run {
                assertTrue(any { it.propertyPath.toString() == "floorCpm" })
            }
        }

        @Test
        @DisplayName("フォーマット不正")
        fun isInvalidFormat() {
            // 先に最大値の検証が行われるため整数桁数はエラーにならない
            validator.validate(DspValidation(null, null, BigDecimal("0.123456789"))).run {
                assertTrue(any { it.propertyPath.toString() == "floorCpm" })
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["0.00000000", "0.00000001", "9999999999.99999998", "9999999999.99999999"])
        @DisplayName("正常 - 入力あり")
        fun isCorrectAndNotEmpty(value: String) {
            validator.validate(DspValidation(null, null, BigDecimal(value))).run {
                assertTrue(none { it.propertyPath.toString() == "floorCpm" })
            }
        }

        @Test
        @DisplayName("正常 - 入力なし")
        fun isCorrectAndEmpty() {
            validator.validate(DspValidation(null, null, null)).run {
                assertTrue(none { it.propertyPath.toString() == "floorCpm" })
            }
        }
    }
}
