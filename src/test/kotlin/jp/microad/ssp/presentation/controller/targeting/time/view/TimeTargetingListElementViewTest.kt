package jp.mangaka.ssp.presentation.controller.targeting.time.view

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verifyOrder
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("TimeTargetingListElementViewのテスト")
private class TimeTargetingListElementViewTest {
    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val dayTypePeriodDetailViews: List<DayTypePeriodDetailView> = mock()
        val checkValue: TimeTargetingCheckValue = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(DayTypePeriodDetailView, TimeTargetingCheckValue)
            every { DayTypePeriodDetailView.of(any()) } returns dayTypePeriodDetailViews
            every { TimeTargetingCheckValue.of(any(), any()) } returns checkValue
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            // Setup
            val timeTargetings = listOf(
                timeTargeting(1, "time1", TimeTargetingStatus.active, true),
                timeTargeting(2, "time2", TimeTargetingStatus.active, false),
                timeTargeting(3, "time3", TimeTargetingStatus.archive, false)
            )
            // タイムターゲティングIDごとのリストのリスト
            val timeTargetingDayTypePeriods = listOf(
                List(1) { timeTargetingDayTypePeriod(1) },
                List(2) { timeTargetingDayTypePeriod(2) },
                List(3) { timeTargetingDayTypePeriod(3) }
            )

            // Exercise
            val actual = TimeTargetingListElementView.of(timeTargetings, timeTargetingDayTypePeriods.flatten())

            // Verify
            assertEquals(
                listOf(
                    timeTargetingListElementView(
                        1, "time1", TimeTargetingStatus.active, true, dayTypePeriodDetailViews, checkValue
                    ),
                    timeTargetingListElementView(
                        2, "time2", TimeTargetingStatus.active, false, dayTypePeriodDetailViews, checkValue
                    ),
                    timeTargetingListElementView(
                        3, "time3", TimeTargetingStatus.archive, false, dayTypePeriodDetailViews, checkValue
                    ),
                ),
                actual
            )
            verifyOrder {
                DayTypePeriodDetailView.of(timeTargetingDayTypePeriods[0])
                DayTypePeriodDetailView.of(timeTargetingDayTypePeriods[1])
                DayTypePeriodDetailView.of(timeTargetingDayTypePeriods[2])
            }
            verifyOrder {
                TimeTargetingCheckValue.of(timeTargetings[0], timeTargetingDayTypePeriods[0])
                TimeTargetingCheckValue.of(timeTargetings[1], timeTargetingDayTypePeriods[1])
                TimeTargetingCheckValue.of(timeTargetings[2], timeTargetingDayTypePeriods[2])
            }
        }

        @Test
        @DisplayName("引数のタイムターゲティングリストが空のとき")
        fun isEmptyTimeTargetings() {
            val actual = TimeTargetingListElementView.of(emptyList(), emptyList())

            assertEmpty(actual)
        }
    }

    private fun timeTargetingListElementView(
        timeTargetingId: Int,
        timeTargetingName: String,
        timeTargetingStatus: TimeTargetingStatus,
        isActiveHoliday: Boolean,
        dayTypePeriodDetailViews: List<DayTypePeriodDetailView>,
        checkValue: TimeTargetingCheckValue
    ) = TimeTargetingListElementView(
        TimeTargetingId(timeTargetingId),
        timeTargetingName,
        timeTargetingStatus,
        isActiveHoliday,
        dayTypePeriodDetailViews,
        checkValue
    )

    private fun timeTargeting(
        timeTargetingId: Int,
        timeTargetingName: String,
        timeTargetingStatus: TimeTargetingStatus,
        isActiveHoliday: Boolean
    ): TimeTargeting = mock {
        on { this.timeTargetingId } doReturn TimeTargetingId(timeTargetingId)
        on { this.timeTargetingName } doReturn timeTargetingName
        on { this.timeTargetingStatus } doReturn timeTargetingStatus
        on { this.isActiveHoliday } doReturn isActiveHoliday
    }

    private fun timeTargetingDayTypePeriod(timeTargetingId: Int): TimeTargetingDayTypePeriod = mock {
        on { this.timeTargetingId } doReturn TimeTargetingId(timeTargetingId)
    }
}
