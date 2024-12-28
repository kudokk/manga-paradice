package jp.mangaka.ssp.infrastructure.datasource.dao.decoration

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId

data class Decoration(
    val decorationId: DecorationId,
    val decorationName: String,
    val coAccountId: CoAccountId,
    val bandHeight: Int,
    val bandBgcolor: String?,
    val bandString: String,
    val bandFontColor: String
)
