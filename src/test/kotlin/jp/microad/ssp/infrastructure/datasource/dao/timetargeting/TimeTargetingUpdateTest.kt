package jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingEditForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingEditForm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("TimeTargetingUpdateのテスト")
private class TimeTargetingUpdateTest {
    companion object {
        val timeTargetingId = TimeTargetingId(1)
        val countryId = CountryId(2)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @ParameterizedTest
        @CsvSource(
            value = [
                "active,,true",
                "archive,desc,false"
            ]
        )
        @DisplayName("正常")
        fun isCorrect(
            timeTargetingStatus: TimeTargeting.TimeTargetingStatus,
            description: String?,
            isActiveHoliday: Boolean
        ) {
            val basic: BasicSettingEditForm = mock {
                on { this.timeTargetingName } doReturn "time1"
                on { this.timeTargetingStatus } doReturn timeTargetingStatus
                on { this.countryId } doReturn countryId
                on { this.description } doReturn description
            }
            val dayTypePeriods: DayTypePeriodsSettingForm = mock {
                on { this.isActiveHoliday } doReturn isActiveHoliday
            }
            val form: TimeTargetingEditForm = mock {
                on { this.basic } doReturn basic
                on { this.dayTypePeriods } doReturn dayTypePeriods
            }

            val actual = TimeTargetingUpdate.of(timeTargetingId, form)

            assertEquals(
                TimeTargetingUpdate(
                    timeTargetingId, "time1", timeTargetingStatus, countryId, isActiveHoliday, description
                ),
                actual
            )
        }
    }
}
