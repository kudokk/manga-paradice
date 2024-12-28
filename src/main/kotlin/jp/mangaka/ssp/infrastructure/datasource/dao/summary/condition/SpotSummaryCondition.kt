package jp.mangaka.ssp.infrastructure.datasource.dao.summary.condition

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import java.time.LocalDate

// レポート画面の対応を行う際はstructId、creativeIdの項目を追加する
data class SpotSummaryCondition(
    val coAccountId: CoAccountId,
    val spotIds: Collection<SpotId>,
    val resellerStructIds: Collection<StructId>,
    val notResellerStructIds: Collection<StructId>,
    val relaySpotStructIds: Collection<StructId>,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    val rtbAndNotResellerStructIds: Collection<StructId> = notResellerStructIds + StructId.zero
}
