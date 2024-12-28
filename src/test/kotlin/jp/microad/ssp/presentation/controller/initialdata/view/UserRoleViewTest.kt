package jp.mangaka.ssp.presentation.controller.initialdata.view

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.user.UserId
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("UserRoleViewのテスト")
private class UserRoleViewTest {
    @Nested
    @DisplayName("生成のテスト")
    inner class OfTest {
        val user = UserMaster(
            UserId(10), mock(), mock(), "test@mail.co.jp", "", "ユーザー1", null, mock(), null, mock(), mock(), mock()
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = UserRoleView.of(user, UserType.agency)

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
