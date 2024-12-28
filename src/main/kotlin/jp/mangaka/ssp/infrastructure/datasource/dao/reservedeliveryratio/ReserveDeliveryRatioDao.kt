package jp.mangaka.ssp.infrastructure.datasource.dao.reservedeliveryratio

import jp.mangaka.ssp.application.valueobject.struct.StructId

interface ReserveDeliveryRatioDao {
    /**
     * @param structIds ストラクトIDのリスト
     * @return 引数のストラクトIDに紐づく ReserveDeliveryRatio のリスト
     */
    fun selectByStructIds(structIds: Collection<StructId>): List<ReserveDeliveryRatio>
}
