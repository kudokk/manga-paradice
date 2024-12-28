package jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay

import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface SpotBannerDisplayDao {
    /**
     * @param spotBannerDisplay 広告枠バナー表示設定のInsertオブジェクト
     */
    fun insert(spotBannerDisplay: SpotBannerDisplayInsert)

    /**
     * @param spotId 広告枠ID
     * @return 広告枠IDに合致する SpotBannerDisplay
     */
    fun selectById(spotId: SpotId): SpotBannerDisplay?

    /**
     * 広告枠に紐づく広告枠バナー表示設定を取得する.
     *
     * @param spotIds 広告枠IDのリスト
     * @return 引数の広告枠IDに合致する SpotBannerDisplay のリスト
     */
    fun selectByIds(spotIds: Collection<SpotId>): List<SpotBannerDisplay>

    /**
     * @param spotBannerDisplay 広告枠バナー表示設定のInsertオブジェクト
     */
    fun update(spotBannerDisplay: SpotBannerDisplayInsert)

    /**
     * @param spotId 広告枠ID
     */
    fun deleteById(spotId: SpotId)
}
