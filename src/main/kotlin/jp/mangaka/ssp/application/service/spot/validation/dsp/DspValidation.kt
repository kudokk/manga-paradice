package jp.mangaka.ssp.application.service.spot.validation.dsp

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Null
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.util.validation.ValidationUtils.DecimalConfig
import jp.mangaka.ssp.util.validation.ValidationUtils.validateBigDecimal
import java.math.BigDecimal

data class DspValidation(
    @field:NotNull(message = "Validation.Input")
    private val dspId: DspId?,
    @field:NotNull(message = "Validation.Input")
    private val bidAdjust: BigDecimal?,
    private val floorCpm: BigDecimal?
) {
    @Null(message = "\${validatedValue}")
    private fun getBidAdjust() = bidAdjust?.let { validateBigDecimal(it, bidAdjustConfig) }

    @Null(message = "\${validatedValue}")
    private fun getFloorCpm() = floorCpm?.let { validateBigDecimal(it, floorCpmConfig) }

    companion object {
        private val bidAdjustConfig = DecimalConfig(BigDecimal("0.000"), BigDecimal("999.999"), 3, 3)
        private val floorCpmConfig = DecimalConfig(BigDecimal("0.00000000"), BigDecimal("9999999999.99999999"), 10, 8)
    }
}
