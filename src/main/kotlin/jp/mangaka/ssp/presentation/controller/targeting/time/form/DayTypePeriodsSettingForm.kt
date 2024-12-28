package jp.mangaka.ssp.presentation.controller.targeting.time.form

import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod
import java.time.LocalTime

data class DayTypePeriodsSettingForm(
    val isActiveHoliday: Boolean,
    val dayTypePeriodDetails: List<DayTypePeriodDetailForm>
) {
    data class DayTypePeriodDetailForm(
        val dayType: TimeTargetingDayTypePeriod.DayType,
        val startTime: LocalTime,
        val endTime: LocalTime
    )
}
