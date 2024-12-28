package jp.mangaka.ssp.infrastructure.datasource.dao.payment

import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface PaymentDao {
    /**
     * @param spotIds 広告枠IDのリスト
     * @return 引数の広告枠IDに紐づく Payment のリスト
     */
    fun selectBySpotIds(spotIds: Collection<SpotId>): List<Payment>

    /**
     * @param spotIds 広告枠IDのリスト
     * @return 引数の広告枠IDに紐づく Payment のリスト
     */
    fun selectBySpotId(spotId: SpotId): List<Payment> = selectBySpotIds(listOf(spotId))
}
