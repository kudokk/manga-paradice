package jp.mangaka.ssp.presentation.config.valueobject

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster
import java.io.Serializable

data class SessionCoAccount(
    val coAccountId: CoAccountId,
    val coAccountName: String,
    val userType: UserMaster.UserType
) : Serializable
