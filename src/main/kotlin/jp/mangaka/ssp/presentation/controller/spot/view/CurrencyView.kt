package jp.mangaka.ssp.presentation.controller.spot.view

import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster.CurrencyMaster

data class CurrencyView(val currencyId: CurrencyId, val code: String) {
    companion object {
        /**
         * @param currencies 通貨のエンティティのリスト
         * @return 通貨一覧のView
         */
        fun of(currencies: Collection<CurrencyMaster>): List<CurrencyView> =
            currencies.map { CurrencyView(it.currencyId, it.code) }
    }
}
