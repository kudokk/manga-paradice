package jp.mangaka.ssp.infrastructure.datasource.dao.relayfixedfloorcpmspot

import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface RelayFixedFloorCpmSpotDao {
    /**
     * @param spotIds 広告枠IDのリスト
     * @return 引数の広告枠IDに合致する RelayFixedFloorCpmSpot のリスト
     */
    fun selectBySpotIds(spotIds: Collection<SpotId>): List<RelayFixedFloorCpmSpot>

    /**
     * @param spotId 広告枠ID
     * @return 引数の広告枠IDに合致する RelayFixedFloorCpmSpot のリスト
     */
    fun selectBySpotId(spotId: SpotId): List<RelayFixedFloorCpmSpot> = selectBySpotIds(listOf(spotId))
}
