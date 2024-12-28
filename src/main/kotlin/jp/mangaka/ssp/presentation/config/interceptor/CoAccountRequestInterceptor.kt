package jp.mangaka.ssp.presentation.config.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.presentation.config.SessionConfig.SessionKey
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount
import org.jetbrains.annotations.TestOnly
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.ServletRequestUtils
import org.springframework.web.servlet.HandlerInterceptor

@Component
class CoAccountRequestInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // リクエストパラメーターにCoアカウントIDがない場合は何もしない
        val coAccountIdStr = ServletRequestUtils.getStringParameter(request, "coAccountId") ?: return true

        return if (isValidCoAccount(coAccountIdStr, request)) {
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
    fun isValidCoAccount(coAccountIdStr: String, request: HttpServletRequest): Boolean {
        val coAccountId = coAccountIdStr.toIntOrNull()?.let { CoAccountId(it) } ?: return false

        if (coAccountId.isZero()) return true

        // ユーザーが利用可能なCoアカウントIDかどうかのチェック
        return (request.session.getAttribute(SessionKey.CO_ACCOUNT_LIST.name) as List<SessionCoAccount>)
            .any { it.coAccountId == coAccountId }
    }
}
