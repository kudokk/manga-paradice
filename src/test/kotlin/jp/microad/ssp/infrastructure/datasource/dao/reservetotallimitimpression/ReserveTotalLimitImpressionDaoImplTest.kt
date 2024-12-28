package jp.mangaka.ssp.infrastructure.datasource.dao.reservetotallimitimpression

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
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
import java.time.LocalDate

@DisplayName("ReserveTotalLimitImpressionDaoImplのテスト")
private class ReserveTotalLimitImpressionDaoImplTest {
    companion object {
        val structId1 = StructId(1)
        val structId2 = StructId(2)
    }

    @Nested
    @DatabaseSetup("/dataset/ReserveTotalLimitImpression/setup.xml")
    @DisplayName("selectByStructIdsのテスト")
    inner class SelectByStructIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectByStructIds(listOf(structId1, structId2))

            assertEqualsInAnyOrder(
                listOf(
                    ReserveTotalLimitImpression(1, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 1), 100, structId1),
                    ReserveTotalLimitImpression(2, LocalDate.of(2024, 1, 2), LocalDate.of(2024, 2, 2), 200, structId1),
                    ReserveTotalLimitImpression(3, LocalDate.of(2024, 1, 3), null, 300, structId1),
                    ReserveTotalLimitImpression(4, LocalDate.of(2024, 1, 4), LocalDate.of(2024, 2, 4), 400, structId2),
                    ReserveTotalLimitImpression(5, LocalDate.of(2024, 1, 5), null, 500, structId2)
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
        @DisplayName("引数のストラクトリストが空")
        fun isEmptyStructIds() {
            val actual = sut.selectByStructIds(emptyList())

            assertEmpty(actual)
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CoreMasterDbConfig::class])
    @Import(ReserveTotalLimitImpressionDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CoreMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: ReserveTotalLimitImpressionDaoImpl
    }
}
