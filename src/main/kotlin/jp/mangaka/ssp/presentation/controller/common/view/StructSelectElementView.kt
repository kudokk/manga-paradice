package jp.mangaka.ssp.presentation.controller.common.view

import com.fasterxml.jackson.annotation.JsonFormat
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.PureadsType
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import java.time.LocalDateTime

// ストラクト選択の要素を表すView
data class StructSelectElementView(
    val structId: StructId,
    val structName: String,
    val structStatus: StructStatus,
    val pureAdType: PureadsType,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val startTime: LocalDateTime,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val endTime: LocalDateTime
) {
    companion object {
        /**
         * @param structs ストラクトのリスト
         * @param campaigns キャンペーンのリスト
         * @return ストラクトのViewのリスト
         */
        fun of(structs: Collection<StructCo>, campaigns: Collection<CampaignCo>): List<StructSelectElementView> {
            val campaignMap = campaigns.associateBy { it.campaignId }

            return structs.map {
                StructSelectElementView(
                    it.structId,
                    it.structName,
                    it.structStatus,
                    campaignMap.getValue(it.campaignId).pureadsType,
                    it.startDate,
                    it.endDate
                )
            }
        }
    }
}
