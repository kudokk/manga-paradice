package jp.mangaka.ssp.infrastructure.datasource.dao.usercoaccount

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.role.RoleId
import jp.mangaka.ssp.application.valueobject.user.UserId
import jp.mangaka.ssp.infrastructure.datasource.config.CoreMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

@DisplayName("UserCoAccountDaoImplのテスト")
private class UserCoAccountDaoImplTest {
    @Nested
    @DatabaseSetup("/dataset/UserCoAccount/setup.xml")
    @DisplayName("selectCompassUserCoAccountsByUserIdのテスト")
    inner class SelectCompassUserCoAccountsByUserIdTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectCompassUserCoAccountsByUserId(UserId(1))

            assertEquals(
                setOf(
                    userCoAccount(0, 1),
                    userCoAccount(1, 2),
                    userCoAccount(2, 3)
                ),
                actual.toSet()
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectCompassUserCoAccountsByUserId(UserId(99))

            assertTrue(actual.isEmpty())
        }
    }

    private fun userCoAccount(coAccountId: Int, roleId: Int) = UserCoAccount(
        CoAccountId(coAccountId), RoleId(roleId)
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = Replace.NONE)
    @ContextConfiguration(classes = [CoreMasterDbConfig::class])
    @Import(UserCoAccountDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CoreMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: UserCoAccountDaoImpl
    }
}
