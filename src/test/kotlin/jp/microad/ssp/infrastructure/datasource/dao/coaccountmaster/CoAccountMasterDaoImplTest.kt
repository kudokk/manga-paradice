package jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.infrastructure.datasource.config.CoreMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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

@DisplayName("CoAccountMasterDaoImplのテスト")
class CoAccountMasterDaoImplTest {
    val coAccount1 = coAccountMaster(1, "Coアカウント1", 1, 1)
    val coAccount2 = coAccountMaster(2, "Coアカウント2", 1, 2)
    val coAccount3 = coAccountMaster(3, "Coアカウント3", 2, 3)

    @Nested
    @DatabaseSetup("/dataset/CoAccountMaster/setup.xml")
    @DisplayName("selectCompassCoAccountsのテスト")
    inner class SelectCompassCoAccountsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectCompassCoAccounts()

            assertEquals(setOf(coAccount1, coAccount2, coAccount3), actual.toSet())
        }
    }

    @Nested
    @DatabaseSetup("/dataset/CoAccountMaster/setup.xml")
    @DisplayName("selectByCoAccountIdのテスト")
    inner class SelectByCoAccountIdTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectByCoAccountId(coAccount1.coAccountId)

            assertEquals(coAccount1, actual)
        }

        @ParameterizedTest
        @ValueSource(ints = [4, 5, 99])
        @DisplayName("対象データなし")
        fun isNotFound(coAccountId: Int) {
            val actual = sut.selectByCoAccountId(CoAccountId(coAccountId))

            assertNull(actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/CoAccountMaster/setup.xml")
    @DisplayName("selectCompassCoAccountsByCoAccountIdsのテスト")
    inner class SelectCompassCoAccountsByCoAccountIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectCompassCoAccountsByCoAccountIds(listOf(1, 3, 4, 5).map { CoAccountId(it) })

            assertEquals(setOf(coAccount1, coAccount3), actual.toSet())
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectCompassCoAccountsByCoAccountIds(listOf(CoAccountId(99)))

            assertTrue(actual.isEmpty())
        }

        @Test
        @DisplayName("引数のCoアカウントIDのリストが空")
        fun isEmptyCoAccountIds() {
            val actual = sut.selectCompassCoAccountsByCoAccountIds(emptyList())

            assertTrue(actual.isEmpty())
        }
    }

    private fun coAccountMaster(coAccountId: Int, coAccountName: String, countryId: Int, currencyId: Int) =
        CoAccountMaster(CoAccountId(coAccountId), coAccountName, CountryId(countryId), CurrencyId(currencyId))

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = Replace.NONE)
    @ContextConfiguration(classes = [CoreMasterDbConfig::class])
    @Import(CoAccountMasterDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CoreMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: CoAccountMasterDaoImpl
    }
}
