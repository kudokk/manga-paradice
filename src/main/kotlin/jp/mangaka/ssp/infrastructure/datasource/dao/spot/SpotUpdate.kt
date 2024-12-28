package jp.mangaka.ssp.infrastructure.datasource.dao.spot

import jp.mangaka.ssp.application.valueobject.spot.SpotId

data class SpotUpdate(
    val spotId: SpotId,
    val spotName: String,
    val width: Int?,
    val height: Int?,
    val descriptions: String?,
    val pageUrl: String?
)
