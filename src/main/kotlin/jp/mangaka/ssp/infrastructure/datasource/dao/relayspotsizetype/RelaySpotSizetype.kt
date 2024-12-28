package jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype

import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId

data class RelaySpotSizetype(
    val spotId: SpotId,
    val sizeTypeId: SizeTypeId
)
