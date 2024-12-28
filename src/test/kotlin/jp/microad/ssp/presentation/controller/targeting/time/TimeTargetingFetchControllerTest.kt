package jp.mangaka.ssp.presentation.controller.targeting.time

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.service.targeting.time.TimeTargetingViewService
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.PureadsType
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod.DayType
import jp.mangaka.ssp.presentation.MockMvcUtils.mockGet
import jp.mangaka.ssp.presentation.controller.common.view.CountrySelectElementView
import jp.mangaka.ssp.presentation.controller.common.view.StructSelectElementView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.CountriesView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.DayTypePeriodDetailView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingCheckValue
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingDetailView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingDetailView.BasicSettingView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingDetailView.DayTypePeriodsSettingView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingListElementView
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
@DisplayName("TimeTargetingFetchControllerのテスト")
private class TimeTargetingFetchControllerTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val timeTargetingId = TimeTargetingId(2)
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var timeTargetingViewService: TimeTargetingViewService

    @Nested
    @DisplayName("getTimeTargetingsのテスト")
    inner class GetTimeTargetingsTest {
        val views = listOf(
            timeTargetingView(
                1, "time1", TimeTargetingStatus.active, true,
                listOf(
                    dayTypePeriodDetailView(DayType.mon, "00:00", "23:59")
                ),
                timeTargetingCheckValue("2024-01-01T00:00:00", 1)
            ),
            timeTargetingView(
                2, "time2", TimeTargetingStatus.active, false,
                listOf(
                    dayTypePeriodDetailView(DayType.tue, "00:00", "00:59"),
                    dayTypePeriodDetailView(DayType.wed, "01:00", "01:59"),
                    dayTypePeriodDetailView(DayType.thu, "02:00", "02:59"),
                    dayTypePeriodDetailView(DayType.fri, "03:00", "03:59")
                ),
                timeTargetingCheckValue("2024-01-02T00:00:00", 4)
            ),
            timeTargetingView(
                3, "time3", TimeTargetingStatus.archive, false,
                listOf(
                    dayTypePeriodDetailView(DayType.sat, "00:00", "00:29"),
                    dayTypePeriodDetailView(DayType.sun, "00:30", "00:59"),
                    dayTypePeriodDetailView(DayType.sun, "01:00", "01:29"),
                    dayTypePeriodDetailView(DayType.hol, "01:30", "01:59"),
                    dayTypePeriodDetailView(DayType.hol, "02:00", "02:29"),
                    dayTypePeriodDetailView(DayType.hol, "02:30", "02:59")
                ),
                timeTargetingCheckValue("2024-01-03T00:00:00", 6)
            )
        )
        val responseJson = """
            [
              {
                "timeTargetingId": 1,
                "timeTargetingName": "time1",
                "timeTargetingStatus": "active",
                "isActiveHoliday": true,
                "dayTypePeriodDetails": [
                  { "dayType": "mon", "startTime": "00:00", "endTime": "23:59" }
                ],
                "checkValue": { "updateTime": "2024-01-01 00:00:00", "count": 1 }
              },
              {
                "timeTargetingId": 2,
                "timeTargetingName": "time2",
                "timeTargetingStatus": "active",
                "isActiveHoliday": false,
                "dayTypePeriodDetails": [
                  { "dayType": "tue", "startTime": "00:00", "endTime": "00:59" },
                  { "dayType": "wed", "startTime": "01:00", "endTime": "01:59" },
                  { "dayType": "thu", "startTime": "02:00", "endTime": "02:59" },
                  { "dayType": "fri", "startTime": "03:00", "endTime": "03:59" }
                ],
                "checkValue": { "updateTime": "2024-01-02 00:00:00", "count": 4 }
              },
              {
                "timeTargetingId": 3,
                "timeTargetingName": "time3",
                "timeTargetingStatus": "archive",
                "isActiveHoliday": false,
                "dayTypePeriodDetails": [
                  { "dayType": "sat", "startTime": "00:00", "endTime": "00:29" },
                  { "dayType": "sun", "startTime": "00:30", "endTime": "00:59" },
                  { "dayType": "sun", "startTime": "01:00", "endTime": "01:29" },
                  { "dayType": "hol", "startTime": "01:30", "endTime": "01:59" },
                  { "dayType": "hol", "startTime": "02:00", "endTime": "02:29" },
                  { "dayType": "hol", "startTime": "02:30", "endTime": "02:59" }
                ],
                "checkValue": { "updateTime": "2024-01-03 00:00:00", "count": 6 }
              }
            ]
        """.trimIndent()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(views).whenever(timeTargetingViewService).getTimeTargetingViews(any(), any())

            mockGet(mockMvc, "/api/targetings/times?coAccountId=$coAccountId&pageNo=3")
                .andExpect(status().isOk)
                .andExpect(content().json(responseJson))

            verify(timeTargetingViewService, times(1)).getTimeTargetingViews(coAccountId, 3)
        }

        private fun timeTargetingView(
            timeTargetingId: Int,
            timeTargetingName: String,
            timeTargetingStatus: TimeTargetingStatus,
            isActiveHoliday: Boolean,
            dayTypePeriodDetails: List<DayTypePeriodDetailView>,
            checkValue: TimeTargetingCheckValue
        ) = TimeTargetingListElementView(
            TimeTargetingId(timeTargetingId),
            timeTargetingName,
            timeTargetingStatus,
            isActiveHoliday,
            dayTypePeriodDetails,
            checkValue
        )

        private fun dayTypePeriodDetailView(dayType: DayType, startTime: String, endTime: String) =
            DayTypePeriodDetailView(dayType, LocalTime.parse(startTime), LocalTime.parse(endTime))

        private fun timeTargetingCheckValue(updateTime: String, count: Int) =
            TimeTargetingCheckValue(LocalDateTime.parse(updateTime), count)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getTimeTargetingのテスト")
    inner class GetTimeTargetingTest {
        val countryView = CountrySelectElementView(CountryId(1), "ja1", "en1", "kr1")
        val countryJson = """
            { "countryId": 1, "countryNameJa": "ja1", "countryNameEn": "en1", "countryNameKr": "kr1" }
        """.trimIndent()
        val checkValue = TimeTargetingCheckValue(LocalDateTime.of(2024, 1, 1, 0, 0, 0), 3)
        val checkValueJson = """{ "updateTime": "2024-01-01 00:00:00", "count": 3 }"""

        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("正常")
        fun isCorrect(
            basicView: BasicSettingView,
            basicJson: String,
            dayTypePeriodsView: DayTypePeriodsSettingView,
            dayTypePeriodsJson: String,
            structsView: List<StructSelectElementView>,
            structsJson: String
        ) {
            val view = TimeTargetingDetailView(basicView, dayTypePeriodsView, structsView, checkValue)
            doReturn(view).whenever(timeTargetingViewService).getTimeTargetingView(any(), any())

            val responseJson = """
                {
                  "basic": $basicJson,
                  "dayTypePeriods": $dayTypePeriodsJson,
                  "structs": $structsJson,
                  "checkValue": $checkValueJson
                }
            """.trimIndent()

            mockGet(mockMvc, "/api/targetings/times/${basicView.timeTargetingId}?coAccountId=$coAccountId")
                .andExpect(status().isOk)
                .andExpect(content().json(responseJson))

            verify(timeTargetingViewService, times(1)).getTimeTargetingView(coAccountId, basicView.timeTargetingId)
        }

        private fun correctParams() = listOf(
            // 必須項目のみ
            Arguments.of(
                BasicSettingView(TimeTargetingId(1), "time1", TimeTargetingStatus.active, null, countryView),
                """
                    {
                      "timeTargetingId": 1,
                      "timeTargetingName": "time1",
                      "timeTargetingStatus": "active",
                      "country": $countryJson
                    }
                """.trimIndent(),
                DayTypePeriodsSettingView(
                    true,
                    listOf(
                        dayTypePeriodDetailView(DayType.mon, "00:00", "00:29"),
                        dayTypePeriodDetailView(DayType.tue, "00:30", "00:59"),
                        dayTypePeriodDetailView(DayType.wed, "01:00", "01:29"),
                        dayTypePeriodDetailView(DayType.thu, "01:30", "01:59"),
                        dayTypePeriodDetailView(DayType.fri, "02:00", "02:29")
                    )
                ),
                """
                    {
                      "isActiveHoliday": true,
                      "dayTypePeriodDetails": [
                        { "dayType": "mon", "startTime": "00:00", "endTime": "00:29" },
                        { "dayType": "tue", "startTime": "00:30", "endTime": "00:59" },
                        { "dayType": "wed", "startTime": "01:00", "endTime": "01:29" },
                        { "dayType": "thu", "startTime": "01:30", "endTime": "01:59" },
                        { "dayType": "fri", "startTime": "02:00", "endTime": "02:29" },
                      ]
                    }
                """.trimIndent(),
                emptyList<StructSelectElementView>(),
                "[]"
            ),
            // 任意項目入力あり
            Arguments.of(
                BasicSettingView(TimeTargetingId(2), "time2", TimeTargetingStatus.archive, "desc2", countryView),
                """
                    {
                      "timeTargetingId": 2,
                      "timeTargetingName": "time2",
                      "timeTargetingStatus": "archive",
                      "country": $countryJson
                    }
                """.trimIndent(),
                DayTypePeriodsSettingView(
                    true,
                    listOf(
                        dayTypePeriodDetailView(DayType.sat, "12:00", "13:59"),
                        dayTypePeriodDetailView(DayType.sun, "14:00", "15:59"),
                        dayTypePeriodDetailView(DayType.hol, "16:00", "17:59"),
                        dayTypePeriodDetailView(DayType.hol, "18:00", "19:59")
                    )
                ),
                """
                    {
                      "isActiveHoliday": true,
                      "dayTypePeriodDetails": [
                        { "dayType": "sat", "startTime": "12:00", "endTime": "13:59" },
                        { "dayType": "sun", "startTime": "14:00", "endTime": "15:59" },
                        { "dayType": "hol", "startTime": "16:00", "endTime": "17:59" },
                        { "dayType": "hol", "startTime": "18:00", "endTime": "19:59" }
                      ]
                    }
                """.trimIndent(),
                listOf(
                    structSelectElementView(
                        1,
                        "struct1",
                        StructStatus.active,
                        PureadsType.bid,
                        "2024-01-01T00:00:00",
                        "2024-01-20T23:59:59"
                    ),
                    structSelectElementView(
                        2,
                        "struct2",
                        StructStatus.pause,
                        PureadsType.commit,
                        "2024-02-01T00:00:00",
                        "2024-02-20T23:59:59"
                    ),
                    structSelectElementView(
                        3,
                        "struct3",
                        StructStatus.stop,
                        PureadsType.filler,
                        "2024-03-01T00:00:00",
                        "2024-03-20T23:59:59"
                    )
                ),
                """
                    [
                      {
                        "structId": 1,
                        "structName": "struct1",
                        "pureAdType": "bid",
                        "structStatus": "active",
                        "startTime": "2024-01-01 00:00:00",
                        "endTime": "2024-01-20 23:59:59"
                      },
                      {
                        "structId": 2,
                        "structName": "struct2",
                        "pureAdType": "commit",
                        "structStatus": "pause",
                        "startTime": "2024-02-01 00:00:00",
                        "endTime": "2024-02-20 23:59:59"
                      },
                      {
                        "structId": 3,
                        "structName": "struct3",
                        "pureAdType": "filler",
                        "structStatus": "stop",
                        "startTime": "2024-03-01 00:00:00",
                        "endTime": "2024-03-20 23:59:59"
                      }
                    ]
                """.trimIndent()
            )
        )

        private fun dayTypePeriodDetailView(dayType: DayType, startTime: String, endTime: String) =
            DayTypePeriodDetailView(dayType, LocalTime.parse(startTime), LocalTime.parse(endTime))

        private fun structSelectElementView(
            structId: Int,
            structName: String,
            structStatus: StructStatus,
            pureAdType: PureadsType,
            startTime: String,
            endTime: String
        ) = StructSelectElementView(
            StructId(structId),
            structName,
            structStatus,
            pureAdType,
            LocalDateTime.parse(startTime),
            LocalDateTime.parse(endTime)
        )
    }

    @Nested
    @DisplayName("getCountriesのテスト")
    inner class GetCountriesTest {
        val view = CountriesView(
            CountryId(1),
            listOf(
                countryView(1, "ja1", "en1", "kr1"),
                countryView(2, "ja2", "en2", "kr2"),
                countryView(3, "ja3", "en3", "kr3")
            )
        )
        val responseJson = """
            {
              "coAccountCountryId": 1,
              "countries": [
                { "countryId": 1, "countryNameJa": "ja1", "countryNameEn": "en1", "countryNameKr": "kr1" },
                { "countryId": 2, "countryNameJa": "ja2", "countryNameEn": "en2", "countryNameKr": "kr2" },
                { "countryId": 3, "countryNameJa": "ja3", "countryNameEn": "en3", "countryNameKr": "kr3" }
              ]
            }
        """.trimIndent()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(view).whenever(timeTargetingViewService).getCountriesView(any())

            mockGet(mockMvc, "/api/targetings/times/forms/countries?coAccountId=$coAccountId")
                .andExpect(status().isOk)
                .andExpect(content().json(responseJson))

            verify(timeTargetingViewService, times(1)).getCountriesView(coAccountId)
        }

        private fun countryView(countryId: Int, countryNameJa: String, countryNameEn: String, countryNameKr: String) =
            CountrySelectElementView(CountryId(countryId), countryNameJa, countryNameEn, countryNameKr)
    }

    @Nested
    @DisplayName("getStructsのテスト")
    inner class GetStructsTest {
        val views = listOf(
            structView(1, "st1", StructStatus.active, PureadsType.bid, "2024-01-01T00:00:00", "2024-01-11T23:59:59"),
            structView(2, "st2", StructStatus.stop, PureadsType.commit, "2024-01-02T00:00:00", "2024-01-12T23:59:59"),
            structView(3, "st3", StructStatus.pause, PureadsType.filler, "2024-01-03T00:00:00", "2024-01-13T23:59:59")
        )
        val responseJson = """
            [
              {
                "structId": 1,
                "structName": "st1",
                "structStatus": "active",
                "pureAdType": "bid",
                "startTime": "2024-01-01 00:00:00",
                "endTime": "2024-01-11 23:59:59"
              },
              {
                "structId": 2,
                "structName": "st2",
                "structStatus": "stop",
                "pureAdType": "commit",
                "startTime": "2024-01-02 00:00:00",
                "endTime": "2024-01-12 23:59:59"
              },
              {
                "structId": 3,
                "structName": "st3",
                "structStatus": "pause",
                "pureAdType": "filler",
                "startTime": "2024-01-03 00:00:00",
                "endTime": "2024-01-13 23:59:59"
              }
            ]
        """.trimIndent()

        @Test
        @DisplayName("正常 - タイムターゲティングID指定なし")
        fun isCorrectWithoutTimeTargetingId() {
            doReturn(views).whenever(timeTargetingViewService).getStructViews(any(), anyOrNull())

            mockGet(mockMvc, "/api/targetings/times/forms/structs?coAccountId=$coAccountId")
                .andExpect(status().isOk)
                .andExpect(content().json(responseJson))

            verify(timeTargetingViewService, times(1)).getStructViews(coAccountId, null)
        }

        @Test
        @DisplayName("正常 - タイムターゲティングID指定あり")
        fun isCorrectWithTimeTargetingId() {
            doReturn(views).whenever(timeTargetingViewService).getStructViews(any(), anyOrNull())

            val uri = "/api/targetings/times/forms/structs?coAccountId=$coAccountId&timeTargetingId=$timeTargetingId"
            mockGet(mockMvc, uri)
                .andExpect(status().isOk)
                .andExpect(content().json(responseJson))

            verify(timeTargetingViewService, times(1)).getStructViews(coAccountId, timeTargetingId)
        }

        private fun structView(
            structId: Int,
            structName: String,
            structStatus: StructStatus,
            pureadsType: PureadsType,
            startTime: String,
            endTime: String
        ) = StructSelectElementView(
            StructId(structId),
            structName,
            structStatus,
            pureadsType,
            LocalDateTime.parse(startTime),
            LocalDateTime.parse(endTime)
        )
    }
}
