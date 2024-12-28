package jp.mangaka.ssp.infrastructure.datasource.dao.reservetotallimitimpression

import jp.mangaka.ssp.application.valueobject.struct.StructId
import java.time.LocalDate

data class ReserveTotalLimitImpression(
    val totalSequence: Int,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val totalLimitImpression: Int,
    val structId: StructId
) {
    /**
     * @param date 対象日
     * @return 終了日が対象日以降の場合は true
     */
    fun isNotEndBy(date: LocalDate): Boolean = endDate == null || !endDate.isBefore(date)
}
