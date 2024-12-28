package jp.mangaka.ssp.application.service.targeting.time

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargetingDao
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.springframework.stereotype.Component

@Component
class TimeTargetingGetWithCheckHelper(
    private val timeTargetingDao: TimeTargetingDao
) {
    /**
     * CoアカウントID・タイムターゲティングID・タイムターゲティングステータスに合致するタイムターゲティングが存在する場合のみ取得する.
     *
     * @param coAccountId CoアカウントID
     * @param timeTargetingId タイムターゲティング
     * @param statuses タイムターゲティングステータスのリスト
     * @return 引数のCoアカウントID・タイムターゲティングID・タイムターゲティングステータスに合致する TimeTargeting
     * @throws CompassManagerException 条件に合致するタイムターゲティングが存在しないとき
     */
    fun getTimeTargetingWithCheck(
        coAccountId: CoAccountId,
        timeTargetingId: TimeTargetingId,
        statuses: Collection<TimeTargetingStatus>
    ): TimeTargeting = timeTargetingDao
        .selectByIdAndStatuses(timeTargetingId, statuses)
        ?.takeIf { it.coAccountId == coAccountId }
        ?: throw CompassManagerException(
            "タイムターゲティングID:$timeTargetingId/CoアカウントID:$coAccountId/タイムターゲティングステータス：$statuses" +
                "に合致するエンティティが取得できませんでした。"
        )
}
