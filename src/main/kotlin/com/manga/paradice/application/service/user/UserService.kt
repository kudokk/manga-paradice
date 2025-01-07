package com.manga.paradice.application.service.user

import com.manga.paradice.presentation.controller.user.form.UserCreateForm
import com.manga.paradice.presentation.controller.user.view.UserCreateResultView

interface UserService {
    fun create(form: UserCreateForm): UserCreateResultView
}