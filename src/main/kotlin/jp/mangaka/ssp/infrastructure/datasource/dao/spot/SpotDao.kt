package jp.mangaka.ssp.infrastructure.datasource.dao.spot

import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus

interface SpotDao {
    /**
     * @param spot 広告枠のInsertオブジェクト
     * @return 登録時に自動採番された広告枠ID
     */
    fun insert(spot: SpotInsert): SpotId

    /**
     * @param spotId 広告枠ID
     * @param statuses 広告枠ステータス
     * @return 引数のスポットID・ステータスに合致するSpot
     */
    fun selectByIdAndStatus(spotId: SpotId, statuses: Collection<SpotStatus>): Spot?

    /**
     * サイトに紐づく広告枠を取得する.
     *
     * @param siteIds サイトIDのリスト
     * @param statuses 広告枠ステータスのリスト
     * @param limit 取得の上限数
     * @param offset 取得の起点
     * @return 引数のサイトID・ステータスに合致する Spot のリスト
     */
    fun selectBySiteIdsAndStatuses(
        siteIds: Collection<SiteId>,
        statuses: Collection<SpotStatus>,
        limit: Int = Int.MAX_VALUE,
        offset: Int = 0
    ): List<Spot>

    /**
     * @param spot 広告枠のUpdateオブジェクト
     */
    fun update(spot: SpotUpdate)

    /**
     * @param spotId
     */
    fun updateRotationMaxById(spotId: SpotId, rotationMax: Int)
}
