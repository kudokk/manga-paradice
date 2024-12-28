package jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio.AspectRatioStatus

interface AspectRatioDao {
    /**
     * @param statuses アスペクト比ステータスのリスト
     * @return 引数のステータスに合致する AspectRatio の一覧
     */
    fun selectByStatuses(statuses: Collection<AspectRatioStatus>): List<AspectRatio>

    /**
     * @param aspectRatioIds アスペクト比IDの一覧
     * @param statuses アスペクト比ステータスのリスト
     * @return 引数のアスペクト比ID・ステータスに合致する AspectRatio の一覧
     */
    fun selectByAspectRatioIdsAndStatuses(
        aspectRatioIds: Collection<AspectRatioId>,
        statuses: Collection<AspectRatioStatus>
    ): List<AspectRatio>
}
