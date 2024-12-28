package jp.mangaka.ssp.infrastructure.datasource.dao.spotupstreamcurrency

import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface SpotUpstreamCurrencyDao {
    /**
     * @param spotUpstreamCurrency 広告枠 HeaderBidding 通貨設定のInsertオブジェクト
     */
    fun insert(spotUpstreamCurrency: SpotUpstreamCurrencyInsert)

    /**
     * @param spotId 広告枠ID
     * @return 広告枠IDに合致する SpotUpstreamCurrency
     */
    fun selectById(spotId: SpotId): SpotUpstreamCurrency?
}
