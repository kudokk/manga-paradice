package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface SpotVideoFloorCpmDao {
    /**
     * @param spotVideoFloorCpms Insertオブジェクトのリスト
     */
    fun inserts(spotVideoFloorCpms: Collection<SpotVideoFloorCpmInsert>)

    /**
     * @param spotId 広告枠ID
     * @return 広告枠IDに紐づく SpotVideoFloorCpm のリスト
     */
    fun selectBySpotId(spotId: SpotId): List<SpotVideoFloorCpm>

    /**
     * @param spotVideoFloorCpms Updateオブジェクトのリスト
     */
    fun updates(spotVideoFloorCpms: Collection<SpotVideoFloorCpmUpdate>)

    /**
     * @param spotId 広告枠ID
     * @param aspectRatioIds アスペクト比IDのリスト
     */
    fun deleteBySpotIdAndAspectRatioIds(spotId: SpotId, aspectRatioIds: Collection<AspectRatioId>)
}
