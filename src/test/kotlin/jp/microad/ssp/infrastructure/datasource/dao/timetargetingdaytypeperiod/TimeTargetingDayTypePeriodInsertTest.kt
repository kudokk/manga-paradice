package jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod.DayType
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm.DayTypePeriodDetailForm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalTime

@DisplayName("TimeTargetingDayTypePeriodInsertのテスト")
private class TimeTargetingDayTypePeriodInsertTest {
    companion object {
        val timeTargetingId = TimeTargetingId(1)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val forms = listOf(
                detailForm(DayType.mon, "00:00", "23:59"),
                detailForm(DayType.tue, "00:00", "00:59"),
                detailForm(DayType.tue, "01:00", "01:59"),
                detailForm(DayType.fri, "10:30", "12:29"),
                detailForm(DayType.fri, "12:30", "14:29"),
                detailForm(DayType.fri, "14:30", "16:29")
            )

            val actual = TimeTargetingDayTypePeriodInsert.of(timeTargetingId, forms)

            assertEquals(
                listOf(
                    insert(timeTargetingId, DayType.mon, "00:00", "23:59"),
                    insert(timeTargetingId, DayType.tue, "00:00", "00:59"),
                    insert(timeTargetingId, DayType.tue, "01:00", "01:59"),
                    insert(timeTargetingId, DayType.fri, "10:30", "12:29"),
                    insert(timeTargetingId, DayType.fri, "12:30", "14:29"),
                    insert(timeTargetingId, DayType.fri, "14:30", "16:29")
                ),
                actual
            )
        }
    }

    private fun detailForm(
        dayType: DayType,
        startTime: String,
        endTime: String
    ): DayTypePeriodDetailForm = mock {
        on { this.dayType } doReturn dayType
        on { this.startTime } doReturn LocalTime.parse(startTime)
        on { this.endTime } doReturn LocalTime.parse(endTime)
    }

    private fun insert(
        timeTargetingId: TimeTargetingId,
        dayType: DayType,
        startTime: String,
        endTime: String
    ): TimeTargetingDayTypePeriodInsert = TimeTargetingDayTypePeriodInsert(
        timeTargetingId, dayType, LocalTime.parse(startTime), LocalTime.parse(endTime)
    )
}
