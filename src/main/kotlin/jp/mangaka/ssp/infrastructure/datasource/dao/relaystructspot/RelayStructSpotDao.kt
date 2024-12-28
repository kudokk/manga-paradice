package jp.mangaka.ssp.infrastructure.datasource.dao.relaystructspot

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId

interface RelayStructSpotDao {
    /**
     * @param spotId 広告枠ID
     * @return 引数の広告枠IDに紐づく RelayStructSpot のリスト
     */
    fun selectBySpotId(spotId: SpotId): List<RelayStructSpot>

    /**
     * 広告枠に紐づくストラクト紐づけ情報を取得
     *
     * @param spotIds 広告枠IDのリスト
     * @return 引数の広告枠IDに合致する RelayStructSpot のリスト
     */
    fun selectBySpotIds(spotIds: Collection<SpotId>): List<RelayStructSpot>

    /**
     * @param structIds ストラクトIDのリスト
     * @return 引数のストラクトIDに紐づく RelayStructSpot のリスト
     */
    fun selectByStructIds(structIds: Collection<StructId>): List<RelayStructSpot>
}
