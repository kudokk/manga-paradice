package jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype

import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface RelaySpotSizetypeDao {
    /**
     * @param relaySpotSizetypeInserts 広告枠とサイズ種別のリレー情報のInsertオブジェクトのリスト
     */
    fun bulkInsert(relaySpotSizetypeInserts: Collection<RelaySpotSizetypeInsert>)

    /**
     * @param spotId 広告枠ID
     * @return 広告枠IDに合致する RelaySpotSizetype のリスト
     */
    fun selectBySpotId(spotId: SpotId): List<RelaySpotSizetype>

    /**
     * @param spotIds 広告枠IDのリスト
     * @return 広告枠IDに合致する RelaySpotSizetype のリスト
     */
    fun selectBySpotIds(spotIds: Collection<SpotId>): List<RelaySpotSizetype>

    /**
     * @param spotId 広告枠ID
     * @param sizeTypeIds サイズ種別ID
     */
    fun deleteBySpotIdAndSizeTypeIds(spotId: SpotId, sizeTypeIds: Collection<SizeTypeId>)
}
