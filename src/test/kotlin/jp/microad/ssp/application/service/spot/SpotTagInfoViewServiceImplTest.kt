package jp.mangaka.ssp.application.service.spot

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.unmockkAll
import jp.mangaka.ssp.application.service.spot.helper.SpotGetWithCheckHelper
import java.math.BigDecimal
import java.time.LocalDateTime
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.proprietydsp.ProprietyDspId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBanner
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBannerDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNative
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNativeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideo
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideoDao
import jp.mangaka.ssp.presentation.controller.spot.view.SpotTagInfoView
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("SpotTagInfoViewServiceImplのテスト")
private class SpotTagInfoViewServiceImplTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val spotId = SpotId(1)
        val incorrectSpotId1 = SpotId(2)
        val incorrectSpotId2 = SpotId(3)
    }

    val spotBannerDao: SpotBannerDao = mock()
    val spotNativeDao: SpotNativeDao = mock()
    val spotVideoDao: SpotVideoDao = mock()
    val spotGetWithCheckHelper: SpotGetWithCheckHelper = mock()

    val sut = spy(
        SpotTagInfoViewServiceImpl(
            spotGetWithCheckHelper,
            spotBannerDao,
            spotNativeDao,
            spotVideoDao
        )
    )

    @AfterEach
    fun after() {
        unmockkAll()
    }

    @Nested
    @DisplayName("generateSpotTypeのテスト")
    inner class GenerateSpotTypeTest {
        val spotBanner: SpotBanner = mock()
        val spotNative: SpotNative = mock()
        val spotVideo: SpotVideo = mock()

        init {
            // spotId = 1 全部一致するパターン
            doReturn(spotBanner).whenever(spotBannerDao).selectById(SpotId(1))
            doReturn(spotNative).whenever(spotNativeDao).selectById(SpotId(1))
            doReturn(spotVideo).whenever(spotVideoDao).selectById(SpotId(1))
            // spotId = 2 バナーとネイティブとが一致するパターン
            doReturn(spotBanner).whenever(spotBannerDao).selectById(SpotId(2))
            doReturn(spotNative).whenever(spotNativeDao).selectById(SpotId(2))
            doReturn(null).whenever(spotVideoDao).selectById(SpotId(2))
            // spotId = 3 バナーとビデオとが一致するパターン
            doReturn(spotBanner).whenever(spotBannerDao).selectById(SpotId(3))
            doReturn(null).whenever(spotNativeDao).selectById(SpotId(3))
            doReturn(spotVideo).whenever(spotVideoDao).selectById(SpotId(3))
            // spotId = 4 ネイティブとビデオとが一致するパターン
            doReturn(null).whenever(spotBannerDao).selectById(SpotId(4))
            doReturn(spotNative).whenever(spotNativeDao).selectById(SpotId(4))
            doReturn(spotVideo).whenever(spotVideoDao).selectById(SpotId(4))
            // spotId = 5 バナーが一致するパターン
            doReturn(spotBanner).whenever(spotBannerDao).selectById(SpotId(5))
            doReturn(null).whenever(spotNativeDao).selectById(SpotId(5))
            doReturn(null).whenever(spotVideoDao).selectById(SpotId(5))
            // spotId = 6 ネイティブが一致するパターン
            doReturn(null).whenever(spotBannerDao).selectById(SpotId(6))
            doReturn(spotNative).whenever(spotNativeDao).selectById(SpotId(6))
            doReturn(null).whenever(spotVideoDao).selectById(SpotId(6))
            // spotId = 7 ビデオが一致するパターン
            doReturn(null).whenever(spotBannerDao).selectById(SpotId(7))
            doReturn(null).whenever(spotNativeDao).selectById(SpotId(7))
            doReturn(spotVideo).whenever(spotVideoDao).selectById(SpotId(7))
            // spotId = 8 どれも一致しないパターン
            doReturn(null).whenever(spotBannerDao).selectById(SpotId(8))
            doReturn(null).whenever(spotNativeDao).selectById(SpotId(8))
            doReturn(null).whenever(spotVideoDao).selectById(SpotId(8))
        }

        @ParameterizedTest
        @DisplayName("正常")
        @CsvSource(
            "1, 'banner/native/video'",
            "2, 'banner/native'",
            "3, 'banner/video'",
            "4, 'native/video'",
            "5, 'banner'",
            "6, 'native'",
            "7, 'video'",
            "8, ''",
        )
        fun isCorrect(spotId: Int, expectedValue: String) =
            assertEquals(expectedValue, sut.generateSpotType(SpotId(spotId)))
    }

    @Nested
    @DisplayName("getSpotTagInfoのテスト")
    inner class GetSpotTagInfoTest {
        val spot: Spot = Spot(
            SpotId(1), SiteId(10), "_", SpotStatus.archive, PlatformId(1),
            Spot.DisplayType.interstitial, Spot.UpstreamType.none, Spot.DeliveryMethod.js, 100, 101, 20,
            true, ProprietyDspId(20), Spot.Anonymous.off, BigDecimal(0.5), "[TEXT]descriptions3",
            "https://test.com/3/", mock()
        )
        val site: Site =
            Site(
                SiteId(1), CoAccountId(1), "サイト1", SiteStatus.active, true, PlatformId(1), Site.SiteType.pc_web,
                ProprietyDspId(1), LocalDateTime.parse("2023-01-01T00:00:00")
            )
        val spotTagInfoView = SpotTagInfoView(
            "_", "", SiteId(1), "サイト1",
            Site.SiteType.pc_web, SpotStatus.archive, Spot.DisplayType.interstitial, "[TEXT]descriptions3"
        )

        val spot2: Spot = Spot(
            SpotId(3), SiteId(3), "[TEXT]_", SpotStatus.archive, PlatformId(3),
            Spot.DisplayType.interstitial, Spot.UpstreamType.none, Spot.DeliveryMethod.js, 100, 101, 20,
            true, ProprietyDspId(20), Spot.Anonymous.off, BigDecimal(0.5), "[TEXT]descriptions3",
            "https://test.com/3/", mock()
        )

        init {
            // 正常
            doReturn(spot).whenever(spotGetWithCheckHelper).getSpotWithCheck(SpotId(1), SpotStatus.entries)
            doReturn(site).whenever(spotGetWithCheckHelper)
                .getSiteWithCheck(coAccountId, spot.siteId, SiteStatus.entries)
            // 異常1 spotがない
            doThrow(CompassManagerException("")).whenever(spotGetWithCheckHelper)
                .getSpotWithCheck(incorrectSpotId1, SpotStatus.entries)
            // 異常2 spotはありがsiteがない
            doReturn(spot2).whenever(spotGetWithCheckHelper).getSpotWithCheck(SpotId(3), SpotStatus.entries)
            doThrow(CompassManagerException("")).whenever(spotGetWithCheckHelper)
                .getSiteWithCheck(coAccountId, SiteId(3), SiteStatus.entries)
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getSpotTagInfo(coAccountId, spotId)
            assertEquals(spotTagInfoView, actual)
            verify(spotGetWithCheckHelper, times(1)).getSpotWithCheck(SpotId(1), SpotStatus.entries)
            verify(spotGetWithCheckHelper, times(1)).getSiteWithCheck(coAccountId, spot.siteId, SiteStatus.entries)
        }

        // 異常系
        @Test
        @DisplayName("異常")
        fun isNotCorrect() {
            // 異常1 spotがない
            val exception1 = assertThrows<CompassManagerException> {
                sut.getSpotTagInfo(coAccountId, incorrectSpotId1)
            }
            assertEquals(exception1.message, "")
            verify(spotGetWithCheckHelper, times(1)).getSpotWithCheck(incorrectSpotId1, SpotStatus.entries)
            verify(spotGetWithCheckHelper, times(0)).getSiteWithCheck(
                coAccountId,
                spot2.siteId,
                SiteStatus.entries
            )

            // 異常2 spotはありがsiteがない
            val exception2 = assertThrows<CompassManagerException> {
                sut.getSpotTagInfo(coAccountId, incorrectSpotId2)
            }
            assertEquals(exception2.message, "")
            verify(spotGetWithCheckHelper, times(1)).getSpotWithCheck(incorrectSpotId2, SpotStatus.entries)
            verify(spotGetWithCheckHelper, times(1)).getSiteWithCheck(coAccountId, SiteId(3), SiteStatus.entries)
        }
    }
}
