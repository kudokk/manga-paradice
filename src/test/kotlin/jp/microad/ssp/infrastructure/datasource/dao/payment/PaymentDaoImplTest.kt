package jp.mangaka.ssp.infrastructure.datasource.dao.payment

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.payment.PaymentId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.payment.Payment.PaymentType
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
import java.time.LocalDateTime

@DisplayName("PaymentDaoImplのテスト")
private class PaymentDaoImplTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
        val spotId99 = SpotId(99)
        val paymentId1 = PaymentId(1)
        val paymentId2 = PaymentId(2)
        val paymentId4 = PaymentId(4)
        val coAccountId1 = CoAccountId(1)
        val coAccountId2 = CoAccountId(2)
        val siteId1 = SiteId(1)
        val siteId2 = SiteId(2)
    }

    @Nested
    @DatabaseSetup("/dataset/Payment/setup.xml")
    @DisplayName("selectBySpotIds")
    inner class SelectBySpotIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectBySpotIds(listOf(spotId1, spotId2))

            assertEqualsInAnyOrder(
                listOf(
                    Payment(
                        paymentId1, coAccountId1, siteId1, spotId1, PaymentType.revenue_share,
                        BigDecimal("123.4560"), BigDecimal("12.34560000"), LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 2, 1), LocalDateTime.of(2024, 1, 1, 0, 0, 0)
                    ),
                    Payment(
                        paymentId2, coAccountId1, siteId1, spotId2, PaymentType.fixed_cpm,
                        BigDecimal("234.5670"), BigDecimal("23.45670000"), LocalDate.of(2024, 1, 2),
                        LocalDate.of(2024, 2, 2), LocalDateTime.of(2024, 1, 2, 0, 0, 0)
                    ),
                    Payment(
                        paymentId4, coAccountId2, siteId2, spotId1, PaymentType.fixed_cpm_all,
                        null, null, LocalDate.of(2024, 1, 4), null, LocalDateTime.of(2024, 1, 4, 0, 0, 0)
                    )
                ),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectBySpotIds(listOf(spotId99))

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数の広告枠IDリストが空")
        fun isEmptySpotIds() {
            val actual = sut.selectBySpotIds(emptyList())

            assertEmpty(actual)
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(PaymentDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: PaymentDaoImpl
    }
}
