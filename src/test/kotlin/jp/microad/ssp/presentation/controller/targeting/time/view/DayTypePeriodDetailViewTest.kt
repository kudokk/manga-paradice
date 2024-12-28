package jp.mangaka.ssp.presentation.controller.targeting.time.view

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod.DayType
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalTime

@DisplayName("DayTypePeriodDetailViewのテスト")
private class DayTypePeriodDetailViewTest {
    companion object {
        val timeTargetingId = TimeTargetingId(1)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = DayTypePeriodDetailView.of(
                listOf(
                    timeTargetingDayTypePeriod(timeTargetingId, DayType.mon, "00:00", "23:59"),
                    timeTargetingDayTypePeriod(timeTargetingId, DayType.tue, "00:00", "00:59"),
                    timeTargetingDayTypePeriod(timeTargetingId, DayType.tue, "01:00", "01:59")
                )
            )

            assertEquals(
                listOf(
                    dayTypePeriodDetailView(DayType.mon, "00:00", "23:59"),
                    dayTypePeriodDetailView(DayType.tue, "00:00", "00:59"),
                    dayTypePeriodDetailView(DayType.tue, "01:00", "01:59")
                ),
                actual
            )
        }

        @Test
        @DisplayName("引数のリストが空のとき")
        fun isEmptyTimeTargetingDayTypePeriods() {
            val actual = DayTypePeriodDetailView.of(emptyList())

            assertEmpty(actual)
        }

        @Test
        @DisplayName("異なるタイムターゲティングが含まれているとき")
        fun isOtherTimeTargeting() {
            assertThrows<CompassManagerException> {
                DayTypePeriodDetailView.of(
                    listOf(
                        timeTargetingDayTypePeriod(timeTargetingId, DayType.mon, "00:00", "23:59"),
                        timeTargetingDayTypePeriod(TimeTargetingId(99), DayType.tue, "00:00", "23:59")
                    )
                )
            }
        }
    }

    private fun dayTypePeriodDetailView(dayType: DayType, startTime: String, endTime: String) =
        DayTypePeriodDetailView(dayType, LocalTime.parse(startTime), LocalTime.parse(endTime))

    private fun timeTargetingDayTypePeriod(
        timeTargetingId: TimeTargetingId,
        dayType: DayType,
        startTime: String,
        endTime: String,
    ): TimeTargetingDayTypePeriod = mock {
        on { this.timeTargetingId } doReturn timeTargetingId
        on { this.dayType } doReturn dayType
        on { this.startTime } doReturn LocalTime.parse(startTime)
        on { this.endTime } doReturn LocalTime.parse(endTime)
    }
}
