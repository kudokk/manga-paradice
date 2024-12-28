package jp.mangaka.ssp.infrastructure.datasource.dao.struct

import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import java.time.LocalDateTime

// struct,compass_structの複合オブジェクト
data class StructCo(
    val structId: StructId,
    val structName: String,
    val campaignId: CampaignId,
    val structStatus: StructStatus,
    val timeTargetingId: TimeTargetingId?,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val resellerFlag: Int
) {
    /**
     * @param dateTime 対象日時
     * @return 終了日時が対象日時以降の場合は true
     */
    fun isNotEndBy(dateTime: LocalDateTime): Boolean = !endDate.isBefore(dateTime)

    /**
     * リセラー対象かどうか
     *
     * @return リセラー対象の場合は true
     */
    fun isReseller(): Boolean = resellerFlag == 1

    enum class StructStatus {
        stop, active, archive, pause;

        companion object {
            /** 画面表示可能なステータスのリスト */
            val viewableStatuses = listOf(active, stop, pause)
        }
    }
}
