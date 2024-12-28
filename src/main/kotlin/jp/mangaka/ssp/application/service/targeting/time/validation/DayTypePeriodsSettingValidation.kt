package jp.mangaka.ssp.application.service.targeting.time.validation

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Null
import jp.mangaka.ssp.application.service.targeting.time.validation.DayTypePeriodsSettingValidation.DayTypeDetailValidation.Period
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod.DayType
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.jetbrains.annotations.TestOnly
import java.time.LocalTime

data class DayTypePeriodsSettingValidation @TestOnly constructor(
    @field:Valid
    @field:NotEmpty(message = "Validation.List.NotEmpty")
    private val dayTypeDetails: Map<DayType, DayTypeDetailValidation>
) {
    data class DayTypeDetailValidation(private val periods: List<Period>) {
        data class Period(val startTime: LocalTime, val endTime: LocalTime)

        @Null(message = "\${validatedValue}")
        fun getPeriods(): String? = when {
            periods.any { it.endTime <= it.startTime } -> "Validation.Period.Invalid"
            hasOverlap(periods) -> "Validation.Periods.Duplicate"
            else -> null
        }

        // 重複する期間が存在するかを再帰で確認
        private fun hasOverlap(elements: List<Period>): Boolean {
            if (elements.size <= 1) return false

            val sorted = elements.sortedBy { it.startTime }
            val current = sorted[0]
            val next = sorted[1]
            return when {
                next.startTime <= current.endTime -> true
                else -> hasOverlap(sorted.subList(1, sorted.size))
            }
        }
    }

    companion object {
        /**
         * ファクトリ関数
         *
         * @param form フォーム
         * @return 生成した DayTypePeriodsSettingValidation のインスタンス
         */
        fun of(
            form: DayTypePeriodsSettingForm
        ): DayTypePeriodsSettingValidation {
            checkHoliday(form)

            return form
                .dayTypePeriodDetails
                .groupBy({ it.dayType }, { Period(it.startTime, it.endTime) })
                .mapValues { DayTypeDetailValidation(it.value) }
                .let { DayTypePeriodsSettingValidation(it) }
        }

        @TestOnly
        fun checkHoliday(form: DayTypePeriodsSettingForm) {
            if (form.isActiveHoliday) return

            if (form.dayTypePeriodDetails.any { it.dayType == DayType.hol }) {
                throw CompassManagerException("祝日判断無効で祝日の時間帯を設定することはできません。")
            }
        }
    }
}
