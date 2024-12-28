package jp.mangaka.ssp.presentation.controller.initialdata.view

import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import org.apache.commons.codec.digest.DigestUtils

data class UserRoleView(
    val encryptedUserId: String,
    val userName: String,
    val mailAddress: String,
    val userType: UserType
) {
    companion object {
        /**
         * @param user ユーザー情報
         * @param userType ユーザーとCoアカウントの紐づきも考慮した権限
         * @return ユーザーと権限情報のView
         */
        fun of(user: UserMaster, userType: UserType): UserRoleView = UserRoleView(
            DigestUtils.sha256Hex(user.userId.value.toString()),
            user.secUserName,
            user.secUserMailAddress,
            userType
        )
    }
}
