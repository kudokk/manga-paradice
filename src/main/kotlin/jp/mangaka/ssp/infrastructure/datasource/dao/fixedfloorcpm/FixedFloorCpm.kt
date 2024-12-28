package jp.mangaka.ssp.infrastructure.datasource.dao.fixedfloorcpm

import jp.mangaka.ssp.application.valueobject.deal.DealId
import java.math.BigDecimal
import java.time.LocalDate

data class FixedFloorCpm(
    val dealId: DealId,
    val floorCpm: BigDecimal,
    val startDate: LocalDate,
    val endDate: LocalDate?
)
