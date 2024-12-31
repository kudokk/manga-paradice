package com.manga.paradice.presentation.config.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import com.manga.paradice.presentation.config.SessionConfig.SessionKey
import com.manga.paradice.presentation.config.valueobject.SessionCoAccount
import org.jetbrains.annotations.TestOnly
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.ServletRequestUtils
import org.springframework.web.servlet.HandlerInterceptor

@Component
class CoAccountRequestInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // リクエストパラメーターにCoアカウントIDがない場合は何もしない
        val userIdStr = ServletRequestUtils.getStringParameter(request, "userId") ?: return true

        return if (isValidUser(userIdStr, request)) {
            true
        } else {
            response.status = HttpServletResponse.SC_BAD_REQUEST
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.outputStream.println("""{"message": "Invalid Request Parameter"}""")
            false
        }
    }

    @TestOnly
    @Suppress("UNCHECKED_CAST")
    fun isValidUser(userIdStr: String, request: HttpServletRequest): Boolean {
        val userId = userIdStr.toIntOrNull() ?: return false

        if (userId == 0) return true

        // ユーザーが利用可能なCoアカウントIDかどうかのチェック
        return (request.session.getAttribute(SessionKey.CO_ACCOUNT_LIST.name) as List<SessionCoAccount>)
            .any { it.coAccountId == userId }
    }
}
