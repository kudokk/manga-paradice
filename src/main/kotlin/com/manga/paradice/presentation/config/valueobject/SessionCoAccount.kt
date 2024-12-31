package com.manga.paradice.presentation.config.valueobject

import com.manga.paradice.infrastructure.datasource.dao.usermaster.UserMaster
import java.io.Serializable

data class SessionCoAccount(
    val coAccountId: Int,
    val coAccountName: String,
    val userType: String
) : Serializable
