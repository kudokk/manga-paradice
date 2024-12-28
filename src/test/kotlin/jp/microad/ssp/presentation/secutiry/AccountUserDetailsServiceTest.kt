package jp.mangaka.ssp.presentation.secutiry

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMasterDao
import jp.mangaka.ssp.presentation.config.secutiry.AccountUserDetails
import jp.mangaka.ssp.presentation.config.secutiry.AccountUserDetailsService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException

@DisplayName("AccountUserDetailsServiceのテスト")
private class AccountUserDetailsServiceTest {
    val userMasterDao: UserMasterDao = mock()

    val sut = spy(AccountUserDetailsService(userMasterDao))

    @Nested
    @DisplayName("loadUserByUsernameのテスト")
    inner class LoadUserByUsernameTest {
        val mailAddress = "user@mail.com"

        @Test
        @DisplayName("ユーザーが存在する")
        fun isFound() {
            val user = mock<UserMaster> {
                on { secUserMailAddress } doReturn mailAddress
                on { secUserPassword } doReturn "password"
            }.apply { doReturn(this).whenever(userMasterDao).selectByMailAddress(any()) }

            val actual = sut.loadUserByUsername(mailAddress) as AccountUserDetails

            // 継承しているUserクラスのequals関数はusernameのみの判定のため個別に判定
            assertEquals(user, actual.user)
            assertTrue(actual.isEnabled)
            assertTrue(actual.isAccountNonExpired)
            assertTrue(actual.isCredentialsNonExpired)
            assertTrue(actual.isAccountNonLocked)
            assertEquals(emptySet<GrantedAuthority>(), actual.authorities)
            verify(userMasterDao, times(1)).selectByMailAddress(mailAddress)
        }

        @Test
        @DisplayName("ユーザーが存在しない")
        fun isNotFound() {
            doReturn(null).whenever(userMasterDao).selectByMailAddress(any())

            assertThrows<UsernameNotFoundException> { sut.loadUserByUsername(mailAddress) }

            verify(userMasterDao, times(1)).selectByMailAddress(mailAddress)
        }
    }
}
