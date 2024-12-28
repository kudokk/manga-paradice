package jp.mangaka.ssp.infrastructure.datasource.dao.spotupstreamcurrency

import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.application.valueobject.spot.SpotId

data class SpotUpstreamCurrencyInsert(
    val spotId: SpotId,
    val currencyId: CurrencyId
)
