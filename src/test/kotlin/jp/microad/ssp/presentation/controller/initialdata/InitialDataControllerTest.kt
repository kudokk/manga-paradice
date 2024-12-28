package jp.mangaka.ssp.presentation.controller.initialdata

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.user.UserId
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.config.SessionConfig
import jp.mangaka.ssp.presentation.config.secutiry.AccountUserDetails
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount
import jp.mangaka.ssp.presentation.controller.initialdata.view.UserRoleView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpSession

@DisplayName("InitialDataControllerのテスト")
private class InitialDataControllerTest {
    val session = MockHttpSession()

    val sut = InitialDataController(session)

    @Nested
    @DisplayName("changeRoleDataのテスト")
    inner class ChangeRoleDataTest {
        val userDetails: AccountUserDetails = mock {
            on { this.user } doReturn UserMaster(
                UserId(10), mock(), mock(), "test@mail.co.jp", "", "ユーザー1", null, mock(),
                null, mock(), mock(), mock()
            )
        }

        init {
            session.setAttribute(
                SessionConfig.SessionKey.CO_ACCOUNT_LIST.name,
                listOf(
                    SessionCoAccount(CoAccountId(1), "Coアカウント1", UserType.ma_staff),
                    SessionCoAccount(CoAccountId(2), "Coアカウント2", UserType.agency),
                    SessionCoAccount(CoAccountId(3), "Coアカウント3", UserType.client)
                )
            )
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.changeRoleData(CoAccountId(2), userDetails)

            assertEquals(
                UserRoleView(
                    "4a44dc15364204a80fe80e9039455cc1608281820fe2b24f1e5233ade6af1dd5",
                    "ユーザー1",
                    "test@mail.co.jp",
                    UserType.agency
                ),
                actual
            )
        }
    }
}
