package jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner

import jp.mangaka.ssp.application.valueobject.spot.SpotId

data class SpotBanner(
    val spotId: SpotId,
    val rotationInterval: Int?,
)
