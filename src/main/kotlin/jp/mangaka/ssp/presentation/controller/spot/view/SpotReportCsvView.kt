package jp.mangaka.ssp.presentation.controller.spot.view

import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.presentation.common.summary.SummaryView

data class SpotReportCsvView(
    val siteId: SiteId,
    val siteName: String,
    val spotId: SpotId,
    val spotName: String,
    val divId: String,
    val deliveryFormats: DeliveryFormatsView,
    val deliveryResult: SummaryView
) {
    companion object {
        /**
         * ファクトリ関数
         *
         * @param spots 広告枠のエンティティのリスト
         * @param siteViewMap サイトIDとサイトのエンティティのマップ
         * @param deliveryFormatsViewMap 広告枠ID-配信フォーマット使用有無のViewのマップ
         * @param summaryViewMap 広告枠ID-配信実績集計結果のViewのマップ
         * @param divIdMap 広告枠ID-暗号化した広告枠IDのマップ
         * @return 広告枠配信実績CSVのView
         */
        fun of(
            spots: Collection<Spot>,
            siteViewMap: Map<SiteId, SiteView>,
            deliveryFormatsViewMap: Map<SpotId, DeliveryFormatsView>,
            summaryViewMap: Map<SpotId, SummaryView>,
            divIdMap: Map<SpotId, String>
        ): List<SpotReportCsvView> = spots.map {
            val site = siteViewMap.getValue(it.siteId)
            val divId = divIdMap.getValue(it.spotId)
            val summary = summaryViewMap.getValue(it.spotId)
            val deliveryFormats = deliveryFormatsViewMap.getValue(it.spotId)

            SpotReportCsvView(
                site.siteId,
                site.siteName,
                it.spotId,
                it.spotName,
                divId,
                deliveryFormats,
                summary
            )
        }
    }
}
