package jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod

import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId

interface TimeTargetingDayTypePeriodDao {
    /**
     * 時間ターゲティング-日種別区間設定を一括登録する.
     *
     * @param timeTargetingDayTypePeriods 登録内容のリスト
     */
    fun inserts(timeTargetingDayTypePeriods: Collection<TimeTargetingDayTypePeriodInsert>)

    /**
     * タイムターゲティングに紐づく時間ターゲティング-日種別区間設定を取得する.
     *
     * @param timeTargetingId タイムターゲティングID
     * @return 引数のタイムターゲティングIDに合致する TimeTargetingDayTypePeriod のリスト
     */
    fun selectById(timeTargetingId: TimeTargetingId): List<TimeTargetingDayTypePeriod>

    /**
     * タイムターゲティングに紐づく時間ターゲティング-日種別区間設定を取得する.
     *
     * @param timeTargetingIds タイムターゲティングIDのリスト
     * @return 引数のタイムターゲティングIDに合致する TimeTargetingDayTypePeriod のリスト
     */
    fun selectByIds(timeTargetingIds: Collection<TimeTargetingId>): List<TimeTargetingDayTypePeriod>

    /**
     * タイムターゲティングに紐づく時間ターゲティング-日種別区間設定を一括削除する.
     *
     * @param conditions 削除対象
     */
    fun deletes(conditions: Collection<TimeTargetingDayTypePeriodDelete>)
}
