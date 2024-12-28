package jp.mangaka.ssp.presentation.controller.spot.view.detail

import jp.mangaka.ssp.application.service.spot.util.SpotUtils
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster.CurrencyMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplay
import jp.mangaka.ssp.presentation.controller.spot.view.CurrencyView
import jp.mangaka.ssp.presentation.controller.spot.view.SiteView

data class BasicSettingView(
    val spotId: SpotId,
    val spotName: String,
    val spotStatus: Spot.SpotStatus,
    val site: SiteView,
    val upstreamType: Spot.UpstreamType,
    val currency: CurrencyView?,
    val deliveryMethod: Spot.DeliveryMethod,
    val displayType: Spot.DisplayType,
    val isDisplayControl: Boolean,
    val isAmp: Boolean,
    val spotMaxSize: SpotMaxSizeView?,
    val description: String?,
    val pageUrl: String?
) {
    data class SpotMaxSizeView(val width: Int?, val height: Int?)

    companion object {
        /**
         * @param spot 広告枠
         * @param site サイト
         * @param spotBannerDisplay 広告枠バナー表示
         * @param spotNativeDisplay 広告枠ネイティブ表示
         * @param spotNativeVideoDisplay 広告枠ネイティブビデオ表示
         * @param spotVideoDisplays 広告枠ビデオ表示
         * @param currency 通貨
         * @return 広告枠基本設定のView
         */
        fun of(
            spot: Spot,
            site: Site,
            spotBannerDisplay: SpotBannerDisplay?,
            spotNativeDisplay: SpotNativeDisplay?,
            spotNativeVideoDisplay: SpotNativeVideoDisplay?,
            spotVideoDisplays: Collection<SpotVideoDisplay>,
            currency: CurrencyMaster?,
        ): BasicSettingView = BasicSettingView(
            spot.spotId,
            spot.spotName,
            spot.spotStatus,
            SiteView(site.siteId, site.siteName, site.siteType, site.platformId),
            spot.upstreamType,
            currency?.let { CurrencyView(it.currencyId, it.code) },
            spot.deliveryMethod,
            spot.displayType,
            SpotUtils.isDisplayControl(spotBannerDisplay, spotNativeDisplay, spotNativeVideoDisplay, spotVideoDisplays),
            spot.isAmp,
            SpotMaxSizeView(spot.width, spot.height),
            spot.descriptions,
            spot.pageUrl
        )
    }
}
