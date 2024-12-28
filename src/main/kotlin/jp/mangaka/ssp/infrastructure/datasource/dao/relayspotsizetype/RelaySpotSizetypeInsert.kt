package jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype

import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId

data class RelaySpotSizetypeInsert(
    val spotId: SpotId,
    val sizeTypeId: SizeTypeId
)
