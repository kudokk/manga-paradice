package jp.mangaka.ssp.presentation.controller.spot.view.detail

import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster.DspMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp.RelaySpotDsp
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.ReqgroupDspCountryCo

data class DspSettingListItemView(val dsp: DspView, val bidAdjust: String, val floorCpm: String?) {
    data class DspView(val dspId: DspId, val dspName: String, val countries: List<CountryView>) {
        data class CountryView(
            val countryId: CountryId,
            val countryNameJa: String,
            val countryNameEn: String,
            val countryNameKr: String
        )
    }

    companion object {
        /**
         * @param relaySpotDsps 広告枠-DSP紐づけ情報のリスト
         * @param reqgroupDspCountryCos DSP-国紐づけ情報のリスト
         * @param dsps DSPのリスト
         * @param countries 国のリスト
         * @return DSP設定要素のリスト
         */
        fun of(
            relaySpotDsps: Collection<RelaySpotDsp>,
            reqgroupDspCountryCos: Collection<ReqgroupDspCountryCo>,
            dsps: Collection<DspMaster>,
            countries: Collection<CountryMaster>
        ): List<DspSettingListItemView> {
            val dspMap = dsps.associateBy { it.dspId }
            val reqgroupDspCountryCoMap = reqgroupDspCountryCos.groupBy({ it.dspId }, { it.countryId })
            val countryMap = countries.associateBy { it.countryId }

            return relaySpotDsps.map { entity ->
                val dsp = dspMap.getValue(entity.dspId)
                val dspCountries = reqgroupDspCountryCoMap[entity.dspId]
                    // country_id=0 が含まれている場合はすべての国になるので空のリストをセット
                    ?.takeIf { !it.contains(CountryId.zero) }
                    ?.map { countryMap.getValue(it) }
                    ?: emptyList()

                DspSettingListItemView(
                    DspView(
                        dsp.dspId,
                        dsp.dspName,
                        dspCountries.map { DspView.CountryView(it.countryId, it.name, it.nameEn, it.nameKr) }
                    ),
                    entity.bidAdjust.toPlainString(),
                    entity.floorCpm?.toPlainString()
                )
            }
        }
    }
}
