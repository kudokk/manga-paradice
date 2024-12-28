package jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner

import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface SpotBannerDao {
    /**
     * @param spotBanner 広告枠バナー設定のInsertオブジェクト
     */
    fun insert(spotBanner: SpotBannerInsert)

    /**
     * @param spotId 広告枠ID
     * @return 引数の広告枠IDに合致するSpotBannerまたはnull
     */
    fun selectById(spotId: SpotId): SpotBanner?

    /**
     * @param spotId 広告枠ID
     */
    fun deleteById(spotId: SpotId)
}
