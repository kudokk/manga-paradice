package jp.mangaka.ssp.presentation.controller.login

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus

@Controller
class LoginController {
    /**
     * CSRFトークンの取得
     */
    @GetMapping("/api/csrfToken")
    fun csrfToken(request: HttpServletRequest, response: HttpServletResponse) {
        response.setHeader("X-CMP-MNG-CSRF-TOKEN", (request.getAttribute("_csrf") as CsrfToken).token)
        response.setHeader("Access-Control-Expose-Headers", "X-CMP-MNG-CSRF-TOKEN")
    }

    /**
     * セッション継続を行う.
     * 旧画面内で操作を行う際に、新画面側のセッションも継続させるために利用する.
     */
    @GetMapping("/api/keep-alive")
    @ResponseStatus(HttpStatus.OK)
    fun keepAlive() = Unit
}
