package jp.mangaka.ssp.presentation.controller.targeting.time

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.service.targeting.time.TimeTargetingService
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod.DayType
import jp.mangaka.ssp.presentation.MockMvcUtils.mockPost
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingEditForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm.DayTypePeriodDetailForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingEditForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingStatusChangeForm
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingCheckValue
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingCreateResultView
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.time.LocalTime

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TimeTargetingPersistControllerのテスト")
private class TimeTargetingPersistControllerTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val timeTargetingId = TimeTargetingId(2)
        val countryId = CountryId(3)
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var timeTargetingService: TimeTargetingService

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("createのテスト")
    inner class CreateTest {
        val view = TimeTargetingCreateResultView(timeTargetingId)

        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("正常")
        fun isCorrect(formJson: String, form: TimeTargetingCreateForm) {
            doReturn(view).whenever(timeTargetingService).create(any(), any())

            mockPost(mockMvc, "/api/targetings/times?coAccountId=$coAccountId", formJson)
                .andExpect(status().isOk)
                .andExpect(content().json("""{ "timeTargetingId": $timeTargetingId }"""))

            verify(timeTargetingService, times(1)).create(coAccountId, form)
        }

        private fun correctParams() = listOf(
            Arguments.of(
                """
                    {
                      "basic": {
                        "timeTargetingName": "タイムターゲティング01",
                        "countryId": $countryId,
                        "description": "備考"
                      },
                      "dayTypePeriods": {
                        "isActiveHoliday": true,
                        "dayTypePeriodDetails": [
                          { "dayType": "mon", "startTime": "00:00", "endTime": "00:29" },
                          { "dayType": "tue", "startTime": "00:30", "endTime": "00:59" },
                          { "dayType": "wed", "startTime": "01:00", "endTime": "01:29" },
                          { "dayType": "thu", "startTime": "01:30", "endTime": "01:59" },
                          { "dayType": "fri", "startTime": "02:00", "endTime": "02:29" },
                          { "dayType": "sat", "startTime": "02:30", "endTime": "02:59" },
                          { "dayType": "sun", "startTime": "03:00", "endTime": "03:29" },
                          { "dayType": "hol", "startTime": "03:30", "endTime": "03:59" }
                        ]
                      },
                      "structIds": [ 1, 2, 3 ]
                    }
                """.trimIndent(),
                TimeTargetingCreateForm(
                    BasicSettingCreateForm("タイムターゲティング01", countryId, "備考"),
                    DayTypePeriodsSettingForm(
                        true,
                        listOf(
                            dayTypePeriodDetailForm(DayType.mon, "00:00", "00:29"),
                            dayTypePeriodDetailForm(DayType.tue, "00:30", "00:59"),
                            dayTypePeriodDetailForm(DayType.wed, "01:00", "01:29"),
                            dayTypePeriodDetailForm(DayType.thu, "01:30", "01:59"),
                            dayTypePeriodDetailForm(DayType.fri, "02:00", "02:29"),
                            dayTypePeriodDetailForm(DayType.sat, "02:30", "02:59"),
                            dayTypePeriodDetailForm(DayType.sun, "03:00", "03:29"),
                            dayTypePeriodDetailForm(DayType.hol, "03:30", "03:59")
                        )
                    ),
                    listOf(1, 2, 3).map { StructId(it) }
                )
            ),
            Arguments.of(
                """
                    {
                      "basic": {
                        "countryId": $countryId
                      },
                      "dayTypePeriods": {
                        "isActiveHoliday": false,
                        "dayTypePeriodDetails": []
                      },
                      "structIds": []
                    }
                """.trimIndent(),
                TimeTargetingCreateForm(
                    BasicSettingCreateForm(null, countryId, null),
                    DayTypePeriodsSettingForm(
                        false,
                        emptyList()
                    ),
                    emptyList()
                )
            )
        )
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("editのテスト")
    inner class EditTest {
        val checkValue = TimeTargetingCheckValue(LocalDateTime.parse("2024-01-01T00:00:00"), 3)

        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("正常")
        fun isCorrect(formJson: String, form: TimeTargetingEditForm) {
            mockPost(mockMvc, "/api/targetings/times/$timeTargetingId?coAccountId=$coAccountId", formJson)
                .andExpect(status().isOk)

            verify(timeTargetingService, times(1)).edit(coAccountId, timeTargetingId, form)
        }

        private fun correctParams() = listOf(
            Arguments.of(
                """
                    {
                      "basic": {
                        "timeTargetingId": $timeTargetingId,
                        "timeTargetingName": "タイムターゲティング01",
                        "timeTargetingStatus": "active",
                        "countryId": $countryId,
                        "description": "備考"
                      },
                      "dayTypePeriods": {
                        "isActiveHoliday": true,
                        "dayTypePeriodDetails": [
                          { "dayType": "mon", "startTime": "00:00", "endTime": "00:29" },
                          { "dayType": "tue", "startTime": "00:30", "endTime": "00:59" },
                          { "dayType": "wed", "startTime": "01:00", "endTime": "01:29" },
                          { "dayType": "thu", "startTime": "01:30", "endTime": "01:59" },
                          { "dayType": "fri", "startTime": "02:00", "endTime": "02:29" },
                          { "dayType": "sat", "startTime": "02:30", "endTime": "02:59" },
                          { "dayType": "sun", "startTime": "03:00", "endTime": "03:29" },
                          { "dayType": "hol", "startTime": "03:30", "endTime": "03:59" }
                        ]
                      },
                      "structIds": [ 1, 2, 3 ],
                      "checkValue": { "updateTime": "2024-01-01 00:00:00", "count": 3 }
                    }
                """.trimIndent(),
                TimeTargetingEditForm(
                    BasicSettingEditForm("タイムターゲティング01", countryId, "備考", TimeTargetingStatus.active),
                    DayTypePeriodsSettingForm(
                        true,
                        listOf(
                            dayTypePeriodDetailForm(DayType.mon, "00:00", "00:29"),
                            dayTypePeriodDetailForm(DayType.tue, "00:30", "00:59"),
                            dayTypePeriodDetailForm(DayType.wed, "01:00", "01:29"),
                            dayTypePeriodDetailForm(DayType.thu, "01:30", "01:59"),
                            dayTypePeriodDetailForm(DayType.fri, "02:00", "02:29"),
                            dayTypePeriodDetailForm(DayType.sat, "02:30", "02:59"),
                            dayTypePeriodDetailForm(DayType.sun, "03:00", "03:29"),
                            dayTypePeriodDetailForm(DayType.hol, "03:30", "03:59")
                        )
                    ),
                    listOf(1, 2, 3).map { StructId(it) },
                    checkValue
                )
            ),
            Arguments.of(
                """
                    {
                      "basic": {
                        "timeTargetingId": $timeTargetingId,
                        "timeTargetingStatus": "archive",
                        "countryId": $countryId
                      },
                      "dayTypePeriods": {
                        "isActiveHoliday": false,
                        "dayTypePeriodDetails": []
                      },
                      "structIds": [],
                      "checkValue": { "updateTime": "2024-01-01 00:00:00", "count": 3 }
                    }
                """.trimIndent(),
                TimeTargetingEditForm(
                    BasicSettingEditForm(null, countryId, null, TimeTargetingStatus.archive),
                    DayTypePeriodsSettingForm(
                        false,
                        emptyList()
                    ),
                    emptyList(),
                    checkValue
                )
            )
        )
    }

    @Nested
    @DisplayName("editTimeTargetingStatusのテスト")
    inner class EditTimeTargetingStatusTest {
        val form = TimeTargetingStatusChangeForm(
            TimeTargetingStatus.archive,
            TimeTargetingCheckValue(LocalDateTime.parse("2024-01-01T00:00:00"), 3)
        )
        val formJson = """
            {
              "timeTargetingStatus": "archive",
              "checkValue": { "updateTime": "2024-01-01 00:00:00", "count": 3 }
            }
        """.trimIndent()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            mockPost(mockMvc, "/api/targetings/times/$timeTargetingId/status?coAccountId=$coAccountId", formJson)
                .andExpect(status().isOk)

            verify(timeTargetingService, times(1)).editTimeTargetingStatus(coAccountId, timeTargetingId, form)
        }
    }

    private fun dayTypePeriodDetailForm(dayType: DayType, startTime: String, endTime: String) =
        DayTypePeriodDetailForm(dayType, LocalTime.parse(startTime), LocalTime.parse(endTime))
}
