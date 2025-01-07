package com.manga.paradice.infrastructure.datasource.dao.usermaster

import java.io.Serializable
import java.time.LocalDateTime

data class UserMaster(
    val userId: Int,
    val secMailAddress: String,
    val secPassword: String,
    val secUserName: String,
    val userType: UserType,
    val reminderUrl: String?,
    val softDeleteFlag: SoftDeleteFlag,
    val updateTime: LocalDateTime,
    val createTime: LocalDateTime
) : Serializable {
    enum class UserType {
        admin, general, client;

        /**
         * @return この UserType のインスタンスが admin のとき true
         */
        fun isAdmin(): Boolean = this == admin
    }

    enum class SoftDeleteFlag {
        open, deleted
    }
}
