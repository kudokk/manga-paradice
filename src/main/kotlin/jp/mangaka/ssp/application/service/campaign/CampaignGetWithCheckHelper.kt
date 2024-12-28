package jp.mangaka.ssp.application.service.campaign

import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.CampaignStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignDao
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.springframework.stereotype.Component

@Component
class CampaignGetWithCheckHelper(
    private val campaignDao: CampaignDao
) {
    /**
     * CoアカウントID・キャンペーンID・キャンペーンステータスに合致するキャンペーンがすべて存在する場合のみ取得する.
     *
     * @param coAccountId CoアカウントID
     * @param campaignIds キャンペーンID
     * @param statuses キャンペーンステータスのリスト
     * @return 引数のCoアカウントID・キャンペーンID・キャンペーンステータスに合致する Campaign のリスト
     * @throws CompassManagerException CoアカウントID・キャンペーンID・キャンペーンステータスに合致しないキャンペーンが含まれているとき
     */
    fun getCampaignsWithCheck(
        coAccountId: CoAccountId,
        campaignIds: Collection<CampaignId>,
        statuses: Collection<CampaignStatus>
    ): List<CampaignCo> = campaignDao
        .selectByIdsAndStatuses(campaignIds, statuses)
        .filter { it.coAccountId == coAccountId }
        .also { entities ->
            val notFoundIds = campaignIds - entities.map { it.campaignId }.toSet()

            if (notFoundIds.isNotEmpty()) {
                throw CompassManagerException(
                    "キャンペーンID：$notFoundIds/CoアカウントID:$coAccountId/キャンペーンステータス：${statuses}に" +
                        "合致するエンティティが取得できませんでした。"
                )
            }
        }
}
