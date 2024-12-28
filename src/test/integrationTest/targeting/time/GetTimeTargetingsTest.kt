package targeting.time

import IntegrationTestBase
import IntegrationTestUtils.mockGet
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseSetups
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("タイムターゲティング一覧取得の結合テスト")
private class GetTimeTargetingsTest : IntegrationTestBase() {
    @Test
    @DatabaseSetups(
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Targeting/Time/setup_fetch_compass_master.xml"],
            connection = "CompassMasterDS"
        )
    )
    @DisplayName("正常")
    fun testSuccessGet() {
        val expectedJson = """
            [
                {
                    "timeTargetingId": 1,
                    "timeTargetingName": "タイムターゲティング1",
                    "timeTargetingStatus": "active",
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
                    ],
                    "checkValue": { "updateTime": "2024-01-01 00:00:00", "count": 8 }
                },
                {
                    "timeTargetingId": 2,
                    "timeTargetingName": "タイムターゲティング2",
                    "timeTargetingStatus": "archive",
                    "isActiveHoliday": false,
                    "dayTypePeriodDetails": [
                        { "dayType": "mon", "startTime": "00:00", "endTime": "00:59" },
                        { "dayType": "tue", "startTime": "01:00", "endTime": "01:59" },
                        { "dayType": "wed", "startTime": "02:00", "endTime": "02:59" }
                    ],
                    "checkValue": { "updateTime": "2024-01-02 00:00:00", "count": 3 }
                }
            ]
        """.trimIndent()

        mockGet(mockMvc, "/api/targetings/times?coAccountId=20&pageNo=0")
            .andExpect(status().isOk)
            .andExpect(content().json(expectedJson))
    }
}
