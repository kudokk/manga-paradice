package jp.mangaka.ssp.presentation.controller.targeting.time.view

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod
import jp.mangaka.ssp.presentation.controller.common.view.CountrySelectElementView
import jp.mangaka.ssp.presentation.controller.common.view.StructSelectElementView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingDetailView.BasicSettingView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingDetailView.DayTypePeriodsSettingView
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import io.mockk.verify as verifyK

@DisplayName("TimeTargetingDetailViewのテスト")
private class TimeTargetingDetailViewTest {
    companion object {
        val timeTargetingId = TimeTargetingId(1)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val timeTargetingDayTypePeriods: List<TimeTargetingDayTypePeriod> = mock()
        val country: CountryMaster = mock()
        val structs: List<StructCo> = mock()
        val campaigns: List<CampaignCo> = mock()
        val countryView: CountrySelectElementView = mock()
        val dayTypePeriodDetailViews: List<DayTypePeriodDetailView> = mock()
        val structViews: List<StructSelectElementView> = mock()
        val checkValue: TimeTargetingCheckValue = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(
                CountrySelectElementView,
                DayTypePeriodDetailView,
                StructSelectElementView,
                TimeTargetingCheckValue
            )
            every { CountrySelectElementView.of(any<CountryMaster>()) } returns countryView
            every { DayTypePeriodDetailView.of(any()) } returns dayTypePeriodDetailViews
            every { StructSelectElementView.of(any(), any()) } returns structViews
            every { TimeTargetingCheckValue.of(any(), any()) } returns checkValue
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("正常")
        fun isCorrect(
            timeTargeting: TimeTargeting,
            basicView: BasicSettingView,
            dayTypePeriodsView: DayTypePeriodsSettingView
        ) {
            val actual =
                TimeTargetingDetailView.of(timeTargeting, timeTargetingDayTypePeriods, country, structs, campaigns)

            assertEquals(
                TimeTargetingDetailView(basicView, dayTypePeriodsView, structViews, checkValue),
                actual
            )
            verifyK { CountrySelectElementView.of(country) }
            verifyK { DayTypePeriodDetailView.of(timeTargetingDayTypePeriods) }
            verifyK { StructSelectElementView.of(structs, campaigns) }
            verifyK { TimeTargetingCheckValue.of(timeTargeting, timeTargetingDayTypePeriods) }
        }

        private fun correctParams() = listOf(
            Arguments.of(
                timeTargeting(timeTargetingId, "time1", TimeTargetingStatus.active, "desc", true),
                BasicSettingView(timeTargetingId, "time1", TimeTargetingStatus.active, "desc", countryView),
                DayTypePeriodsSettingView(true, dayTypePeriodDetailViews)
            ),
            Arguments.of(
                timeTargeting(timeTargetingId, "time2", TimeTargetingStatus.archive, null, false),
                BasicSettingView(timeTargetingId, "time2", TimeTargetingStatus.archive, null, countryView),
                DayTypePeriodsSettingView(false, dayTypePeriodDetailViews)
            )
        )
    }

    private fun timeTargeting(
        timeTargetingId: TimeTargetingId,
        timeTargetingName: String,
        timeTargetingStatus: TimeTargetingStatus,
        description: String?,
        isActiveHoliday: Boolean
    ): TimeTargeting = mock {
        on { this.timeTargetingId } doReturn timeTargetingId
        on { this.timeTargetingName } doReturn timeTargetingName
        on { this.timeTargetingStatus } doReturn timeTargetingStatus
        on { this.description } doReturn description
        on { this.isActiveHoliday } doReturn isActiveHoliday
    }
}
