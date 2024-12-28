package jp.mangaka.ssp.infrastructure.datasource.dao.campaign

import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId

// campaign,compass_campaignの複合オブジェクト
data class CampaignCo(
    val campaignId: CampaignId,
    val coAccountId: CoAccountId,
    val campaignName: String,
    val campaignStatus: CampaignStatus,
    val pureadsType: PureadsType
) {
    enum class CampaignStatus {
        stop, active, archive;

        companion object {
            /** 画面表示可能なステータスのリスト */
            val viewableStatuses = listOf(active, stop)
        }
    }

    enum class PureadsType {
        commit, bid, filler
    }
}
