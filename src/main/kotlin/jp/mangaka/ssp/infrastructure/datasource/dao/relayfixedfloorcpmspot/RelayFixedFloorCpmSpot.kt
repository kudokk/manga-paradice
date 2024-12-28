package jp.mangaka.ssp.infrastructure.datasource.dao.relayfixedfloorcpmspot

import jp.mangaka.ssp.application.valueobject.deal.DealId
import jp.mangaka.ssp.application.valueobject.spot.SpotId

data class RelayFixedFloorCpmSpot(
    val dealId: DealId,
    val spotId: SpotId
)
