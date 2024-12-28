package jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus

interface TimeTargetingDao {
    /**
     * タイムターゲティングを登録する.
     *
     * @param timeTargeting 登録内容
     * @return 登録したタイムターゲティングのID
     */
    fun insert(timeTargeting: TimeTargetingInsert): TimeTargetingId

    /**
     * IDに合致するタイムターゲティングを取得する.
     *
     * @param timeTargetingId タイムターゲティングID
     * @param statuses タイムターゲティングステータスのリスト
     * @return 引数のタイムターゲティングIDに合致する TimeTargeting
     */
    fun selectByIdAndStatuses(
        timeTargetingId: TimeTargetingId,
        statuses: Collection<TimeTargetingStatus>
    ): TimeTargeting?

    /**
     * Coアカウントに紐づくタイムターゲティングを取得する.
     *
     * @param coAccountId CoアカウントID
     * @param statuses タイムターゲティングステータスのリスト
     * @param limit 取得の上限数
     * @param offset 取得の起点
     * @return 引数のCoアカウントID・ステータスに合致する TimeTargeting のリスト
     */
    fun selectByCoAccountIdAndStatuses(
        coAccountId: CoAccountId,
        statuses: Collection<TimeTargetingStatus>,
        limit: Int = Int.MAX_VALUE,
        offset: Int = 0
    ): List<TimeTargeting>

    /**
     * タイムターゲティングを更新する.
     *
     * @param timeTargeting 更新内容
     */
    fun update(timeTargeting: TimeTargetingUpdate)

    /**
     * タイムターゲティングステータスを更新する.
     *
     * @param timeTargetingId タイムターゲティングID
     * @param status タイムターゲティングステータス
     */
    fun updateStatus(timeTargetingId: TimeTargetingId, status: TimeTargetingStatus)
}
