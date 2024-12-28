package jp.mangaka.ssp.infrastructure.datasource.dao.fixedfloorcpm

import jp.mangaka.ssp.application.valueobject.deal.DealId

interface FixedFloorCpmDao {
    /**
     * @param dealIds 取引IDのリスト
     * @return 引数の取引IDに合致する FixedFloorCpm のリスト
     */
    fun selectByIds(dealIds: Collection<DealId>): List<FixedFloorCpm>
}
