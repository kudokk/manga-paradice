package jp.mangaka.ssp.application.valueobject.role

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jp.mangaka.ssp.application.valueobject.IdValueObject
import jp.mangaka.ssp.application.valueobject.IdValueObject.Companion.assertNonNegative
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.util.exception.CompassManagerException

data class RoleId(@JsonValue override val value: Int) : IdValueObject<Int>() {
    init {
        assertNonNegative(value)
    }

    /**
     * @return ロールIDに紐づくユーザー種別
     * @throws CompassManagerException ユーザー種別と紐づかないロールIDのとき
     */
    fun toUserType(): UserType = when (value) {
        1 -> UserType.ma_staff
        2 -> UserType.agency
        3 -> UserType.client
        99 -> UserType.other
        else -> throw CompassManagerException("不正なロールID:${this}が入力されました。")
    }

    companion object {
        @JvmStatic
        @JsonCreator
        fun of(value: Int): RoleId = RoleId(value)
    }
}
