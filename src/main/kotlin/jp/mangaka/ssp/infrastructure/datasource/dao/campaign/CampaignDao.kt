package jp.mangaka.ssp.infrastructure.datasource.dao.campaign

import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId

interface CampaignDao {
    /**
     * @param campaignIds キャンペーンIDのリスト
     * @param statuses キャンペーンステータスのリスト
     * @return 引数のキャンペーンID・ステータスに合致する Campaign のリスト
     */
    fun selectByIdsAndStatuses(
        campaignIds: Collection<CampaignId>,
        statuses: Collection<CampaignCo.CampaignStatus>
    ): List<CampaignCo>

    /**
     * Coアカウントに紐づくキャンペーンを取得する.
     *
     * @param coAccountId CoアカウントID
     * @param statuses キャンペーンステータスのリスト
     * @return 引数のCoアカウントIDに合致する Campaign のリスト
     */
    fun selectByCoAccountIdAndStatuses(
        coAccountId: CoAccountId,
        statuses: Collection<CampaignCo.CampaignStatus>
    ): List<CampaignCo>
}
