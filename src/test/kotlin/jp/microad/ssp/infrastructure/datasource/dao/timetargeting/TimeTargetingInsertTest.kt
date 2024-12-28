package jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingCreateForm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("TimeTargetingInsertのテスト")
private class TimeTargetingInsertTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val countryId = CountryId(2)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @ParameterizedTest
        @CsvSource(
            value = [
                ",true",
                "desc,false"
            ]
        )
        @DisplayName("正常")
        fun isCorrect(description: String?, isActiveHoliday: Boolean) {
            val basic: BasicSettingCreateForm = mock {
                on { this.timeTargetingName } doReturn "time1"
                on { this.countryId } doReturn countryId
                on { this.description } doReturn description
            }
            val dayTypePeriods: DayTypePeriodsSettingForm = mock {
                on { this.isActiveHoliday } doReturn isActiveHoliday
            }
            val form: TimeTargetingCreateForm = mock {
                on { this.basic } doReturn basic
                on { this.dayTypePeriods } doReturn dayTypePeriods
            }

            val actual = TimeTargetingInsert.of(coAccountId, form)

            assertEquals(
                TimeTargetingInsert(coAccountId, "time1", countryId, isActiveHoliday, description),
                actual
            )
        }
    }
}
