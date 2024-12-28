package jp.mangaka.ssp.presentation

import jakarta.servlet.http.HttpSession
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.config.SessionConfig.SessionKey
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount

object SessionUtils {
    @Suppress("UNCHECKED_CAST")
    fun getUserType(coAccountId: CoAccountId, session: HttpSession): UserType =
        (session.getAttribute(SessionKey.CO_ACCOUNT_LIST.name) as List<SessionCoAccount>)
            // この関数を呼び出すとき、Interceptorのチェックで存在が確定している想定なのでfirstで取得
            .first { it.coAccountId == coAccountId }
            .userType
}
