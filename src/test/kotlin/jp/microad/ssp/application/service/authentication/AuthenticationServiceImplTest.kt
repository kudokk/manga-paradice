package jp.mangaka.ssp.application.service.authentication

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jakarta.servlet.http.HttpSession
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.application.valueobject.role.RoleId
import jp.mangaka.ssp.application.valueobject.user.UserId
import jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster.CoAccountMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster.CoAccountMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.usercoaccount.UserCoAccount
import jp.mangaka.ssp.infrastructure.datasource.dao.usercoaccount.UserCoAccountDao
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.config.SessionConfig.SessionKey
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("AuthenticationServiceImplのテスト")
private class AuthenticationServiceImplTest {
    val coAccountMasterDao: CoAccountMasterDao = mock()
    val userCoAccountDao: UserCoAccountDao = mock()

    val sut = spy(AuthenticationServiceImpl(coAccountMasterDao, userCoAccountDao))

    @Nested
    @DisplayName("addSessionCoAccountInfoListのテスト")
    inner class AddSessionCoAccountInfoListTest {
        val coAccountMasters = listOf(
            coAccountMaster(1, "Coアカウント1", 10, 20),
            coAccountMaster(2, "Coアカウント2", 10, 21),
            coAccountMaster(3, "Coアカウント3", 11, 20),
            coAccountMaster(4, "Coアカウント4", 11, 21)
        )
        val user: UserMaster = mock {
            on { userId } doReturn UserId(1)
            on { userType } doReturn UserType.client
        }
        val session: HttpSession = mock()

        @Test
        @DisplayName("全Coアカウント指定のとき")
        fun isAllCoAccount() {
            doReturn(coAccountMasters).whenever(coAccountMasterDao).selectCompassCoAccounts()
            listOf(userCoAccount(CoAccountId.zero.value, 99)).let {
                doReturn(it).whenever(userCoAccountDao).selectCompassUserCoAccountsByUserId(any())
            }

            sut.addSessionCoAccountInfoList(user, session)

            verify(userCoAccountDao, times(1)).selectCompassUserCoAccountsByUserId(user.userId)
            verify(coAccountMasterDao, times(1)).selectCompassCoAccounts()
            verify(coAccountMasterDao, never()).selectCompassCoAccountsByCoAccountIds(any())
            verify(session, times(1)).setAttribute(
                SessionKey.CO_ACCOUNT_LIST.name,
                listOf(
                    sessionCoAccount(1, "Coアカウント1", UserType.client),
                    sessionCoAccount(2, "Coアカウント2", UserType.client),
                    sessionCoAccount(3, "Coアカウント3", UserType.client),
                    sessionCoAccount(4, "Coアカウント4", UserType.client)
                )
            )
        }

        @Test
        @DisplayName("全Coアカウント指定でないとき")
        fun isNotAllCoAccount() {
            doReturn(coAccountMasters).whenever(coAccountMasterDao).selectCompassCoAccountsByCoAccountIds(any())
            listOf(userCoAccount(1, 1), userCoAccount(2, 2), userCoAccount(3, 3), userCoAccount(4, 99)).let {
                doReturn(it).whenever(userCoAccountDao).selectCompassUserCoAccountsByUserId(any())
            }

            sut.addSessionCoAccountInfoList(user, session)

            verify(userCoAccountDao, times(1)).selectCompassUserCoAccountsByUserId(user.userId)
            verify(coAccountMasterDao, never()).selectCompassCoAccounts()
            verify(coAccountMasterDao, times(1))
                .selectCompassCoAccountsByCoAccountIds(listOf(1, 2, 3, 4).map { CoAccountId(it) }.toSet())
            verify(session, times(1)).setAttribute(
                SessionKey.CO_ACCOUNT_LIST.name,
                listOf(
                    sessionCoAccount(1, "Coアカウント1", UserType.ma_staff),
                    sessionCoAccount(2, "Coアカウント2", UserType.agency),
                    sessionCoAccount(3, "Coアカウント3", UserType.client),
                    sessionCoAccount(4, "Coアカウント4", UserType.other),
                )
            )
        }
    }

    private fun userCoAccount(coAccountId: Int, roleId: Int) = UserCoAccount(
        CoAccountId(coAccountId), RoleId(roleId)
    )

    private fun coAccountMaster(coAccountId: Int, coAccountName: String, countryId: Int, currencyId: Int) =
        CoAccountMaster(CoAccountId(coAccountId), coAccountName, CountryId(countryId), CurrencyId(currencyId))

    private fun sessionCoAccount(coAccountId: Int, coAccountName: String, userType: UserType) =
        SessionCoAccount(CoAccountId(coAccountId), coAccountName, userType)
}
