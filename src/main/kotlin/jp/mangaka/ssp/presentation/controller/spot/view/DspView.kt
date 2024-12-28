package jp.mangaka.ssp.presentation.controller.spot.view

import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster.DspMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp.RelayDefaultCoAccountDsp

data class DspView(
    val dspId: DspId,
    val dspName: String,
    val countryIds: List<CountryId>,
    val defaultBidAdjust: String?
) {
    companion object {
        /**
         * @param dsps DSPのEntityのリスト
         * @param dspCountryIds DSPに紐づく国IDのMap
         * @param defaultDsps デフォルトのDSP設定のリスト
         * @return
         */
        fun of(
            dsps: Collection<DspMaster>,
            dspCountryIds: Map<DspId, List<CountryId>>,
            defaultDsps: Map<DspId, RelayDefaultCoAccountDsp>
        ): List<DspView> = dsps.map {
            DspView(
                it.dspId,
                it.dspName,
                dspCountryIds[it.dspId]!!,
                defaultDsps[it.dspId]?.bidAdjust?.toPlainString()
            )
        }
    }
}
