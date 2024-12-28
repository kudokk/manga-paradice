package jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

@DisplayName("RelayDefaultCoAccountDspDaoImplのテスト")
private class RelayDefaultCoAccountDspDaoImplTest {
    companion object {
        val coAccountId = CoAccountId(1)
    }

    val relayDefaultCoAccountDsp1 = RelayDefaultCoAccountDsp(coAccountId, DspId(10), BigDecimal("0.0000000"), 20)
    val relayDefaultCoAccountDsp2 = RelayDefaultCoAccountDsp(coAccountId, DspId(11), BigDecimal("123.4560000"), 40)
    val relayDefaultCoAccountDsp3 = RelayDefaultCoAccountDsp(coAccountId, DspId(12), BigDecimal("999.9990000"), 60)

    @Nested
    @DatabaseSetup("/dataset/RelayDefaultCoAccountDsp/setup.xml")
    @DisplayName("selectByCoAccountIdのテスト")
    inner class SelectByCoAccountIdTest : TestBase() {
        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            val actual = sut.selectByCoAccountId(coAccountId)

            assertEquals(
                setOf(relayDefaultCoAccountDsp1, relayDefaultCoAccountDsp2, relayDefaultCoAccountDsp3),
                actual.toSet()
            )
        }

        @Test
        @DisplayName("対象レコードなし")
        fun isNotFound() {
            val actual = sut.selectByCoAccountId(CoAccountId(99))

            assertTrue(actual.isEmpty())
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(RelayDefaultCoAccountDspDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: RelayDefaultCoAccountDspDaoImpl
    }
}
