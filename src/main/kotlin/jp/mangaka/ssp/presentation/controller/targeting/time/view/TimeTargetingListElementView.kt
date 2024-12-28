package jp.mangaka.ssp.presentation.controller.targeting.time.view

import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod

data class TimeTargetingListElementView(
    val timeTargetingId: TimeTargetingId,
    val timeTargetingName: String,
    val timeTargetingStatus: TimeTargetingStatus,
    val isActiveHoliday: Boolean,
    val dayTypePeriodDetails: List<DayTypePeriodDetailView>,
    val checkValue: TimeTargetingCheckValue
) {
    companion object {
        /**
         * ファクトリ関数
         *
         * @param timeTargetings タイムターゲティングのエンティティのリスト
         * @param timeTargetingDayTypePeriods 時間ターゲティング-日種別区間設定のエンティティのリスト
         * @return 生成した TimeTargetingListElementView のインスタンスのリスト
         */
        fun of(
            timeTargetings: List<TimeTargeting>,
            timeTargetingDayTypePeriods: Collection<TimeTargetingDayTypePeriod>
        ): List<TimeTargetingListElementView> {
            if (timeTargetings.isEmpty()) return emptyList()

            val groupedDayTypePeriods = timeTargetingDayTypePeriods
                .groupBy { it.timeTargetingId }

            return timeTargetings.map {
                // タイムターゲティングには最低１つは詳細設定があるので必ず取れる想定
                val dayTypePeriods = groupedDayTypePeriods.getValue(it.timeTargetingId)

                TimeTargetingListElementView(
                    it.timeTargetingId,
                    it.timeTargetingName,
                    it.timeTargetingStatus,
                    it.isActiveHoliday,
                    DayTypePeriodDetailView.of(dayTypePeriods),
                    TimeTargetingCheckValue.of(it, dayTypePeriods)
                )
            }
        }
    }
}
