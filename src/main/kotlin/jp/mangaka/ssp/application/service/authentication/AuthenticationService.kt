package jp.mangaka.ssp.application.service.authentication

import jakarta.servlet.http.HttpSession
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster

interface AuthenticationService {
    /**
     * セッションにユーザーに紐づくCoアカウントの情報を追加する
     *
     * @param user ユーザー
     * @param session セッション
     */
    fun addSessionCoAccountInfoList(user: UserMaster, session: HttpSession)
}
