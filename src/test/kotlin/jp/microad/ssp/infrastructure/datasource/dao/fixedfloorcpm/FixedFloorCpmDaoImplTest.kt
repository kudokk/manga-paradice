package jp.mangaka.ssp.infrastructure.datasource.dao.fixedfloorcpm

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.deal.DealId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
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
import java.math.BigDecimal
import java.time.LocalDate

@DisplayName("FixedFloorCpmDaoImplのテスト")
private class FixedFloorCpmDaoImplTest {
    companion object {
        val dealId1 = DealId(1)
        val dealId2 = DealId(2)
        val dealId3 = DealId(3)
        val dealId99 = DealId(99)
    }

    @Nested
    @DatabaseSetup("/dataset/FixedFloorCpm/setup.xml")
    @DisplayName("selectByIds")
    inner class SelectByIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectByIds(listOf(dealId1, dealId2, dealId3))

            assertEqualsInAnyOrder(
                listOf(
                    FixedFloorCpm(
                        dealId1, BigDecimal("123.45600000"), LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 1)
                    ),
                    FixedFloorCpm(
                        dealId2, BigDecimal("234.56700000"), LocalDate.of(2024, 1, 2), LocalDate.of(2024, 2, 2)
                    ),
                    FixedFloorCpm(
                        dealId3, BigDecimal("345.67800000"), LocalDate.of(2024, 1, 3), null
                    )
                ),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectByIds(listOf(dealId99))

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のIDリストが空")
        fun isEmptyIds() {
            val actual = sut.selectByIds(emptyList())

            assertEmpty(actual)
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(FixedFloorCpmDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: FixedFloorCpmDaoImpl
    }
}
