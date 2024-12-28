package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import java.math.BigDecimal
import java.time.LocalDate

data class SpotVideoFloorCpm(
    val spotId: SpotId,
    val aspectRatioId: AspectRatioId,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val floorCpm: BigDecimal
) {
    /**
     * @param targetDate 対象日付
     * @return 対象日付が開始・終了日時の期間内に含まれているか
     */
    fun containsPeriod(targetDate: LocalDate): Boolean =
        startDate <= targetDate && (endDate == null || endDate >= targetDate)
}
