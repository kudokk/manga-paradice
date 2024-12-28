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

@DisplayName("タイムターゲティングステータス変更の結合テスト")
private class EditTimeTargetingStatusTest : IntegrationTestBase() {
    @Test
    @DatabaseSetups(
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Targeting/Time/setup_persist_core_master.xml"],
            connection = "CoreMasterDS"
        ),
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Targeting/Time/setup_edit_time_targeting_status_compass_master.xml"],
            connection = "CompassMasterDS"
        ),
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Targeting/Time/setup_persist_compass_log.xml"],
            connection = "CompassLogDS"
        )
    )
    @ExpectedDatabases(
        ExpectedDatabase(
            value = "/dataset/IntegrationTest/Targeting/Time/expected_edit_time_targeting_status_compass_master.xml",
            connection = "CompassMasterDS",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        ),
        ExpectedDatabase(
            value = "/dataset/IntegrationTest/Targeting/Time/expected_edit_time_targeting_status_compass_log.xml",
            connection = "CompassLogDS",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
    )
    @DisplayName("正常")
    fun testSuccessEditTimeTargetingStatus() {
        val formJson = """
            {
                "timeTargetingStatus": "archive",
                "checkValue": { "updateTime": "2024-01-01 00:00:00", "count": 3 }
            }
        """.trimIndent()

        mockPost(mockMvc, "/api/targetings/times/1/status?coAccountId=20", formJson)
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
