package com.manga.paradice.infrastructure.datasource.dao.usermaster

import java.io.Serializable
import java.time.LocalDateTime

data class UserMaster(
    val userId: Int,
    val countryId: Int,
    val userClass: UserClass,
    val secUserMailAddress: String,
    val secUserPassword: String,
    val secUserName: String,
    val reminderurl: String?,
    val userType: UserType,
    val remarks: String?,
    val softDeleteFlag: SoftDeleteFlag,
    val updateTime: LocalDateTime,
    val createTime: LocalDateTime
) : Serializable {
    enum class UserClass {
        co_account, master
    }

    enum class UserType {
        admin, general, client;

        /**
         * @return この UserType のインスタンスが admin のとき true
         */
        fun isAdmin(): Boolean = this == admin
    }

    enum class SoftDeleteFlag {
        open
    }
}
