import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.user.UserId
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.config.SessionConfig.SessionKey
import jp.mangaka.ssp.presentation.config.secutiry.AccountUserDetails
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

object IntegrationTestUtils {
    val defaultUserDetails = mockUserDetails(UserId(10), "user10", "user10@test.com", listOf("ROLE_USER"))
    val defaultCoAccount = mockSessionCoAccount(CoAccountId(20), "coAccount20", UserType.ma_staff)
    val defaultSession = mockSession(listOf(defaultCoAccount))

    /**
     * @param userId ユーザーID
     * @param userName ユーザー名
     * @param secUserMailAddress メールアドレス
     * @param roles ロールのリスト
     * @return AccountUserDetailsのモックオブジェクト
     */
    fun mockUserDetails(
        userId: UserId,
        userName: String = "テストユーザー",
        secUserMailAddress: String = "user@test.com",
        roles: List<String> = emptyList()
    ): AccountUserDetails {
        val user: UserMaster = mock {
            on { this.userId } doReturn userId
            on { this.secUserName } doReturn userName
            on { this.secUserMailAddress } doReturn secUserMailAddress
        }

        return mock {
            on { this.user } doReturn user
            on { this.authorities } doReturn roles.map { SimpleGrantedAuthority(it) }
        }
    }

    /**
     * @param coAccountId CoアカウントID
     * @param coAccountName Coアカウント名
     * @param userType ユーザー種別
     * @return SessionCoAccountのモックオブジェクト
     */
    fun mockSessionCoAccount(
        coAccountId: CoAccountId,
        coAccountName: String = "Coアカウント",
        userType: UserType = UserType.ma_staff
    ): SessionCoAccount = mock {
        on { this.coAccountId } doReturn coAccountId
        on { this.coAccountName } doReturn coAccountName
        on { this.userType } doReturn userType
    }

    /**
     * @param coAccounts Coアカウント情報のリスト
     * @return HttpSessionのモックオブジェクト
     */
    fun mockSession(coAccounts: List<SessionCoAccount> = emptyList()): MockHttpSession {
        val session = MockHttpSession()
        session.setAttribute(SessionKey.CO_ACCOUNT_LIST.name, coAccounts)
        return session
    }

    /**
     * GETリクエストを実行する.
     *
     * @param mockMvc mockMvc
     * @param uri URI
     * @param userDetails ユーザー情報（省略時はデフォルト）
     * @param session セッション情報（省略時はデフォルト）
     * @return GETリクエストの実行結果
     */
    fun mockGet(
        mockMvc: MockMvc,
        uri: String,
        userDetails: AccountUserDetails = defaultUserDetails,
        session: MockHttpSession = defaultSession
    ): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .get(uri)
                .with(user(userDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
    }

    /**
     * POSTリクエストを実行する.
     *
     * @param mockMvc mockMvc
     * @param uri URI
     * @param content フォームのJSON文字列
     * @param userDetails ユーザー情報（省略時はデフォルト）
     * @param session セッション情報（省略時はデフォルト）
     * @return POSTリクエストの実行結果
     */
    fun mockPost(
        mockMvc: MockMvc,
        uri: String,
        content: String,
        userDetails: AccountUserDetails = defaultUserDetails,
        session: MockHttpSession = defaultSession
    ): ResultActions = mockMvc.perform(
        MockMvcRequestBuilders
            .post(uri)
            .with(user(userDetails))
            .with {
                it.remoteAddr = "127.0.0.1"
                it
            }
            .with(csrf())
            .session(session)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)
    )
}
