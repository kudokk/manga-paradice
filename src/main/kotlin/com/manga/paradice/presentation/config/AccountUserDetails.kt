package com.manga.paradice.presentation.config

import com.manga.paradice.infrastructure.datasource.dao.usermaster.UserMaster
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class AccountUserDetails(
    val user: UserMaster,
    authorities: List<GrantedAuthority>
) : User(
    user.secMailAddress,
    user.secPassword,
    true,
    true,
    true,
    true,
    authorities
)
