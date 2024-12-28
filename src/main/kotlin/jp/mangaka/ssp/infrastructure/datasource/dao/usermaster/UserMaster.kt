package jp.mangaka.ssp.infrastructure.datasource.dao.usermaster

import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.user.UserId
import java.io.Serializable
import java.time.LocalDateTime

data class UserMaster(
    val userId: UserId,
    val countryId: CountryId,
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
        ma_staff, agency, client, other;

        /**
         * @return この UserType のインスタンスが ma_staff のとき true
         */
        fun isMaStaff(): Boolean = this == ma_staff
    }

    enum class SoftDeleteFlag {
        open
    }
}
