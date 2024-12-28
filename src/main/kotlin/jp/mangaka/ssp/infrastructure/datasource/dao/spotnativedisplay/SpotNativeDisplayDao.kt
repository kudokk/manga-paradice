package jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay

import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface SpotNativeDisplayDao {
    /**
     * @param spotNativeDisplay 登録オブジェクト
     */
    fun insert(spotNativeDisplay: SpotNativeDisplayInsert)

    /**
     * @param spotId 広告枠ID
     * @return 広告枠IDに合致する SpotNativeDisplay
     */
    fun selectById(spotId: SpotId): SpotNativeDisplay?

    /**
     * 広告枠に紐づく広告枠ネイティブ表示設定を取得する.
     *
     * @param spotIds 広告枠IDのリスト
     * @return 引数の広告枠IDに合致する SpotNativeDisplay のリスト
     */
    fun selectByIds(spotIds: Collection<SpotId>): List<SpotNativeDisplay>

    /**
     * @param spotNativeDisplay 更新オブジェクト
     */
    fun update(spotNativeDisplay: SpotNativeDisplayInsert)

    /**
     * @param spotId 広告枠ID
     */
    fun deleteById(spotId: SpotId)
}
