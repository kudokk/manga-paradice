package jp.mangaka.ssp.infrastructure.datasource.dao.payment

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.payment.PaymentId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class Payment(
    val paymentId: PaymentId,
    val coAccountId: CoAccountId,
    val siteId: SiteId?,
    val spotId: SpotId?,
    val paymentType: PaymentType,
    val shareRate: BigDecimal?,
    val fixedCpm: BigDecimal?,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val updateTime: LocalDateTime
) {
    enum class PaymentType {
        revenue_share, fixed_cpm, fixed_cpm_all
    }
}
