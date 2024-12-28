package jp.mangaka.ssp.infrastructure.datasource.dao.reservedeliveryratio

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.struct.StructId
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

@DisplayName("ReserveDeliveryRatioDaoImplのテスト")
private class ReserveDeliveryRatioDaoImplTest {
    companion object {
        val structId1 = StructId(1)
        val structId2 = StructId(2)
    }

    @Nested
    @DatabaseSetup("/dataset/ReserveDeliveryRatio/setup.xml")
    @DisplayName("selectByStructIdsのテスト")
    inner class SelectByIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectByStructIds(listOf(structId1, structId2))

            assertEqualsInAnyOrder(
                listOf(
                    ReserveDeliveryRatio(
                        1, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 1), BigDecimal("10.1230000"),
                        BigDecimal("20.12300000"), BigDecimal("30.12300000"), structId1
                    ),
                    ReserveDeliveryRatio(
                        2, LocalDate.of(2024, 1, 2), LocalDate.of(2024, 2, 2), BigDecimal("20.1230000"),
                        BigDecimal("30.12300000"), BigDecimal("40.12300000"), structId1
                    ),
                    ReserveDeliveryRatio(
                        3, LocalDate.of(2024, 1, 3), null, BigDecimal("30.1230000"), null, BigDecimal("50.12300000"),
                        structId1
                    ),
                    ReserveDeliveryRatio(
                        4, LocalDate.of(2024, 1, 4), LocalDate.of(2024, 2, 4), BigDecimal("40.1230000"),
                        BigDecimal("50.12300000"), BigDecimal("60.12300000"), structId2
                    ),
                    ReserveDeliveryRatio(
                        5, LocalDate.of(2024, 1, 5), null, BigDecimal("50.1230000"), null, BigDecimal("70.12300000"),
                        structId2
                    ),
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
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(ReserveDeliveryRatioDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: ReserveDeliveryRatioDaoImpl
    }
}
