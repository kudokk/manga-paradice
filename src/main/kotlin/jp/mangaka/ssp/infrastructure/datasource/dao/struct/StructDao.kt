package jp.mangaka.ssp.infrastructure.datasource.dao.struct

import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId

interface StructDao {
    /**
     * @param structIds ストラクトIDのリスト
     * @param statuses ストラクトステータスのリスト
     * @return 引数のストラクトID・ステータスに合致する StructCo のリスト
     */
    fun selectByIdsAndStatuses(structIds: Collection<StructId>, statuses: Collection<StructStatus>): List<StructCo>

    /**
     * キャンペーンに紐づくストラクトを取得する
     *
     * @param campaignIds キャンペーンIDのリスト
     * @param statuses ストラクトステータスのリスト
     * @return 引数のキャンペーンIDに合致する StructCo のリスト
     */
    fun selectByCampaignIdsAndStatuses(
        campaignIds: Collection<CampaignId>,
        statuses: Collection<StructStatus>
    ): List<StructCo>

    /**
     * タイムターゲティングに紐づくストラクトを取得する.
     *
     * @param timeTargetingId タイムターゲティングID
     * @param statuses ストラクトステータスのリスト
     * @return 引数のタイムターゲティングID・ステータスに合致する Struct のリスト
     */
    fun selectByTimeTargetingIdAndStatuses(
        timeTargetingId: TimeTargetingId,
        statuses: Collection<StructStatus>
    ): List<StructCo>
}
