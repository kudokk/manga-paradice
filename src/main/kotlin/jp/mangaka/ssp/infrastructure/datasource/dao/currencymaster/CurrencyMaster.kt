package jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster

import jp.mangaka.ssp.application.valueobject.currency.CurrencyId

data class CurrencyMaster(
    val currencyId: CurrencyId,
    val code: String
)
