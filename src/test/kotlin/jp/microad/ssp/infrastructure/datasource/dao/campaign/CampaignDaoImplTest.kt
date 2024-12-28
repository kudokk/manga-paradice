package jp.mangaka.ssp.infrastructure.datasource.dao.campaign

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseSetups
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.config.CoreMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.CampaignStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.PureadsType
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import jp.mangaka.ssp.util.TestUtils.assertEqualsInAnyOrder
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.transaction.annotation.Transactional

@DisplayName("CampaignDaoImplのテスト")
private class CampaignDaoImplTest {
    val campaign1 = CampaignCo(CampaignId(1), CoAccountId(1), "campaign1", CampaignStatus.active, PureadsType.commit)
    val campaign2 = CampaignCo(CampaignId(2), CoAccountId(1), "campaign2", CampaignStatus.stop, PureadsType.bid)

    @Nested
    @DatabaseSetups(
        value = [
            DatabaseSetup(
                value = ["/dataset/Campaign/setup_core_master.xml"],
                connection = "CoreMasterDS"
            ),
            DatabaseSetup(
                value = ["/dataset/Campaign/setup_compass_master.xml"],
                connection = "CompassMasterDS"
            )
        ]
    )
    @DisplayName("selectByIdsAndStatuses")
    inner class SelectByIdsAndStatusesTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectByIdsAndStatuses(
                listOf(1, 2, 3, 5, 6, 99).map { CampaignId(it) },
                listOf(CampaignStatus.active, CampaignStatus.stop)
            )

            assertEqualsInAnyOrder(listOf(campaign1, campaign2), actual)
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectByIdsAndStatuses(listOf(CampaignId(99)), CampaignStatus.entries)

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のキャンペーンIDリストが空")
        fun isEmptyCampaignIds() {
            val actual = sut.selectByIdsAndStatuses(emptyList(), CampaignStatus.entries)

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のキャンペーンステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectByIdsAndStatuses(listOf(CampaignId(1)), emptyList())

            assertEmpty(actual)
        }
    }

    @Nested
    @DatabaseSetups(
        value = [
            DatabaseSetup(
                value = ["/dataset/Campaign/setup_core_master.xml"],
                connection = "CoreMasterDS"
            ),
            DatabaseSetup(
                value = ["/dataset/Campaign/setup_compass_master.xml"],
                connection = "CompassMasterDS"
            )
        ]
    )
    @DisplayName("selectByCoAccountIdAndStatusesのテスト")
    inner class SelectByCoAccountIdAndStatusesTest : TestBase() {
        @Test
        @DisplayName("取得成功")
        fun isCorrect() {
            val actual = sut.selectByCoAccountIdAndStatuses(
                CoAccountId(1),
                listOf(CampaignStatus.active, CampaignStatus.stop)
            )

            assertEqualsInAnyOrder(listOf(campaign1, campaign2), actual)
        }

        @Test
        @DisplayName("対象0件")
        fun isEmptyResult() {
            val actual = sut.selectByCoAccountIdAndStatuses(CoAccountId(99), CampaignStatus.entries)

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のキャンペーンステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectByCoAccountIdAndStatuses(CoAccountId(1), emptyList())

            assertEmpty(actual)
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(
        classes = [
            CoreMasterDbConfig::class,
            CompassMasterDbConfig::class
        ]
    )
    @Import(CampaignDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @Transactional("CoreMasterTX")
    @DbUnitConfiguration(databaseConnection = ["CoreMasterDS", "CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: CampaignDaoImpl
    }
}
