package jp.mangaka.ssp.infrastructure.datasource.dao.creative

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseSetups
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.creative.CreativeId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.config.CoreMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.creative.Creative.CreativeStatus
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

@DisplayName("CreativeDaoImplのテスト")
private class CreativeDaoImplTest {
    companion object {
        val creativeId1 = CreativeId(1)
        val creativeId3 = CreativeId(3)
        val creativeId4 = CreativeId(4)
        val creativeId5 = CreativeId(5)
        val creativeId6 = CreativeId(6)
        val creativeId7 = CreativeId(7)
        val sizeTypeId1 = SizeTypeId(1)
        val sizeTypeId2 = SizeTypeId(2)
        val sizeTypeId3 = SizeTypeId(3)
    }

    val creative1 = Creative(creativeId1, CreativeStatus.active, sizeTypeId1)
    val creative3 = Creative(creativeId3, CreativeStatus.stop, sizeTypeId2)
    val creative4 = Creative(creativeId4, CreativeStatus.archive, sizeTypeId3)

    @Nested
    @DatabaseSetups(
        value = [
            DatabaseSetup(
                value = ["/dataset/Creative/setup_core_master.xml"],
                connection = "CoreMasterDS"
            ),
            DatabaseSetup(
                value = ["/dataset/Creative/setup_compass_master.xml"],
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
                listOf(creativeId1, creativeId3, creativeId4, creativeId5, creativeId6, creativeId7),
                CreativeStatus.viewableStatuses
            )

            assertEqualsInAnyOrder(actual, listOf(creative1, creative3, creative4))
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectByIdsAndStatuses(
                listOf(creativeId5, creativeId6, creativeId7),
                CreativeStatus.viewableStatuses
            )

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のクリエイティブIDリストが空")
        fun isEmptyCreativeIds() {
            val actual = sut.selectByIdsAndStatuses(
                emptyList(),
                CreativeStatus.viewableStatuses
            )

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectByIdsAndStatuses(
                listOf(creativeId1, creativeId3, creativeId4, creativeId5, creativeId6, creativeId7),
                emptyList()
            )

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
    @Import(CreativeDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @Transactional("CoreMasterTX")
    @DbUnitConfiguration(databaseConnection = ["CoreMasterDS", "CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: CreativeDaoImpl
    }
}
