package jp.mangaka.ssp.infrastructure.datasource.dao.spotnative

import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface SpotNativeDao {
    /**
     * @param spotNative 登録オブジェクト
     */
    fun insert(spotNative: SpotNativeInsert)

    /**
     * @param spotId 広告枠ID
     * @return 引数の広告枠IDに合致するSpotNativeまたはnull
     */
    fun selectById(spotId: SpotId): SpotNative?

    /**
     * @param spotNative 更新オブジェクト
     */
    fun update(spotNative: SpotNativeUpdate)

    /**
     * @param spotId 広告枠ID
     */
    fun deleteById(spotId: SpotId)
}
