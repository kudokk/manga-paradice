package com.manga.paradice.presentation.controller.user

import com.manga.paradice.application.service.user.UserService
import com.manga.paradice.presentation.controller.user.form.UserCreateForm
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService
) {
    @PostMapping("/api/user")
    fun create(
        @RequestBody form: UserCreateForm
    ) {
        userService.create(form)
    }
}
