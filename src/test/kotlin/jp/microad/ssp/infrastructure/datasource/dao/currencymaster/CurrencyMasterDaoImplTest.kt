package jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.infrastructure.datasource.config.CoreMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener

@DisplayName("CurrencyMasterDaoImplのテスト")
private class CurrencyMasterDaoImplTest {
    val currency1 = CurrencyMaster(CurrencyId(1), "JPY")
    val currency2 = CurrencyMaster(CurrencyId(2), "USD")
    val currency3 = CurrencyMaster(CurrencyId(3), "CNY")

    @Nested
    @DatabaseSetup("/dataset/CurrencyMaster/setup.xml")
    @DisplayName("selectByIdのテスト")
    inner class SelectByIdTest : TestBase() {
        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            assertEquals(currency2, sut.selectById(currency2.currencyId))
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            assertNull(sut.selectById(CurrencyId(99)))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/CurrencyMaster/setup.xml")
    @DisplayName("selectAllのテスト")
    inner class SelectAllTest : TestBase() {
        @Test
        @DisplayName("取得成功")
        fun isFound() {
            val actual = sut.selectAll()

            assertEquals(setOf(currency1, currency2, currency3), actual.toSet())
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = Replace.NONE)
    @ContextConfiguration(classes = [CoreMasterDbConfig::class])
    @Import(CurrencyMasterDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CoreMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: CurrencyMasterDaoImpl
    }
}
