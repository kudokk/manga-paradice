package jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod

import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod.DayType
import java.time.LocalTime

data class TimeTargetingDayTypePeriodDelete(
    val timeTargetingId: TimeTargetingId,
    val dayType: DayType,
    val startTime: LocalTime,
    val endTime: LocalTime
) {
    companion object {
        /**
         * ファクトリ関数
         *
         * @param entities 登録内容のエンティティのリスト
         * @return 生成した TimeTargetingDayTypePeriodDelete のインスタンスのリスト
         */
        fun of(
            entities: Collection<TimeTargetingDayTypePeriod>
        ): List<TimeTargetingDayTypePeriodDelete> = entities.map {
            TimeTargetingDayTypePeriodDelete(
                it.timeTargetingId,
                it.dayType,
                it.startTime,
                it.endTime
            )
        }
    }
}
