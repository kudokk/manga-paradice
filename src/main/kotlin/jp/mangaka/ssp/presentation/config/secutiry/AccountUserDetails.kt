package jp.mangaka.ssp.presentation.config.secutiry

import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class AccountUserDetails(
    val user: UserMaster,
    authorities: List<GrantedAuthority>
) : User(
    user.secUserMailAddress,
    user.secUserPassword,
    true,
    true,
    true,
    true,
    authorities
)
