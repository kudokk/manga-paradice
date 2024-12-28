package jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp

import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp.RelayDefaultCoAccountDsp
import jp.mangaka.ssp.presentation.controller.spot.form.DspForm
import java.math.BigDecimal

data class RelaySpotDspInsert(
    val spotId: SpotId,
    val dspId: DspId,
    val bidAdjust: BigDecimal,
    val priority: Int,
    val floorCpm: BigDecimal?
) {
    companion object {
        /**
         * @param spotId 広告枠ID
         * @param forms DSP設定のFormのリスト
         * @param defaultDsps デフォルトDSPのリスト
         * @return DSP設定のInsertオブジェクトのリスト
         */
        fun of(
            spotId: SpotId,
            forms: Collection<DspForm>,
            defaultDsps: Collection<RelayDefaultCoAccountDsp>
        ): List<RelaySpotDspInsert> {
            val defaultDspMap = defaultDsps.associateBy { it.dspId }

            return forms.map {
                // バリデーション後に呼び出される想定なのでnon-nullのカラムは強制キャスト
                RelaySpotDspInsert(
                    spotId,
                    it.dspId!!,
                    it.bidAdjust!!,
                    defaultDspMap[it.dspId]?.priority ?: 1,
                    it.floorCpm
                )
            }
        }

        /**
         * @param spotId 広告枠ID
         * @param defaultDsps デフォルトDSPのリスト
         * @return DSP設定のInsertオブジェクトのリスト
         */
        fun of(spotId: SpotId, defaultDsps: Collection<RelayDefaultCoAccountDsp>): List<RelaySpotDspInsert> =
            defaultDsps.map { RelaySpotDspInsert(spotId, it.dspId, it.bidAdjust, it.priority, null) }
    }
}
