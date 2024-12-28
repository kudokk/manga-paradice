package jp.mangaka.ssp.infrastructure.datasource.dao.relaystructcreative

import jp.mangaka.ssp.application.valueobject.creative.CreativeId
import jp.mangaka.ssp.application.valueobject.struct.StructId

data class RelayStructCreative(
    val structId: StructId,
    val creativeId: CreativeId,
    val deliveryWeight: Int?
)
