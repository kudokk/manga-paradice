package com.manga.paradice.infrastructure.datasource.dao.usermaster

import com.manga.paradice.infrastructure.datasource.dao.usermaster.UserMaster.UserType

data class UserMasterInsert(
    val secMailAddress: String,
    val secPassword: String,
    val secUserName: String,
    val userType: UserType,
)
