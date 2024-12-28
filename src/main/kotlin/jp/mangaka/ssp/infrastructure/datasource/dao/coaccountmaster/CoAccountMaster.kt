package jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.currency.CurrencyId

data class CoAccountMaster(
    val coAccountId: CoAccountId,
    val coAccountName: String,
    val countryId: CountryId,
    val currencyId: CurrencyId
)
