package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo

import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface SpotVideoDao {
    /**
     * @param spotVideo Insertオブジェクト
     */
    fun insert(spotVideo: SpotVideoInsert)

    /**
     * @param spotId 広告枠ID
     * @return 引数の広告枠IDに合致するSpotVideoまたはnull
     */
    fun selectById(spotId: SpotId): SpotVideo?

    /**
     * @param spotVideo Updateオブジェクト
     */
    fun update(spotVideo: SpotVideoUpdate)

    /**
     * @param spotId 広告枠ID
     */
    fun deleteById(spotId: SpotId)
}
