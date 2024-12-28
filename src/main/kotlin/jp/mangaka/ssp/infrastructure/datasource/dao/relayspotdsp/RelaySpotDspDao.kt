package jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp

import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface RelaySpotDspDao {
    /**
     * @param dsps 広告枠と DSP のリレー情報のInsertオブジェクトのリスト
     */
    fun bulkInsert(dsps: Collection<RelaySpotDspInsert>)

    /**
     * @param spotId 広告枠ID
     * @return 広告枠IDに紐づく RelaySpotDsp のリスト
     */
    fun selectBySpotId(spotId: SpotId): List<RelaySpotDsp>

    /**
     * @param dsps Updateオブジェクトのリスト
     */
    fun bulkUpdate(dsps: Collection<RelaySpotDspInsert>)

    /**
     * @param spotId 広告枠ID
     * @param dspIds DSPIDのリスト
     */
    fun deleteBySpotIdAndDspIds(spotId: SpotId, dspIds: Collection<DspId>)
}
