package jp.mangaka.ssp.infrastructure.datasource.dao.usercoaccount

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.role.RoleId

data class UserCoAccount(
    val coAccountId: CoAccountId,
    val roleId: RoleId
)
