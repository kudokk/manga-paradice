package jp.mangaka.ssp.infrastructure.datasource.dao.reservetotaltargetimpression

import jp.mangaka.ssp.application.valueobject.struct.StructId

interface ReserveTotalTargetImpressionDao {
    /**
     * @param structIds ストラクトIDのリスト
     * @return 引数のストラクトIDに紐づく ReserveTotalTargetImpression のリスト
     */
    fun selectByStructIds(structIds: Collection<StructId>): List<ReserveTotalTargetImpression>
}
