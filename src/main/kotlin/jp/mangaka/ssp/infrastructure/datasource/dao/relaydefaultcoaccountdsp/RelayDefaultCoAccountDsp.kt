package jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import java.math.BigDecimal

data class RelayDefaultCoAccountDsp(
    val coAccountId: CoAccountId,
    val dspId: DspId,
    val bidAdjust: BigDecimal,
    val priority: Int
)
