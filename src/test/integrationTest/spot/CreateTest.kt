package spot

import IntegrationTestUtils.mockSession
import IntegrationTestUtils.mockSessionCoAccount
import IntegrationTestUtils.mockUserDetails
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseSetups
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.annotation.ExpectedDatabases
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import com.github.springtestdbunit.dataset.ReplacementDataSetLoader
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.CompassManagerApplication
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.user.UserId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassLogDbConfig
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.config.CoreMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DeliveryMethod
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm.ColorForm
import jp.mangaka.ssp.presentation.controller.spot.form.DspForm
import jp.mangaka.ssp.presentation.controller.spot.form.SizeTypeForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.BannerSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeStandardForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeVideoForm
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@SpringBootTest(classes = [CompassManagerApplication::class])
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        CoreMasterDbConfig::class,
        CompassMasterDbConfig::class,
        CompassLogDbConfig::class
    ]
)
@TestExecutionListeners(
    DependencyInjectionTestExecutionListener::class,
    DirtiesContextTestExecutionListener::class,
    TransactionDbUnitTestExecutionListener::class
)
@DbUnitConfiguration(
    databaseConnection = ["CoreMasterDS", "CompassMasterDS", "CompassLogDS"],
    dataSetLoader = ReplacementDataSetLoader::class
)
@DisplayName("広告枠作成の結合テスト")
private class CreateTest @Autowired constructor(
    val mockMvc: MockMvc,
    @Qualifier("CompassMasterJdbcTemplate") val compassJdbc: JdbcTemplate,
    @Qualifier("CoreMasterJdbcTemplate") val coreJdbc: JdbcTemplate
) {
    companion object {
        val coAccountId = CoAccountId(10)
        val userId = UserId(20)
        val siteId = SiteId(30)
    }

    val sessionCoAccount = mockSessionCoAccount(coAccountId)

    @BeforeEach
    fun before() {
        coreJdbc.execute("ALTER TABLE `size_type_info` auto_increment = 1")
        compassJdbc.execute("ALTER TABLE `spot` auto_increment = 1")
    }

    @Test
    @DatabaseSetups(
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Spot/Create/setup_core_master.xml"],
            connection = "CoreMasterDS"
        ),
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Spot/Create/setup_compass_master.xml"],
            connection = "CompassMasterDS"
        ),
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Spot/Create/setup_compass_log.xml"],
            connection = "CompassLogDS"
        )
    )
    @ExpectedDatabases(
        ExpectedDatabase(
            value = "/dataset/IntegrationTest/Spot/Create/expected_banner_core_master.xml",
            connection = "CoreMasterDS",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        ),
        ExpectedDatabase(
            value = "/dataset/IntegrationTest/Spot/Create/expected_banner_compass_master.xml",
            connection = "CompassMasterDS",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        ),
        ExpectedDatabase(
            value = "/dataset/IntegrationTest/Spot/Create/expected_banner_compass_log.xml",
            connection = "CompassLogDS",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
    )
    @DisplayName("正常 - バナー設定")
    fun isCorrectAndBanner() {
        doReturn(UserType.ma_staff).whenever(sessionCoAccount).userType

        val form = SpotCreateForm(
            BasicSettingCreateForm(
                siteId, "広告枠1", SpotStatus.active, UpstreamType.prebidjs, CurrencyId(41), DeliveryMethod.js,
                DisplayType.inline, false, false, null, "説明1", "https://test.com/1/"
            ),
            listOf(
                dspForm(1, "0.000", "0.00000000"),
                dspForm(2, "999.999", "9999999999.99999999"),
                dspForm(3, "123.456", null),
            ),
            BannerSettingForm(
                listOf(SizeTypeForm(100, 200), SizeTypeForm(300, 400), SizeTypeForm(500, 600), SizeTypeForm(700, 800)),
                false, true, null, null
            ),
            null,
            null
        )

        post(form).andExpect(status().isOk)
    }

    @Test
    @DatabaseSetups(
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Spot/Create/setup_core_master.xml"],
            connection = "CoreMasterDS"
        ),
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Spot/Create/setup_compass_master.xml"],
            connection = "CompassMasterDS"
        ),
        DatabaseSetup(
            value = ["/dataset/IntegrationTest/Spot/Create/setup_compass_log.xml"],
            connection = "CompassLogDS"
        )
    )
    @ExpectedDatabases(
        ExpectedDatabase(
            value = "/dataset/IntegrationTest/Spot/Create/setup_core_master.xml",
            connection = "CoreMasterDS",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        ),
        ExpectedDatabase(
            value = "/dataset/IntegrationTest/Spot/Create/expected_native_compass_master.xml",
            connection = "CompassMasterDS",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        ),
        ExpectedDatabase(
            value = "/dataset/IntegrationTest/Spot/Create/expected_native_compass_log.xml",
            connection = "CompassLogDS",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
    )
    @DisplayName("正常 - ネイティブ設定")
    fun isCorrectAndNative() {
        doReturn(UserType.ma_staff).whenever(sessionCoAccount).userType

        val form = SpotCreateForm(
            BasicSettingCreateForm(
                siteId, "広告枠1", SpotStatus.active, UpstreamType.none, null, DeliveryMethod.js,
                DisplayType.interstitial, false, false, null, "説明1", "https://test.com/1/"
            ),
            listOf(
                dspForm(1, "0.000", "0.00000000"),
                dspForm(2, "999.999", "9999999999.99999999"),
                dspForm(3, "123.456", null),
            ),
            null,
            NativeSettingForm(
                NativeStandardForm(
                    NativeTemplateId(1),
                    null
                ),
                NativeVideoForm(
                    NativeTemplateId(51),
                    CloseButtonForm(
                        1,
                        200,
                        ColorForm(255, 0, 0, 1.0),
                        ColorForm(0, 255, 0, 0.5),
                        ColorForm(0, 0, 255, 0.1)
                    ),
                    false
                )
            ),
            null
        )

        post(form).andExpect(status().isOk)
    }

    fun post(form: SpotCreateForm) = mockMvc.perform(
        MockMvcRequestBuilders
            .post("/api/spots?coAccountId=10")
            .with(user(mockUserDetails(userId)))
            .with {
                it.remoteAddr = "127.0.0.1"
                it
            }
            .with(csrf())
            .session(mockSession(listOf(sessionCoAccount)))
            .content(jacksonObjectMapper().writeValueAsString(form))
            .contentType(MediaType.APPLICATION_JSON)
    )

    private fun dspForm(dspId: Int, bidAdjust: String, floorCpm: String?) = DspForm(
        DspId(dspId), BigDecimal(bidAdjust), floorCpm?.let { BigDecimal(it) }
    )
}
