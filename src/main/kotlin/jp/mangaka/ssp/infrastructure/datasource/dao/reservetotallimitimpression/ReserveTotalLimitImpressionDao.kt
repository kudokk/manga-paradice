package jp.mangaka.ssp.infrastructure.datasource.dao.reservetotallimitimpression

import jp.mangaka.ssp.application.valueobject.struct.StructId

interface ReserveTotalLimitImpressionDao {
    /**
     * @param structIds ストラクトIDのリスト
     * @return 引数のストラクトIDに紐づく ReserveTotalLimitImpression のリスト
     */
    fun selectByStructIds(structIds: Collection<StructId>): List<ReserveTotalLimitImpression>
}
