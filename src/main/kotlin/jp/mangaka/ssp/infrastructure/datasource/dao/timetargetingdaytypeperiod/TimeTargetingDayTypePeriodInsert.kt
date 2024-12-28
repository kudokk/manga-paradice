package jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod

import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod.DayType
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm.DayTypePeriodDetailForm
import java.time.LocalTime

data class TimeTargetingDayTypePeriodInsert(
    val timeTargetingId: TimeTargetingId,
    val dayType: DayType,
    val startTime: LocalTime,
    val endTime: LocalTime
) {
    companion object {
        /**
         * ファクトリ関数
         *
         * @param timeTargetingId タイムターゲティングID
         * @param forms 登録内容のフォームのリスト
         * @return 生成した TimeTargetingDayTypePeriodInsert のインスタンスのリスト
         */
        fun of(
            timeTargetingId: TimeTargetingId,
            forms: Collection<DayTypePeriodDetailForm>
        ): List<TimeTargetingDayTypePeriodInsert> = forms.map {
            TimeTargetingDayTypePeriodInsert(
                timeTargetingId,
                it.dayType,
                it.startTime,
                it.endTime
            )
        }
    }
}
