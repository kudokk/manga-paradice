package jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp

import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import java.math.BigDecimal

data class RelaySpotDsp(
    val spotId: SpotId,
    val dspId: DspId,
    val bidAdjust: BigDecimal,
    val floorCpm: BigDecimal?
)
