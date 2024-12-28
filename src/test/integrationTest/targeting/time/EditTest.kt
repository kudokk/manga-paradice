package targeting.time

import IntegrationTestBase
import IntegrationTestUtils.mockPost
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseSetups
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.annotation.ExpectedDatabases
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@DisplayName("タイムターゲティング編集の結合テスト")
private class EditTest : IntegrationTestBase() {
    @Test
    @DatabaseSetups(
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Targeting/Time/setup_persist_core_master.xml"],
            connection = "CoreMasterDS"
        ),
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Targeting/Time/setup_persist_compass_master.xml"],
            connection = "CompassMasterDS"
        ),
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Targeting/Time/setup_persist_compass_log.xml"],
            connection = "CompassLogDS"
        )
    )
    @ExpectedDatabases(
        ExpectedDatabase(
            value = "/dataset/IntegrationTest/Targeting/Time/expected_edit_compass_master.xml",
            connection = "CompassMasterDS",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        ),
        ExpectedDatabase(
            value = "/dataset/IntegrationTest/Targeting/Time/expected_edit_compass_log.xml",
            connection = "CompassLogDS",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
    )
    @DisplayName("正常")
    fun testSuccessEdit() {
        val formJson = """
            {
                "basic": {
                    "timeTargetingName": "編集テスト",
                    "countryId": 3,
                    "timeTargetingStatus": "active",
                    "description": "タイムターゲティング編集"
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
        """.trimIndent()

        mockPost(mockMvc, "/api/targetings/times/1?coAccountId=20", formJson)
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
