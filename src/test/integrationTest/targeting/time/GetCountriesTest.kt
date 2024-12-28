package targeting.time

import IntegrationTestBase
import IntegrationTestUtils.mockGet
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseSetups
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("タイムターゲティングの国一覧取得の結合テスト")
private class GetCountriesTest : IntegrationTestBase() {
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
    fun testSuccessGetCountries() {
        val expectedJson = """
            {
                "coAccountCountryId": 1,
                "countries": [
                    {
                        "countryId": 1,
                        "countryNameJa": "日本",
                        "countryNameEn": "Japan",
                        "countryNameKr": "일본" 
                    },
                    {
                        "countryId": 2,
                        "countryNameJa": "インドネシア",
                        "countryNameEn": "Indonesia",
                        "countryNameKr": "인도네시아" 
                    },
                    {
                        "countryId": 3,
                        "countryNameJa": "中国",
                        "countryNameEn": "China",
                        "countryNameKr": "중국" 
                    }
                ]
            }
        """.trimIndent()

        mockGet(mockMvc, "/api/targetings/times/forms/countries?coAccountId=20")
            .andExpect(status().isOk)
            .andExpect(content().json(expectedJson))
    }
}
