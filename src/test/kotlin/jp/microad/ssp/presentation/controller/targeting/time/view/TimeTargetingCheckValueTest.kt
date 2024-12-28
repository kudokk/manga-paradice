package jp.mangaka.ssp.presentation.controller.targeting.time.view

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime

@DisplayName("TimeTargetingCheckValueのテスト")
private class TimeTargetingCheckValueTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("正常")
    inner class FactoryTest {
        val dateTime1 = LocalDateTime.parse("2024-01-01T00:00:00")!!
        val dateTime2 = LocalDateTime.parse("2024-01-02T00:00:00")!!
        val dateTime3 = LocalDateTime.parse("2024-01-02T23:59:59")!!
        val dateTime4 = LocalDateTime.parse("2024-01-03T00:00:00")!!
        val dateTime5 = LocalDateTime.parse("2024-01-03T00:00:01")!!

        val timeTargeting: TimeTargeting = mock {
            on { this.updateTime } doReturn dateTime4
        }

        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("タイムターゲティングの更新日時が最新")
        fun isCorrect(
            timeTargetingDayTypePeriods: List<TimeTargetingDayTypePeriod>,
            expected: TimeTargetingCheckValue
        ) {
            val actual = TimeTargetingCheckValue.of(timeTargeting, timeTargetingDayTypePeriods)

            assertEquals(expected, actual)
        }

        private fun correctParams() = listOf(
            // タイムターゲティングの更新日時が最新
            Arguments.of(
                timeTargetingDayTypePeriods(dateTime1, dateTime2, dateTime3),
                TimeTargetingCheckValue(dateTime4, 3)
            ),
            // タイムターゲティングの更新日時と時間ターゲティング-日種別区間設定の最新作成日時が同じ
            Arguments.of(
                timeTargetingDayTypePeriods(dateTime1, dateTime2, dateTime3, dateTime4),
                TimeTargetingCheckValue(dateTime4, 4)
            ),
            // 時間ターゲティング-日種別区間設定の作成日時が最新
            Arguments.of(
                timeTargetingDayTypePeriods(dateTime1, dateTime2, dateTime3, dateTime4, dateTime5),
                TimeTargetingCheckValue(dateTime5, 5)
            )
        )
    }

    private fun timeTargetingDayTypePeriods(vararg createTimes: LocalDateTime): List<TimeTargetingDayTypePeriod> =
        createTimes.map { createTime ->
            mock {
                on { this.createTime } doReturn createTime
            }
        }
}
