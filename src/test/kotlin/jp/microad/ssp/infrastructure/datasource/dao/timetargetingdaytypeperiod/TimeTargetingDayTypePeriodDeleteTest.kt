package jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod.DayType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalTime

@DisplayName("TimeTargetingDayTypePeriodDeleteのテスト")
private class TimeTargetingDayTypePeriodDeleteTest {
    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val entities = listOf(
                timeTargetingDayTypePeriod(1, DayType.mon, "00:00", "23:59"),
                timeTargetingDayTypePeriod(1, DayType.tue, "00:00", "00:59"),
                timeTargetingDayTypePeriod(1, DayType.tue, "01:00", "01:59"),
                timeTargetingDayTypePeriod(2, DayType.fri, "10:30", "12:29"),
                timeTargetingDayTypePeriod(2, DayType.fri, "12:30", "14:29"),
                timeTargetingDayTypePeriod(2, DayType.fri, "14:30", "16:29")
            )

            val actual = TimeTargetingDayTypePeriodDelete.of(entities)

            assertEquals(
                listOf(
                    timeTargetingDayTypePeriodDelete(1, DayType.mon, "00:00", "23:59"),
                    timeTargetingDayTypePeriodDelete(1, DayType.tue, "00:00", "00:59"),
                    timeTargetingDayTypePeriodDelete(1, DayType.tue, "01:00", "01:59"),
                    timeTargetingDayTypePeriodDelete(2, DayType.fri, "10:30", "12:29"),
                    timeTargetingDayTypePeriodDelete(2, DayType.fri, "12:30", "14:29"),
                    timeTargetingDayTypePeriodDelete(2, DayType.fri, "14:30", "16:29")
                ),
                actual
            )
        }
    }

    private fun timeTargetingDayTypePeriod(
        timeTargetingId: Int,
        dayType: DayType,
        startTime: String,
        endTime: String
    ): TimeTargetingDayTypePeriod = mock {
        on { this.timeTargetingId } doReturn TimeTargetingId(timeTargetingId)
        on { this.dayType } doReturn dayType
        on { this.startTime } doReturn LocalTime.parse(startTime)
        on { this.endTime } doReturn LocalTime.parse(endTime)
    }

    private fun timeTargetingDayTypePeriodDelete(
        timeTargetingId: Int,
        dayType: DayType,
        startTime: String,
        endTime: String
    ): TimeTargetingDayTypePeriodDelete = TimeTargetingDayTypePeriodDelete(
        TimeTargetingId(timeTargetingId), dayType, LocalTime.parse(startTime), LocalTime.parse(endTime)
    )
}
