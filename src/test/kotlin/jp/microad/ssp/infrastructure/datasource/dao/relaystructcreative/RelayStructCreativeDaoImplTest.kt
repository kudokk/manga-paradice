package jp.mangaka.ssp.infrastructure.datasource.dao.relaystructcreative

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.creative.CreativeId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.config.CoreMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
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

@DisplayName("RelayStructCreativeDaoImplのテスト")
private class RelayStructCreativeDaoImplTest {
    companion object {
        val structId1 = StructId(1)
        val structId2 = StructId(2)
        val creativeId1 = CreativeId(1)
        val creativeId2 = CreativeId(2)
    }

    @Nested
    @DatabaseSetup("/dataset/RelayStructCreative/setup.xml")
    @DisplayName("selectByStructIdsのテスト")
    inner class SelectByStructIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectByStructIds(listOf(structId1, structId2))

            assertEqualsInAnyOrder(
                listOf(
                    RelayStructCreative(structId1, creativeId1, 10),
                    RelayStructCreative(structId2, creativeId1, 20),
                    RelayStructCreative(structId2, creativeId2, 30)
                ),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectByStructIds(listOf(StructId(99)))

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のストラクトIDリストが空")
        fun isEmptyStructIds() {
            val actual = sut.selectByStructIds(emptyList())

            assertEmpty(actual)
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CoreMasterDbConfig::class])
    @Import(RelayStructCreativeDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CoreMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: RelayStructCreativeDaoImpl
    }
}
