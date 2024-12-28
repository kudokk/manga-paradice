package jp.mangaka.ssp.infrastructure.datasource.dao.operationlog

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.user.UserId

data class CompassUserOperationLogInsert(
    val ipAddress: Long,
    val userId: UserId,
    val coAccountId: CoAccountId,
    val execQuery: String?,
    val execParameters: String?,
)
