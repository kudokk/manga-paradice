package targeting.time

import IntegrationTestBase
import IntegrationTestUtils.mockGet
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseSetups
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.nio.charset.StandardCharsets

@DisplayName("タイムターゲティング詳細の結合テスト")
private class GetTimeTargetingTest : IntegrationTestBase() {
    @Test
    @DatabaseSetups(
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Targeting/Time/setup_fetch_core_master.xml"],
            connection = "CoreMasterDS"
        ),
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Targeting/Time/setup_fetch_compass_master.xml"],
            connection = "CompassMasterDS"
        )
    )
    @DisplayName("正常")
    fun testSuccessGet() {
        val expectedJson = """
            {
              "basic": {
                "timeTargetingId": 1,
                "timeTargetingName": "タイムターゲティング1",
                "timeTargetingStatus": "active",
                "description": "備考1",
                "country": {
                    "countryId": 2,
                    "countryNameJa": "インドネシア",
                    "countryNameEn": "Indonesia",
                    "countryNameKr": "인도네시아" 
                }
              },
              "dayTypePeriods": {
                "isActiveHoliday": true,
                "dayTypePeriodDetails": [
                    { "dayType": "mon", "startTime": "00:00", "endTime": "23:59" },
                    { "dayType": "tue", "startTime": "00:00", "endTime": "23:59" },
                    { "dayType": "wed", "startTime": "00:00", "endTime": "23:59" },
                    { "dayType": "thu", "startTime": "00:00", "endTime": "23:59" },
                    { "dayType": "fri", "startTime": "00:00", "endTime": "23:59" },
                    { "dayType": "sat", "startTime": "00:00", "endTime": "23:59" },
                    { "dayType": "sun", "startTime": "00:00", "endTime": "23:59" },
                    { "dayType": "hol", "startTime": "00:00", "endTime": "23:59" }
                ]
              },
              "structs": [
                {
                  "structId": 1,
                  "structName": "ストラクト1",
                  "pureAdType": "commit",
                  "structStatus": "active",
                  "startTime": "2024-01-01 00:00:00",
                  "endTime": "2024-02-01 23:59:59"
                },
                {
                  "structId": 2,
                  "structName": "ストラクト2",
                  "pureAdType": "commit",
                  "structStatus": "stop",
                  "startTime": "2024-01-02 00:00:00",
                  "endTime": "2024-02-02 23:59:59"
                },
                {
                  "structId": 3,
                  "structName": "ストラクト3",
                  "pureAdType": "bid",
                  "structStatus": "pause",
                  "startTime": "2024-01-03 00:00:00",
                  "endTime": "2024-02-03 23:59:59"
                }
              ],
              "checkValue": { "updateTime": "2024-01-01 00:00:00", "count": 8 }
            }
        """.trimIndent()

        mockGet(mockMvc, "/api/targetings/times/1?coAccountId=20")
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(expectedJson))
    }
}
