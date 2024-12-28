package jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.infrastructure.datasource.config.CoreMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.util.TestUtils.assertEqualsInAnyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
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

@DisplayName("CountryMasterDaoImplのテスト")
private class CountryMasterDaoImplTest {
    val country1 = CountryMaster(CountryId(1), "name1", "nameEn1", "nameKr1", BigDecimal("1.2"), 0)
    val country2 = CountryMaster(CountryId(2), "name2", "nameEn2", "nameKr2", BigDecimal("2.3"), 1)
    val country3 = CountryMaster(CountryId(3), "name3", "nameEn3", "nameKr3", BigDecimal("3.4"), 0)

    @Nested
    @DatabaseSetup("/dataset/CountryMaster/setup.xml")
    @DisplayName("selectAllのテスト")
    inner class SelectAllTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectAll()

            assertEqualsInAnyOrder(listOf(country1, country2, country3), actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/CountryMaster/setup.xml")
    @DisplayName("selectByIdのテスト")
    inner class SelectByIdTest : TestBase() {
        @Test
        @DisplayName("取得成功")
        fun isCorrect() {
            val actual = sut.selectById(country1.countryId)

            assertEquals(country1, actual)
        }

        @Test
        @DisplayName("対象データなし")
        fun isNull() {
            assertNull(sut.selectById(CountryId(99)))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/CountryMaster/setup.xml")
    @DisplayName("selectByIdsのテスト")
    inner class SelectByIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectByIds(listOf(1, 2).map { CountryId(it) })

            assertEqualsInAnyOrder(listOf(country1, country2), actual)
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CoreMasterDbConfig::class])
    @Import(CountryMasterDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CoreMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: CountryMasterDaoImpl
    }
}
