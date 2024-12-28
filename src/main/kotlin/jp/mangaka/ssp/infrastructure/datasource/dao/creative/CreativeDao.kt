package jp.mangaka.ssp.infrastructure.datasource.dao.creative

import jp.mangaka.ssp.application.valueobject.creative.CreativeId
import jp.mangaka.ssp.infrastructure.datasource.dao.creative.Creative.CreativeStatus

interface CreativeDao {
    /**
     * @param creativeIds クリエイティブIDのリスト
     * @param statuses クリエイティブステータスのリスト
     * @return 引数のクリエイティブID・クリエイティブステータスに合致する Creative のリスト
     */
    fun selectByIdsAndStatuses(
        creativeIds: Collection<CreativeId>,
        statuses: Collection<CreativeStatus>
    ): List<Creative>
}
