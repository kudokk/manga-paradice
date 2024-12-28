package jp.mangaka.ssp.infrastructure.datasource.dao.struct

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseSetups
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.config.CoreMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
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
import java.time.LocalDateTime

@DisplayName("StructDaoImplのテスト")
private class StructDaoImplTest {
    val struct1 = struct(1, "struct1", 1, StructStatus.active, 1, "2024-02-01T00:00:00", "2024-01-01T00:00:00", 0)
    val struct2 = struct(2, "struct2", 1, StructStatus.stop, 2, "2024-02-02T00:00:00", "2024-01-02T00:00:00", 1)
    val struct3 = struct(3, "struct3", 2, StructStatus.archive, null, "2024-02-03T00:00:00", "2024-01-03T00:00:00", 0)
    val struct5 = struct(5, "struct5", 2, StructStatus.active, 2, "2024-02-05T00:00:00", "2024-01-05T00:00:00", 0)
    val struct8 = struct(8, "struct8", 3, StructStatus.stop, 1, "2024-02-08T00:00:00", "2024-01-08T00:00:00", 1)
    val struct9 = struct(9, "struct9", 2, StructStatus.archive, 1, "2024-02-09T00:00:00", "2024-01-09T00:00:00", 0)

    @Nested
    @DatabaseSetups(
        value = [
            DatabaseSetup(
                value = ["/dataset/Struct/setup_core_master.xml"],
                connection = "CoreMasterDS"
            ),
            DatabaseSetup(
                value = ["/dataset/Struct/setup_compass_master.xml"],
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
                listOf(1, 2, 3, 4, 6, 7, 99).map { StructId(it) },
                listOf(StructStatus.active, StructStatus.stop, StructStatus.archive)
            )

            assertEqualsInAnyOrder(listOf(struct1, struct2, struct3), actual)
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectByIdsAndStatuses(listOf(StructId(99)), StructStatus.entries)

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のストラクトIDリストが空")
        fun isEmptyIds() {
            val actual = sut.selectByIdsAndStatuses(emptyList(), StructStatus.entries)

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のストラクトステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectByIdsAndStatuses(listOf(StructId(1)), emptyList())

            assertEmpty(actual)
        }
    }

    @Nested
    @DatabaseSetups(
        value = [
            DatabaseSetup(
                value = ["/dataset/Struct/setup_core_master.xml"],
                connection = "CoreMasterDS"
            ),
            DatabaseSetup(
                value = ["/dataset/Struct/setup_compass_master.xml"],
                connection = "CompassMasterDS"
            )
        ]
    )
    @DisplayName("selectByCampaignIdsAndStatusesのテスト")
    inner class SelectByCampaignIdsAndStatusesTest : TestBase() {
        val campaignIds = listOf(1, 2).map { CampaignId(it) }

        @Test
        @DisplayName("取得成功")
        fun isCorrect() {
            val actual = sut.selectByCampaignIdsAndStatuses(
                campaignIds,
                listOf(StructStatus.active, StructStatus.stop)
            )

            assertEqualsInAnyOrder(listOf(struct1, struct2, struct5), actual)
        }

        @Test
        @DisplayName("対象0件")
        fun isEmptyResult() {
            val actual = sut.selectByCampaignIdsAndStatuses(listOf(CampaignId(99)), StructStatus.entries)

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のキャンペーンIDリストが空")
        fun isEmptyCampaignIds() {
            val actual = sut.selectByCampaignIdsAndStatuses(emptyList(), StructStatus.entries)

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のストラクトステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectByCampaignIdsAndStatuses(campaignIds, emptyList())

            assertEmpty(actual)
        }
    }

    @Nested
    @DatabaseSetups(
        value = [
            DatabaseSetup(
                value = ["/dataset/Struct/setup_core_master.xml"],
                connection = "CoreMasterDS"
            ),
            DatabaseSetup(
                value = ["/dataset/Struct/setup_compass_master.xml"],
                connection = "CompassMasterDS"
            )
        ]
    )
    @DisplayName("selectByTimeTargetingIdAndStatusesのテスト")
    inner class SelectByTimeTargetingIdAndStatusesTest : TestBase() {
        @Test
        @DisplayName("対象あり")
        fun isFound() {
            val actual = sut.selectByTimeTargetingIdAndStatuses(
                TimeTargetingId(1),
                listOf(StructStatus.active, StructStatus.stop, StructStatus.archive)
            )

            assertEqualsInAnyOrder(listOf(struct1, struct8, struct9), actual)
        }

        @Test
        @DisplayName("対象なし")
        fun isNotFound() {
            val actual = sut.selectByTimeTargetingIdAndStatuses(TimeTargetingId(99), StructStatus.entries)

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectByTimeTargetingIdAndStatuses(TimeTargetingId(1), emptyList())

            assertEmpty(actual)
        }
    }

    private fun struct(
        structId: Int, structName: String, campaignId: Int, structStatus: StructStatus,
        timeTargetingId: Int?, startDate: String, endDate: String, resellerFlag: Int
    ) = StructCo(
        StructId(structId), structName, CampaignId(campaignId), structStatus,
        timeTargetingId?.let { TimeTargetingId(it) }, LocalDateTime.parse(startDate), LocalDateTime.parse(endDate),
        resellerFlag
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(
        classes = [
            CoreMasterDbConfig::class,
            CompassMasterDbConfig::class
        ]
    )
    @Import(StructDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @Transactional("CoreMasterTX")
    @DbUnitConfiguration(databaseConnection = ["CoreMasterDS", "CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: StructDaoImpl
    }
}
