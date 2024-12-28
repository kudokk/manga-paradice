package jp.mangaka.ssp.presentation.controller.spot.view.detail

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster.CurrencyMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DeliveryMethod
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplay
import jp.mangaka.ssp.presentation.controller.spot.view.CurrencyView
import jp.mangaka.ssp.presentation.controller.spot.view.SiteView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.BasicSettingView.SpotMaxSizeView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("BasicSettingViewのテスト")
private class BasicSettingViewTest {
    companion object {
        val spotId = SpotId(1)
        val siteId = SiteId(1)
        val platformId = PlatformId(1)
        val currencyId = CurrencyId(1)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @Test
        @DisplayName("全項目あり")
        fun isFull() {
            val spot = Spot(
                spotId, mock(), "spot1", SpotStatus.active, mock(), DisplayType.overlay, UpstreamType.prebidjs,
                DeliveryMethod.js, 100, 200, 30, false, mock(), mock(), mock(), "desc1", "pageUrl1", mock()
            )
            val site = Site(siteId, mock(), "site1", mock(), true, platformId, SiteType.a_app, mock(), mock())
            val spotBannerDisplay = mockBanner(true)
            val spotNativeDisplay = mockNative(true)
            val spotNativeVideoDisplay = mockNativeVideo(true)
            val spotVideoDisplays = mockVideos(listOf(true))
            val currency = CurrencyMaster(currencyId, "code1")

            val actual = BasicSettingView.of(
                spot, site, spotBannerDisplay, spotNativeDisplay, spotNativeVideoDisplay, spotVideoDisplays, currency
            )

            assertEquals(
                BasicSettingView(
                    spotId,
                    "spot1",
                    SpotStatus.active,
                    SiteView(siteId, "site1", SiteType.a_app, platformId),
                    UpstreamType.prebidjs,
                    CurrencyView(currencyId, "code1"),
                    DeliveryMethod.js,
                    DisplayType.overlay,
                    true,
                    false,
                    SpotMaxSizeView(100, 200),
                    "desc1",
                    "pageUrl1"
                ),
                actual
            )
        }

        @Test
        @DisplayName("必須のみ")
        fun isNotFull() {
            val spot = Spot(
                spotId, mock(), "spot1", SpotStatus.standby, mock(), DisplayType.interstitial, UpstreamType.none,
                DeliveryMethod.sdk, null, null, 30, true, mock(), mock(), mock(), null, null, mock()
            )
            val site = Site(siteId, mock(), "site1", mock(), true, platformId, SiteType.pc_web, mock(), mock())

            val actual = BasicSettingView.of(
                spot, site, null, null, null, emptyList(), null
            )

            assertEquals(
                BasicSettingView(
                    spotId,
                    "spot1",
                    SpotStatus.standby,
                    SiteView(siteId, "site1", SiteType.pc_web, platformId),
                    UpstreamType.none,
                    null,
                    DeliveryMethod.sdk,
                    DisplayType.interstitial,
                    false,
                    true,
                    SpotMaxSizeView(null, null),
                    null,
                    null
                ),
                actual
            )
        }
    }

    fun mockBanner(isDisplayControl: Boolean): SpotBannerDisplay = mock {
        on { this.isDisplayControl() } doReturn isDisplayControl
    }

    fun mockNative(isDisplayControl: Boolean): SpotNativeDisplay = mock {
        on { this.isDisplayControl() } doReturn isDisplayControl
    }

    fun mockNativeVideo(isDisplayControl: Boolean): SpotNativeVideoDisplay = mock {
        on { this.isDisplayControl() } doReturn isDisplayControl
    }

    fun mockVideos(isDisplayControls: List<Boolean>): List<SpotVideoDisplay> = isDisplayControls
        .map { isDisplayControl ->
            mock {
                on { this.isDisplayControl() } doReturn isDisplayControl
            }
        }
}
