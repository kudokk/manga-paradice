package jp.mangaka.ssp.presentation.config.secutiry

import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMasterDao
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class AccountUserDetailsService(private val userMasterDao: UserMasterDao) : UserDetailsService {
    /**
     * @param mailAddress
     * @return メールアドレスに紐づくユーザーのUserDetails
     * @throws UsernameNotFoundException ユーザーが存在しない場合
     */
    override fun loadUserByUsername(mailAddress: String): UserDetails = userMasterDao
        .selectByMailAddress(mailAddress)
        // COMPASSリニューアルの第１段階ではログイン機能の実装のみとなるため権限については未対応
        ?.let { AccountUserDetails(it, emptyList()) }
        ?: throw UsernameNotFoundException("User not found for login id: $mailAddress")
}
