package targeting.time

import IntegrationTestBase
import IntegrationTestUtils.mockPost
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseSetups
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.annotation.ExpectedDatabases
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("タイムターゲティング作成の結合テスト")
private class CreateTest : IntegrationTestBase() {
    @Autowired
    @Qualifier("CompassMasterJdbcTemplate")
    private lateinit var compassMasterJdbc: JdbcTemplate

    @BeforeEach
    fun beforeEach() {
        compassMasterJdbc.execute("ALTER TABLE time_targeting auto_increment = 2")
    }

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
            value = "/dataset/IntegrationTest/Targeting/Time/expected_create_compass_master.xml",
            connection = "CompassMasterDS",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        ),
        ExpectedDatabase(
            value = "/dataset/IntegrationTest/Targeting/Time/expected_create_compass_log.xml",
            connection = "CompassLogDS",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
    )
    @DisplayName("正常")
    fun testSuccessCreate() {
        val formJson = """
            {
                "basic": { "timeTargetingName": "作成テスト", "countryId": 2, "description": "タイムターゲティング作成" },
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
                "structIds": [ 1, 2 ]
            }
        """.trimIndent()

        mockPost(mockMvc, "/api/targetings/times?coAccountId=20", formJson)
            .andExpect(status().isOk)
            .andExpect(content().json("""{ "timeTargetingId": 2 }"""))
    }
}
