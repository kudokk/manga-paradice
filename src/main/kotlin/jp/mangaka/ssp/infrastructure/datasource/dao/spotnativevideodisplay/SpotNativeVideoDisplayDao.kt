package jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay

import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface SpotNativeVideoDisplayDao {
    /**
     * @param spotNativeVideoDisplay 登録オブジェクト
     */
    fun insert(spotNativeVideoDisplay: SpotNativeVideoDisplayInsert)

    /**
     * @param spotId 広告枠ID
     * @return 広告枠IDに合致する SpotNativeVideoDisplay
     */
    fun selectBySpotId(spotId: SpotId): SpotNativeVideoDisplay?

    /**
     * 広告枠に紐づく広告枠ネイティブビデオ表示設定を取得する.
     *
     * @param spotIds 広告枠IDのリスト
     * @return 引数の広告枠IDに合致する SpotNativeVideoDisplay のリスト
     */
    fun selectBySpotIds(spotIds: Collection<SpotId>): List<SpotNativeVideoDisplay>

    /**
     * @param spotNativeVideoDisplay 更新オブジェクト
     */
    fun update(spotNativeVideoDisplay: SpotNativeVideoDisplayInsert)

    /**
     * @param spotId 広告枠ID
     */
    fun deleteById(spotId: SpotId)
}
