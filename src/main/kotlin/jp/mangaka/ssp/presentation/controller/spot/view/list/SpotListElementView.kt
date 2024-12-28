package jp.mangaka.ssp.presentation.controller.spot.view.list

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.presentation.common.summary.SummaryView
import jp.mangaka.ssp.presentation.controller.spot.view.DeliveryFormatsView
import jp.mangaka.ssp.presentation.controller.spot.view.SiteView
import jp.mangaka.ssp.presentation.controller.spot.view.SizeTypeInfoView
import java.time.LocalDateTime

data class SpotListElementView(
    val spotId: SpotId,
    val spotName: String,
    val spotStatus: SpotStatus,
    val site: SiteView,
    val displayType: DisplayType,
    val sizeTypes: List<SizeTypeInfoView>,
    val deliveryFormats: DeliveryFormatsView,
    val deliveryResult: SummaryView,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    val updateTime: LocalDateTime
) {
    companion object {
        /**
         * ファクトリ関数
         *
         * @param spots 広告枠のエンティティのリスト
         * @param spotSizeTypesMap 広告枠ID-サイズ種別IDリストのマップ
         * @param siteViewMap サイトID-サイトのViewのマップ
         * @param sizeTypeInfoViewMap サイズ種別ID-サイズ種別のViewのマップ
         * @param deliveryFormatsViewMap 広告枠ID-配信フォーマット使用有無のViewのマップ
         * @param summaryViewMap 広告枠ID-配信実績集計結果のViewのマップ
         * @return 生成した SpotListElementView のインスタンスのリスト
         */
        fun of(
            spots: Collection<Spot>,
            spotSizeTypesMap: Map<SpotId, Collection<SizeTypeId>>,
            siteViewMap: Map<SiteId, SiteView>,
            sizeTypeInfoViewMap: Map<SizeTypeId, SizeTypeInfoView>,
            deliveryFormatsViewMap: Map<SpotId, DeliveryFormatsView>,
            summaryViewMap: Map<SpotId, SummaryView>,
        ): List<SpotListElementView> = spots.map {
            SpotListElementView(
                it.spotId,
                it.spotName,
                it.spotStatus,
                siteViewMap.getValue(it.siteId),
                it.displayType,
                sizeTypeInfoViewMap.getValues(
                    spotSizeTypesMap.getValue(it.spotId)
                ),
                deliveryFormatsViewMap.getValue(it.spotId),
                summaryViewMap.getValue(it.spotId),
                it.updateTime
            )
        }

        private fun <K, V> Map<K, V>.getValues(keys: Collection<K>): List<V> = keys.map { this.getValue(it) }
    }
}
