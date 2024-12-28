package jp.mangaka.ssp.util.validation

import java.math.BigDecimal

object ValidationUtils {
    /**
     * BigDecimalの値のバリデーションを行う.
     *
     * アノテーションの @DecimalMax と @Digits で検証を行うと
     * 最大値を超えて桁上がりして両方のエラーが出てしまう場合はこの関数を使用する.
     *
     * @param value 値
     * @param config 設定
     * @return エラーがある場合はエラーメッセージ
     */
    fun validateBigDecimal(value: BigDecimal, config: DecimalConfig): String? = when {
        value < config.valueMin -> "Validation.Number.Min"
        value > config.valueMax -> "Validation.Number.Max"
        !isValidBigDecimalDigits(value, config) -> "Validation.Decimal.Format"
        else -> null
    }

    private fun isValidBigDecimalDigits(value: BigDecimal, config: DecimalConfig): Boolean {
        val intLength = value.precision() - value.scale()
        val fractionLength = if (value.scale() < 0) 0 else value.scale()

        return config.intLength >= intLength && config.fractionLength >= fractionLength
    }

    data class DecimalConfig(
        val valueMin: BigDecimal,
        val valueMax: BigDecimal,
        val intLength: Int,
        val fractionLength: Int
    )
}
