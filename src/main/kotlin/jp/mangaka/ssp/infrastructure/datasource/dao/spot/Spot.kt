package jp.mangaka.ssp.infrastructure.datasource.dao.spot

import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.proprietydsp.ProprietyDspId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import java.math.BigDecimal
import java.time.LocalDateTime

data class Spot(
    val spotId: SpotId,
    val siteId: SiteId,
    val spotName: String,
    val spotStatus: SpotStatus,
    val platformId: PlatformId,
    val displayType: DisplayType,
    val upstreamType: UpstreamType,
    val deliveryMethod: DeliveryMethod,
    val width: Int?,
    val height: Int?,
    val rotationMax: Int,
    val isAmp: Boolean,
    val proprietyDspId: ProprietyDspId,
    val anonymous: Anonymous,
    val winningBidWeight: BigDecimal,
    val descriptions: String?,
    val pageUrl: String?,
    val updateTime: LocalDateTime
) {
    /**
     * @return ネイティブ設定可能な場合は true
     */
    fun isAllowNative(): Boolean = upstreamType.isNone() && deliveryMethod.isJs() && !isAmp

    /**
     * @return ビデオ設定可能な場合は true
     */
    fun isAllowVideo(): Boolean = upstreamType.isNone() && deliveryMethod.isJs() && !isAmp

    enum class SpotStatus {
        active, standby, archive;

        companion object {
            /** 編集可能なステータスのリスト **/
            val editableStatuses = listOf(active, standby)

            /** 閲覧可能なステータスのリスト */
            val viewableStatuses = listOf(active, standby, archive)
        }
    }

    enum class DisplayType {
        inline, overlay, interstitial;

        /**
         * @return この DisplayType のインスタンスが inline のとき true
         */
        fun isInline(): Boolean = this == inline

        /**
         * @return この DisplayType のインスタンスが overlay のとき true
         */
        fun isOverlay(): Boolean = this == overlay

        /**
         * @return この DisplayType のインスタンスが interstitial のとき true
         */
        fun isInterstitial(): Boolean = this == interstitial
    }

    enum class UpstreamType {
        none, prebidjs;

        /**
         * @return この UpstreamType のインスタンスが prebidjs のとき true
         */
        fun isPrebidjs(): Boolean = this == prebidjs

        /**
         * @return この UpstreamType のインスタンスが none のとき true
         */
        fun isNone(): Boolean = this == none
    }

    enum class DeliveryMethod {
        js, sdk;

        /**
         * @return この DeliveryMethod のインスタンスが js のとき true
         */
        fun isJs(): Boolean = this == js

        /**
         * @return この DeliveryMethod のインスタンスが sdk のとき true
         */
        fun isSdk(): Boolean = this == sdk
    }

    enum class Anonymous {
        off, on
    }

    companion object {
        /**
         * 最大ローテーション回数のデフォルト値
         */
        const val rotationMaxDefaultValue: Int = 10
    }
}
