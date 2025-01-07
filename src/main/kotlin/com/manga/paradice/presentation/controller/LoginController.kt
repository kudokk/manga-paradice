package com.manga.paradice.presentation.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController {

    @GetMapping("/api/csrfToken")
    fun csrfToken(request: HttpServletRequest, response: HttpServletResponse) {
        response.setHeader("X-CSRF-TOKEN", (request.getAttribute("_csrf") as CsrfToken).token)
        response.setHeader("Access-Control-Expose-Headers", "X-CSRF-TOKEN")
    }
}