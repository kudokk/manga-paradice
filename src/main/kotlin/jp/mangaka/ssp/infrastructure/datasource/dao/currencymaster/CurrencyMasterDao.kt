package jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster

import jp.mangaka.ssp.application.valueobject.currency.CurrencyId

interface CurrencyMasterDao {
    /**
     * @param currencyId 通貨ID
     * @return 引数の通貨IDに合致するCurrencyMaster
     */
    fun selectById(currencyId: CurrencyId): CurrencyMaster?

    /**
     * @return すべてのCurrencyMasterのリスト
     */
    fun selectAll(): List<CurrencyMaster>
}
