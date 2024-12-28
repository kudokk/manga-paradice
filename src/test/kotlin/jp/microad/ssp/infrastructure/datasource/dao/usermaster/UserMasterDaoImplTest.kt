package jp.mangaka.ssp.infrastructure.datasource.dao.usermaster

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.user.UserId
import jp.mangaka.ssp.infrastructure.datasource.config.CoreMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.SoftDeleteFlag
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserClass
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
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
import java.time.LocalDateTime

@DisplayName("UserMasterDaoImplのテスト")
class UserMasterDaoImplTest {
    @Nested
    @DatabaseSetup("/dataset/UserMaster/setup.xml")
    @DisplayName("selectByMailAddressのテスト")
    inner class SelectByMailAddressTest : TestBase() {
        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            // 複数取得の関数がなくデータパターンの確認ができないので複数レコード取得して確認している
            val actual = listOf(
                "user1@mangaka.co.jp",
                "user2@mangaka.co.jp"
            ).map { sut.selectByMailAddress(it) }

            assertEquals(
                listOf(
                    userMaster(
                        1, 10, UserClass.co_account, "user1@mangaka.co.jp", "password1", "ユーザー1", "reminderurl1",
                        UserType.ma_staff, "備考1", SoftDeleteFlag.open, "2023-02-01T00:00:00", "2023-01-01T00:00:00"
                    ),
                    userMaster(
                        2, 11, UserClass.master, "user2@mangaka.co.jp", "password2", "ユーザー2", null,
                        UserType.agency, null, SoftDeleteFlag.open, "2023-02-02T00:00:00", "2023-01-02T00:00:00"
                    )
                ),
                actual
            )
        }

        @Test
        @DisplayName("削除済みのユーザー")
        fun isDeleted() {
            assertNull(sut.selectByMailAddress("user3@mangaka.co.jp"))
        }

        @Test
        @DisplayName("存在しないメールアドレス")
        fun isNotFound() {
            assertNull(sut.selectByMailAddress("user99@mangaka.co.jp"))
        }
    }

    private fun userMaster(
        userId: Int, countryId: Int, userClass: UserClass, secUserMailAddress: String, secUserPassword: String,
        secUserName: String, reminderurl: String?, userType: UserType, remarks: String?,
        softDeleteFlag: SoftDeleteFlag, updateTime: String, createTime: String
    ) = UserMaster(
        UserId(userId), CountryId(countryId), userClass, secUserMailAddress, secUserPassword, secUserName, reminderurl,
        userType, remarks, softDeleteFlag, LocalDateTime.parse(updateTime), LocalDateTime.parse(createTime)
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = Replace.NONE)
    @ContextConfiguration(classes = [CoreMasterDbConfig::class])
    @Import(UserMasterDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CoreMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: UserMasterDaoImpl
    }
}
