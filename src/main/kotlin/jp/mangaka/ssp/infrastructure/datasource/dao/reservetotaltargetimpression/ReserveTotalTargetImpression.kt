package jp.mangaka.ssp.infrastructure.datasource.dao.reservetotaltargetimpression

import jp.mangaka.ssp.application.valueobject.struct.StructId
import java.math.BigDecimal
import java.time.LocalDate

data class ReserveTotalTargetImpression(
    val totalSequence: Int,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val totalTargetImpression: Int,
    val totalAmount: BigDecimal,
    val structId: StructId
) {
    /**
     * @param date 対象日
     * @return 終了日が対象日以降の場合は true
     */
    fun isNotEndBy(date: LocalDate): Boolean = endDate == null || !endDate.isBefore(date)
}
