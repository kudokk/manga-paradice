package jp.mangaka.ssp.presentation.controller.spot.view

import com.fasterxml.jackson.annotation.JsonFormat
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.fixedfloorcpm.FixedFloorCpm
import jp.mangaka.ssp.infrastructure.datasource.dao.payment.Payment
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import java.time.LocalDate

data class FixedCpmView(
    val spotId: SpotId,
    val spotName: String,
    val fixedCpm: String?,
    val floorCpm: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate?
) {
    companion object {
        /**
         * @param spot 広告枠
         * @param payments 支払い方法のリスト
         * @param fixedFloorCpms 固定単価CPMのリスト
         * @return 固定単価CPMのViewのリスト
         */
        fun of(
            spot: Spot,
            payments: Collection<Payment>,
            fixedFloorCpms: Collection<FixedFloorCpm>
        ): List<FixedCpmView> = payments.mapNotNull { payment ->
            val fixedFloorCpm = fixedFloorCpms.firstOrNull {
                it.startDate == payment.startDate && it.endDate == payment.endDate
            } ?: return@mapNotNull null

            FixedCpmView(
                spot.spotId,
                spot.spotName,
                payment.fixedCpm?.toPlainString(),
                fixedFloorCpm.floorCpm.toPlainString(),
                fixedFloorCpm.startDate,
                fixedFloorCpm.endDate
            )
        }
    }
}
