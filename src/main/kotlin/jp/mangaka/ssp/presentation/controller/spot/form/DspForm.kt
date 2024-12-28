package jp.mangaka.ssp.presentation.controller.spot.form

import jp.mangaka.ssp.application.valueobject.dsp.DspId
import java.math.BigDecimal

data class DspForm(
    val dspId: DspId?,
    val bidAdjust: BigDecimal?,
    val floorCpm: BigDecimal?
)
