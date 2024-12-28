package jp.mangaka.ssp.infrastructure.datasource.dao.relaystructspot

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId

data class RelayStructSpot(
    val structId: StructId,
    val spotId: SpotId
)
