package targeting.time

import IntegrationTestBase
import IntegrationTestUtils.mockGet
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseSetups
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("タイムターゲティングのストラクト一覧取得の結合テスト")
private class GetStructsTest : IntegrationTestBase() {
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
    fun testSuccessGetStructs() {
        val expectedJson = """
            [
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
                },
                {
                    "structId": 4,
                    "structName": "ストラクト4",
                    "pureAdType": "filler",
                    "structStatus": "active",
                    "startTime": "2024-01-04 00:00:00",
                    "endTime": "2024-02-04 23:59:59"
                }
            ]
        """.trimIndent()

        mockGet(mockMvc, "/api/targetings/times/forms/structs?coAccountId=20&timeTargetingId=1")
            .andExpect(status().isOk)
            .andExpect(content().json(expectedJson))
    }
}
