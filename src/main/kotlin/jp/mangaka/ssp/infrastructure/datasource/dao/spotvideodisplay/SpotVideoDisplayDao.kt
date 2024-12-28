package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface SpotVideoDisplayDao {
    /**
     * @param spotVideoDisplays Insertオブジェクトのリスト
     */
    fun inserts(spotVideoDisplays: Collection<SpotVideoDisplayInsert>)

    /**
     * @param spotId 広告枠ID
     * @return 広告枠IDに合致する SpotVideoDisplay のリスト
     */
    fun selectBySpotId(spotId: SpotId): List<SpotVideoDisplay>

    /**
     * 広告枠に紐づく広告枠ビデオ表示設定を取得する.
     *
     * @param spotIds 広告枠IDのリスト
     * @return 引数の広告枠IDに合致する SpotVideoDisplay のリスト
     */
    fun selectBySpotIds(spotIds: Collection<SpotId>): List<SpotVideoDisplay>

    /**
     * @param spotVideoDisplays SpotVideoDisplayInsertオブジェクトのリスト
     */
    fun updates(spotVideoDisplays: Collection<SpotVideoDisplayInsert>)

    /**
     * @param spotId 広告枠ID
     * @param aspectRatioIds アスペクト比IDのリスト
     */
    fun deleteBySpotIdAndAspectRatioIds(spotId: SpotId, aspectRatioIds: Collection<AspectRatioId>)
}
