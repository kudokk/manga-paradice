package jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod

import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import java.time.LocalDateTime
import java.time.LocalTime

data class TimeTargetingDayTypePeriod(
    val timeTargetingId: TimeTargetingId,
    val dayType: DayType,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val createTime: LocalDateTime
) {
    enum class DayType {
        mon, tue, wed, thu, fri, sat, sun, hol
    }
}
