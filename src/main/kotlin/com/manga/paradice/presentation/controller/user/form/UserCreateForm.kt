package com.manga.paradice.presentation.controller.user.form

import com.manga.paradice.infrastructure.datasource.dao.usermaster.UserMaster

data class UserCreateForm(
    val secUserName: String,
    val secMailAddress: String,
    val secPassword: String,
    val userType: UserMaster.UserType?,
)
