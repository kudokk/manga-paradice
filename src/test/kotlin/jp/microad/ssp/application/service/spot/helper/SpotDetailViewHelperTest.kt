package jp.mangaka.ssp.application.service.spot.helper

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jp.mangaka.ssp.application.service.coaccount.CoAccountGetWithCheckHelper
import jp.mangaka.ssp.application.service.spot.util.SpotUtils
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio.AspectRatioStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.CampaignStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignDao
import jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster.CoAccountMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster.CurrencyMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.decoration.Decoration
import jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster.DspMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate.NativeTemplateStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp.RelaySpotDsp
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp.RelaySpotDspDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetype
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetypeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relaystructspot.RelayStructSpot
import jp.mangaka.ssp.infrastructure.datasource.dao.relaystructspot.RelayStructSpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.Reqgroup.ReqgroupStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.ReqgroupDao
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.ReqgroupDspCountryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.reservedeliveryratio.ReserveDeliveryRatio
import jp.mangaka.ssp.infrastructure.datasource.dao.reservedeliveryratio.ReserveDeliveryRatioDao
import jp.mangaka.ssp.infrastructure.datasource.dao.reservetotallimitimpression.ReserveTotalLimitImpression
import jp.mangaka.ssp.infrastructure.datasource.dao.reservetotallimitimpression.ReserveTotalLimitImpressionDao
import jp.mangaka.ssp.infrastructure.datasource.dao.reservetotaltargetimpression.ReserveTotalTargetImpression
import jp.mangaka.ssp.infrastructure.datasource.dao.reservetotaltargetimpression.ReserveTotalTargetImpressionDao
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBanner
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBannerDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNative
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNativeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotupstreamcurrency.SpotUpstreamCurrency
import jp.mangaka.ssp.infrastructure.datasource.dao.spotupstreamcurrency.SpotUpstreamCurrencyDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideo
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpm
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpmDao
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructDao
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.view.detail.BannerSettingView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.BasicSettingView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.DspSettingListItemView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.NativeSettingView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.SpotDetailView
import jp.mangaka.ssp.presentation.controller.common.view.StructSelectElementView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.VideoSettingView
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import io.mockk.verify as verifyK

@DisplayName("SpotDetailViewHelperのテスト")
private class SpotDetailViewHelperTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val currencyId = CurrencyId(1)
        val spotId = SpotId(1)
        val siteId = SiteId(1)
        val platformId = PlatformId(1)
        val updateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0)
    }

    val spot: Spot = mock {
        on { this.spotId } doReturn spotId
        on { this.siteId } doReturn siteId
        on { this.updateTime } doReturn updateTime
    }
    val site: Site = mock {
        on { this.siteId } doReturn siteId
        on { this.platformId } doReturn platformId
    }
    val spotBanner: SpotBanner = mock()
    val spotBannerDisplay: SpotBannerDisplay = mock()
    val spotNative: SpotNative = mock()
    val spotNativeDisplay: SpotNativeDisplay = mock()
    val spotNativeVideoDisplay: SpotNativeVideoDisplay = mock()
    val spotVideo: SpotVideo = mock()
    val spotVideoDisplays: List<SpotVideoDisplay> = mock()

    val campaignDao: CampaignDao = mock()
    val relaySpotDspDao: RelaySpotDspDao = mock()
    val relaySpotSizetypeDao: RelaySpotSizetypeDao = mock()
    val relayStructSpotDao: RelayStructSpotDao = mock()
    val reqgroupDao: ReqgroupDao = mock()
    val reserveDeliveryRatioDao: ReserveDeliveryRatioDao = mock()
    val reserveTotalLimitImpressionDao: ReserveTotalLimitImpressionDao = mock()
    val reserveTotalTargetImpressionDao: ReserveTotalTargetImpressionDao = mock()
    val spotBannerDao: SpotBannerDao = mock()
    val spotBannerDisplayDao: SpotBannerDisplayDao = mock()
    val spotNativeDao: SpotNativeDao = mock()
    val spotNativeDisplayDao: SpotNativeDisplayDao = mock()
    val spotNativeVideoDisplayDao: SpotNativeVideoDisplayDao = mock()
    val spotVideoDao: SpotVideoDao = mock()
    val spotVideoDisplayDao: SpotVideoDisplayDao = mock()
    val spotVideoFloorCpmDao: SpotVideoFloorCpmDao = mock()
    val spotUpstreamCurrencyDao: SpotUpstreamCurrencyDao = mock()
    val structDao: StructDao = mock()
    val coAccountGetWithCheckHelper: CoAccountGetWithCheckHelper = mock()
    val spotGetWithCheckHelper: SpotGetWithCheckHelper = mock()

    val sut = spy(
        SpotDetailViewHelper(
            campaignDao, relaySpotDspDao, relaySpotSizetypeDao, relayStructSpotDao, reqgroupDao,
            reserveDeliveryRatioDao, reserveTotalLimitImpressionDao, reserveTotalTargetImpressionDao,
            spotBannerDao, spotBannerDisplayDao, spotNativeDao, spotNativeDisplayDao, spotNativeVideoDisplayDao,
            spotVideoDao, spotVideoDisplayDao, spotVideoFloorCpmDao, spotUpstreamCurrencyDao, structDao,
            coAccountGetWithCheckHelper, spotGetWithCheckHelper
        )
    )

    @Nested
    @DisplayName("getSpotDetailのテスト")
    inner class GetSpotDetailTest {
        val basicView: BasicSettingView = mock()
        val dspViews: List<DspSettingListItemView> = mock()
        val bannerView: BannerSettingView = mock()
        val nativeView: NativeSettingView = mock()
        val videoView: VideoSettingView = mock()
        val structLitItemViews: List<StructSelectElementView> = mock()

        @Test
        @DisplayName("正常 - 全フォーマットあり")
        fun isCorrectAndAllFormat() {
            doReturn(spot).whenever(spotGetWithCheckHelper).getSpotWithCheck(any(), any(), any())
            doReturn(site).whenever(spotGetWithCheckHelper).getSiteWithCheck(any(), any(), any())
            doReturn(spotBanner).whenever(spotBannerDao).selectById(any())
            doReturn(spotBannerDisplay).whenever(spotBannerDisplayDao).selectById(any())
            doReturn(spotNative).whenever(spotNativeDao).selectById(any())
            doReturn(spotNativeDisplay).whenever(spotNativeDisplayDao).selectById(any())
            doReturn(spotNativeVideoDisplay).whenever(spotNativeVideoDisplayDao).selectBySpotId(any())
            doReturn(spotVideo).whenever(spotVideoDao).selectById(any())
            doReturn(spotVideoDisplays).whenever(spotVideoDisplayDao).selectBySpotId(any())
            doReturn(basicView)
                .whenever(sut)
                .getBasicView(any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
            doReturn(dspViews).whenever(sut).getDspViews(any())
            doReturn(bannerView).whenever(sut).getBannerView(any(), any(), anyOrNull(), anyOrNull())
            doReturn(nativeView).whenever(sut).getNativeView(any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull())
            doReturn(videoView).whenever(sut).getVideoView(any(), anyOrNull(), any())
            doReturn(structLitItemViews).whenever(sut).getStructViews(any())

            val actual = sut.getSpotDetail(coAccountId, spotId, UserType.ma_staff)

            assertEquals(
                SpotDetailView(basicView, dspViews, bannerView, nativeView, videoView, structLitItemViews, updateTime),
                actual
            )

            verify(spotGetWithCheckHelper, times(1)).getSpotWithCheck(spotId, SpotStatus.entries, UserType.ma_staff)
            verify(spotGetWithCheckHelper, times(1)).getSiteWithCheck(coAccountId, siteId, SiteStatus.entries)
            verify(spotBannerDao, times(1)).selectById(spotId)
            verify(spotBannerDisplayDao, times(1)).selectById(spotId)
            verify(spotNativeDao, times(1)).selectById(spotId)
            verify(spotNativeDisplayDao, times(1)).selectById(spotId)
            verify(spotNativeVideoDisplayDao, times(1)).selectBySpotId(spotId)
            verify(spotVideoDao, times(1)).selectById(spotId)
            verify(spotVideoDisplayDao, times(1)).selectBySpotId(spotId)
            verify(sut, times(1)).getBasicView(
                coAccountId, spot, site, spotBannerDisplay, spotNativeDisplay, spotNativeVideoDisplay,
                spotVideoDisplays
            )
            verify(sut, times(1)).getDspViews(spot)
            verify(sut, times(1)).getBannerView(coAccountId, spot, spotBanner, spotBannerDisplay)
            verify(sut, times(1)).getNativeView(
                coAccountId, spot, site, spotNative, spotNativeDisplay, spotNativeVideoDisplay
            )
            verify(sut, times(1)).getVideoView(spot, spotVideo, spotVideoDisplays)
            verify(sut, times(1)).getStructViews(spot)
        }

        @Test
        @DisplayName("正常 - 全フォーマットなし")
        fun isCorrectAndNoFormat() {
            doReturn(spot).whenever(spotGetWithCheckHelper).getSpotWithCheck(any(), any(), any())
            doReturn(site).whenever(spotGetWithCheckHelper).getSiteWithCheck(any(), any(), any())
            doReturn(null).whenever(spotBannerDao).selectById(any())
            doReturn(null).whenever(spotNativeDao).selectById(any())
            doReturn(null).whenever(spotVideoDao).selectById(any())
            doReturn(basicView)
                .whenever(sut)
                .getBasicView(any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
            doReturn(dspViews).whenever(sut).getDspViews(any())
            doReturn(null).whenever(sut).getBannerView(any(), any(), anyOrNull(), anyOrNull())
            doReturn(null).whenever(sut).getNativeView(any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull())
            doReturn(null).whenever(sut).getVideoView(any(), anyOrNull(), any())
            doReturn(structLitItemViews).whenever(sut).getStructViews(any())

            val actual = sut.getSpotDetail(coAccountId, spotId, UserType.ma_staff)

            assertEquals(
                SpotDetailView(basicView, dspViews, null, null, null, structLitItemViews, updateTime),
                actual
            )

            verify(spotGetWithCheckHelper, times(1)).getSpotWithCheck(spotId, SpotStatus.entries, UserType.ma_staff)
            verify(spotGetWithCheckHelper, times(1)).getSiteWithCheck(coAccountId, siteId, SiteStatus.entries)
            verify(spotBannerDao, times(1)).selectById(spotId)
            verify(spotBannerDisplayDao, never()).selectById(any())
            verify(spotNativeDao, times(1)).selectById(spotId)
            verify(spotNativeDisplayDao, never()).selectById(any())
            verify(spotNativeVideoDisplayDao, never()).selectBySpotId(spotId)
            verify(spotVideoDao, times(1)).selectById(spotId)
            verify(spotVideoDisplayDao, never()).selectBySpotId(any())
            verify(sut, times(1)).getBasicView(coAccountId, spot, site, null, null, null, emptyList())
            verify(sut, times(1)).getDspViews(spot)
            verify(sut, times(1)).getBannerView(coAccountId, spot, null, null)
            verify(sut, times(1)).getNativeView(coAccountId, spot, site, null, null, null)
            verify(sut, times(1)).getVideoView(spot, null, emptyList())
            verify(sut, times(1)).getStructViews(spot)
        }
    }

    @Nested
    @DisplayName("getBasicViewのテスト")
    inner class GetBasicViewTest {
        val currency: CurrencyMaster = mock()
        val expected: BasicSettingView = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(BasicSettingView)
            every { BasicSettingView.of(any(), any(), any(), any(), any(), any(), any()) } returns expected
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("任意項目設定あり")
        fun isFull() {
            doReturn(currency).whenever(sut).getSpotUpstreamCurrency(any(), any())

            val actual = sut.getBasicView(
                coAccountId, spot, site, spotBannerDisplay, spotNativeDisplay, spotNativeVideoDisplay, spotVideoDisplays
            )

            assertEquals(expected, actual)

            verify(sut, times(1)).getSpotUpstreamCurrency(coAccountId, spot)
            verifyK {
                BasicSettingView.of(
                    spot, site, spotBannerDisplay, spotNativeDisplay, spotNativeVideoDisplay,
                    spotVideoDisplays, currency
                )
            }
        }

        @Test
        @DisplayName("任意項目設定なし")
        fun isNotFull() {
            doReturn(null).whenever(sut).getSpotUpstreamCurrency(any(), any())

            val actual = sut.getBasicView(
                coAccountId, spot, site, spotBannerDisplay, spotNativeDisplay, spotNativeVideoDisplay, spotVideoDisplays
            )

            assertEquals(expected, actual)

            verify(sut, times(1)).getSpotUpstreamCurrency(coAccountId, spot)
            verifyK {
                BasicSettingView.of(
                    spot, site, spotBannerDisplay, spotNativeDisplay, spotNativeVideoDisplay,
                    spotVideoDisplays, null
                )
            }
        }
    }

    @Nested
    @DisplayName("GetSpotUpstreamCurrencyのテスト")
    inner class GetSpotUpstreamCurrencyTest {
        val spot: Spot = mock {
            on { this.spotId } doReturn spotId
        }
        val coAccount: CoAccountMaster = mock {
            on { this.currencyId } doReturn currencyId
        }
        val spotUpstreamCurrency: SpotUpstreamCurrency = mock {
            on { this.currencyId } doReturn currencyId
        }
        val currency: CurrencyMaster = mock()

        @Test
        @DisplayName("広告枠がヘッダービディングなしのとき")
        fun isUpstreamTypeNone() {
            doReturn(UpstreamType.none).whenever(spot).upstreamType

            assertNull(sut.getSpotUpstreamCurrency(coAccountId, spot))
        }

        @Test
        @DisplayName("広告枠のヘッダービディング通貨がCoアカウントのデフォルト通貨のとき")
        fun isDefaultCurrency() {
            doReturn(UpstreamType.prebidjs).whenever(spot).upstreamType
            doReturn(null).whenever(spotUpstreamCurrencyDao).selectById(any())
            doReturn(coAccount).whenever(coAccountGetWithCheckHelper).getCoAccountWithCheck(any())
            doReturn(currency).whenever(spotGetWithCheckHelper).getCurrencyWithCheck(any())

            val actual = sut.getSpotUpstreamCurrency(coAccountId, spot)

            assertEquals(currency, actual)
            verify(spotUpstreamCurrencyDao, times(1)).selectById(spotId)
            verify(coAccountGetWithCheckHelper, times(1)).getCoAccountWithCheck(coAccountId)
            verify(spotGetWithCheckHelper, times(1)).getCurrencyWithCheck(currencyId)
        }

        @Test
        @DisplayName("広告枠のヘッダービディング通貨がCoアカウントのデフォルト通貨でないのとき")
        fun isNotDefaultCurrency() {
            doReturn(UpstreamType.prebidjs).whenever(spot).upstreamType
            doReturn(spotUpstreamCurrency).whenever(spotUpstreamCurrencyDao).selectById(any())
            doReturn(currency).whenever(spotGetWithCheckHelper).getCurrencyWithCheck(any())

            val actual = sut.getSpotUpstreamCurrency(coAccountId, spot)

            assertEquals(currency, actual)
            verify(spotUpstreamCurrencyDao, times(1)).selectById(spotId)
            verify(coAccountGetWithCheckHelper, never()).getCoAccountWithCheck(coAccountId)
            verify(spotGetWithCheckHelper, times(1)).getCurrencyWithCheck(currencyId)
        }
    }

    @Nested
    @DisplayName("getDspViewsのテスト")
    inner class GetDspViewsTest {
        val dspIds = listOf(1, 2, 3).map { DspId(it) }
        val allCountryIds = listOf(1, 2, 3, CountryId.zero.value, 1, 2, CountryId.zero.value, 1).map { CountryId(it) }
        val filteredCountryIds = listOf(1, 2, 3, 1, 2, 1).map { CountryId(it) }
        val relaySpotDsps: List<RelaySpotDsp> = dspIds.map { dspId ->
            mock {
                on { this.dspId } doReturn dspId
            }
        }
        val dsps: List<DspMaster> = mock()
        val reqgroupDspCountryCos = allCountryIds.map { mockReqgroupDspCountryCo(it) }
        val countries: List<CountryMaster> = mock()
        val expected: List<DspSettingListItemView> = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(DspSettingListItemView)
            every { DspSettingListItemView.of(any(), any(), any(), any()) } returns expected
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(relaySpotDsps).whenever(relaySpotDspDao).selectBySpotId(any())
            doReturn(dsps).whenever(spotGetWithCheckHelper).getDspsWithCheck(any())
            doReturn(reqgroupDspCountryCos)
                .whenever(reqgroupDao)
                .selectReqgroupDspCountryCosByDspIdsAndStatuses(any(), any())
            doReturn(countries).whenever(spotGetWithCheckHelper).getCountiesWithCheck(any())

            val actual = sut.getDspViews(spot)

            assertEquals(expected, actual)

            verify(relaySpotDspDao, times(1)).selectBySpotId(spotId)
            verify(spotGetWithCheckHelper, times(1)).getDspsWithCheck(dspIds)
            verify(reqgroupDao, times(1)).selectReqgroupDspCountryCosByDspIdsAndStatuses(
                dspIds, ReqgroupStatus.entries
            )
            verify(spotGetWithCheckHelper, times(1)).getCountiesWithCheck(filteredCountryIds)
            verifyK { DspSettingListItemView.of(relaySpotDsps, reqgroupDspCountryCos, dsps, countries) }
        }

        private fun mockReqgroupDspCountryCo(countryId: CountryId): ReqgroupDspCountryCo = mock {
            on { this.countryId } doReturn countryId
        }
    }

    @Nested
    @DisplayName("getBannerViewのテスト")
    inner class GetBannerViewTest {
        val bannerSizeTypeIds = listOf(1, 2, 3).map { SizeTypeId(it) }
        val nativeSizeTypeIds = listOf(SizeTypeId.nativePc, SizeTypeId.nativeSp)
        val decorationId = DecorationId(1)
        val relaySpotSizetypes: List<RelaySpotSizetype> = (bannerSizeTypeIds + nativeSizeTypeIds).map { sizeTypeId ->
            mock {
                on { this.sizeTypeId } doReturn sizeTypeId
            }
        }
        val sizetypeInfos: List<SizeTypeInfo> = mock()
        val decoration: Decoration = mock()
        val expected: BannerSettingView = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(BannerSettingView, SpotUtils)
            every { BannerSettingView.of(any(), any(), any()) } returns expected
            every { SpotUtils.checkSpotBannerConsistency(any(), any(), any()) } returns Unit

            doReturn(relaySpotSizetypes).whenever(relaySpotSizetypeDao).selectBySpotId(any())
            doReturn(sizetypeInfos).whenever(spotGetWithCheckHelper).getSizeTypeInfosWithCheck(any())
            doReturn(decoration).whenever(spotGetWithCheckHelper).getDecorationWithCheck(any(), any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("任意項目設定あり")
        fun isFull() {
            doReturn(decorationId).whenever(spotBannerDisplay).decorationId

            val actual = sut.getBannerView(coAccountId, spot, spotBanner, spotBannerDisplay)

            assertEquals(expected, actual)

            verify(relaySpotSizetypeDao, times(1)).selectBySpotId(spotId)
            verify(spotGetWithCheckHelper, times(1)).getSizeTypeInfosWithCheck(bannerSizeTypeIds)
            verify(spotGetWithCheckHelper, times(1)).getDecorationWithCheck(coAccountId, decorationId)
            verifyK { BannerSettingView.of(spotBannerDisplay, sizetypeInfos, decoration) }
            verifyK { SpotUtils.checkSpotBannerConsistency(spotId, spotBanner, spotBannerDisplay) }
        }

        @Test
        @DisplayName("任意項目設定なし")
        fun isNotFull() {
            doReturn(null).whenever(spotBannerDisplay).decorationId

            val actual = sut.getBannerView(coAccountId, spot, spotBanner, spotBannerDisplay)

            assertEquals(expected, actual)

            verify(relaySpotSizetypeDao, times(1)).selectBySpotId(spotId)
            verify(spotGetWithCheckHelper, times(1)).getSizeTypeInfosWithCheck(bannerSizeTypeIds)
            verify(spotGetWithCheckHelper, never()).getDecorationWithCheck(any(), any())
            verifyK { BannerSettingView.of(spotBannerDisplay, sizetypeInfos, null) }
            verifyK { SpotUtils.checkSpotBannerConsistency(spotId, spotBanner, spotBannerDisplay) }
        }

        @Test
        @DisplayName("バナー設定なし")
        fun isNotBanner() {
            val actual = sut.getBannerView(coAccountId, spot, null, null)

            assertNull(actual)
        }
    }

    @Nested
    @DisplayName("getNativeViewのテスト")
    inner class GetNativeViewTest {
        val nativeStandardTemplateId = NativeTemplateId(1)
        val nativeStandardTemplate: NativeTemplate = mock()
        val nativeVideoTemplateId = NativeTemplateId(2)
        val nativeVideoTemplate: NativeTemplate = mock()
        val expected: NativeSettingView = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(NativeSettingView, SpotUtils)
            every { NativeSettingView.of(any(), any(), any(), any()) } returns expected
            every { SpotUtils.checkSpotNativeConsistency(any(), any(), any(), any()) } returns Unit

            doReturn(nativeStandardTemplateId).whenever(spotNativeDisplay).nativeTemplateId
            doReturn(nativeVideoTemplateId).whenever(spotNativeVideoDisplay).nativeTemplateId
            doReturn(nativeStandardTemplate)
                .whenever(spotGetWithCheckHelper)
                .getNativeStandardTemplateWithCheck(any(), any(), any(), any())
            doReturn(nativeVideoTemplate)
                .whenever(spotGetWithCheckHelper)
                .getNativeVideoTemplateWithCheck(any(), any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常 - 通常・ビデオあり")
        fun isCorrectAndFull() {
            val actual = sut.getNativeView(
                coAccountId, spot, site, spotNative, spotNativeDisplay, spotNativeVideoDisplay
            )

            assertEquals(expected, actual)

            verify(spotGetWithCheckHelper, times(1)).getNativeStandardTemplateWithCheck(
                coAccountId, nativeStandardTemplateId, NativeTemplateStatus.entries, platformId
            )
            verify(spotGetWithCheckHelper, times(1)).getNativeVideoTemplateWithCheck(
                nativeVideoTemplateId, NativeTemplateStatus.entries
            )
            verifyK {
                NativeSettingView.of(
                    spotNativeDisplay,
                    nativeStandardTemplate,
                    spotNativeVideoDisplay,
                    nativeVideoTemplate
                )
            }
            verifyK {
                SpotUtils.checkSpotNativeConsistency(spotId, spotNative, spotNativeDisplay, spotNativeVideoDisplay)
            }
        }

        @Test
        @DisplayName("正常 - 通常のみ")
        fun isCorrectAndStandardOnly() {
            val actual = sut.getNativeView(coAccountId, spot, site, spotNative, spotNativeDisplay, null)

            assertEquals(expected, actual)

            verify(spotGetWithCheckHelper, times(1)).getNativeStandardTemplateWithCheck(
                coAccountId, nativeStandardTemplateId, NativeTemplateStatus.entries, platformId
            )
            verify(spotGetWithCheckHelper, never()).getNativeVideoTemplateWithCheck(any(), any())
            verifyK { NativeSettingView.of(spotNativeDisplay, nativeStandardTemplate, null, null) }
            verifyK { SpotUtils.checkSpotNativeConsistency(spotId, spotNative, spotNativeDisplay, null) }
        }

        @Test
        @DisplayName("正常 - ビデオのみ")
        fun isCorrectAndVideoOnly() {
            val actual = sut.getNativeView(coAccountId, spot, site, spotNative, null, spotNativeVideoDisplay)

            assertEquals(expected, actual)

            verify(spotGetWithCheckHelper, never()).getNativeStandardTemplateWithCheck(any(), any(), any(), any())
            verify(spotGetWithCheckHelper, times(1)).getNativeVideoTemplateWithCheck(
                nativeVideoTemplateId, NativeTemplateStatus.entries
            )
            verifyK { NativeSettingView.of(null, null, spotNativeVideoDisplay, nativeVideoTemplate) }
            verifyK { SpotUtils.checkSpotNativeConsistency(spotId, spotNative, null, spotNativeVideoDisplay) }
        }

        @Test
        @DisplayName("ネイティブ設定なし")
        fun isNotNative() {
            val actual = sut.getNativeView(coAccountId, spot, site, null, null, null)

            assertNull(actual)
        }
    }

    @Nested
    @DisplayName("getVideoViewのテスト")
    inner class GetVideoViewTest {
        val spotVideoFloorCpms: List<SpotVideoFloorCpm> = mock()
        val aspectRatios: List<AspectRatio> = mock()
        val aspectRatioIds = listOf(1, 2, 3).map { AspectRatioId(it) }
        val spotVideoDisplays: List<SpotVideoDisplay> = aspectRatioIds.map { aspectRatioId ->
            mock {
                on { this.aspectRatioId } doReturn aspectRatioId
            }
        }
        val expected: VideoSettingView = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(VideoSettingView, SpotUtils)
            every { VideoSettingView.of(any(), any(), any(), any(), any()) } returns expected
            every { SpotUtils.checkSpotVideoConsistency(any(), any(), any()) } returns Unit
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(spotVideoFloorCpms).whenever(spotVideoFloorCpmDao).selectBySpotId(any())
            doReturn(aspectRatios).whenever(spotGetWithCheckHelper).getAspectRatiosWithCheck(any(), any())

            val actual = sut.getVideoView(spot, spotVideo, spotVideoDisplays)

            assertEquals(expected, actual)

            verify(spotVideoFloorCpmDao, times(1)).selectBySpotId(spotId)
            verify(spotGetWithCheckHelper, times(1)).getAspectRatiosWithCheck(aspectRatioIds, AspectRatioStatus.entries)
            verifyK { VideoSettingView.of(spot, spotVideo, spotVideoDisplays, spotVideoFloorCpms, aspectRatios) }
            verifyK { SpotUtils.checkSpotVideoConsistency(spotId, spotVideo, spotVideoDisplays) }
        }

        @Test
        @DisplayName("ビデオ設定なし")
        fun isNotVideo() {
            val actual = sut.getVideoView(spot, null, emptyList())

            assertNull(actual)
        }
    }

    @Nested
    @DisplayName("getStructViewsのテスト")
    inner class GetStructViewsTest {
        val structIds = listOf(1, 2, 3).map { StructId(it) }
        val relayStructSpots: List<RelayStructSpot> = structIds.map { structId ->
            mock {
                on { this.structId } doReturn structId
            }
        }
        val campaignIds = listOf(1, 2, 3).map { CampaignId(it) }
        val structs: List<StructCo> = campaignIds.map { campaignId ->
            mock {
                on { this.campaignId } doReturn campaignId
            }
        }
        val filteredStructs: List<StructCo> = mock()
        val campaigns: List<CampaignCo> = mock()
        val reserveDeliveryRatios: List<ReserveDeliveryRatio> = mock()
        val reserveTotalLimitImpressions: List<ReserveTotalLimitImpression> = mock()
        val reserveTotalTargetImpressions: List<ReserveTotalTargetImpression> = mock()
        val expected: List<StructSelectElementView> = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(StructSelectElementView)
            every { StructSelectElementView.of(any(), any()) } returns expected
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(relayStructSpots).whenever(relayStructSpotDao).selectBySpotId(any())
            doReturn(structs).whenever(structDao).selectByIdsAndStatuses(any(), any())
            doReturn(campaigns).whenever(campaignDao).selectByIdsAndStatuses(any(), any())
            doReturn(reserveDeliveryRatios).whenever(reserveDeliveryRatioDao).selectByStructIds(any())
            doReturn(reserveTotalLimitImpressions).whenever(reserveTotalLimitImpressionDao).selectByStructIds(any())
            doReturn(reserveTotalTargetImpressions).whenever(reserveTotalTargetImpressionDao).selectByStructIds(any())
            doReturn(filteredStructs).whenever(sut).filterDeliverableStructs(any(), any(), any(), any())

            val actual = sut.getStructViews(spot)

            assertEquals(expected, actual)

            verify(relayStructSpotDao, times(1)).selectBySpotId(spotId)
            verify(structDao, times(1)).selectByIdsAndStatuses(structIds, StructCo.StructStatus.viewableStatuses)
            verify(campaignDao, times(1)).selectByIdsAndStatuses(campaignIds, CampaignStatus.entries)
            verify(reserveDeliveryRatioDao, times(1)).selectByStructIds(structIds)
            verify(reserveTotalLimitImpressionDao, times(1)).selectByStructIds(structIds)
            verify(reserveTotalTargetImpressionDao, times(1)).selectByStructIds(structIds)
            verify(sut, times(1)).filterDeliverableStructs(
                structs, reserveDeliveryRatios, reserveTotalLimitImpressions, reserveTotalTargetImpressions
            )
            verifyK { StructSelectElementView.of(filteredStructs, campaigns) }
        }
    }

    @Nested
    @DisplayName("isDeliverableStructのテスト")
    inner class IsDeliverableStructTest {
        val struct: StructCo = mock()
        val now = LocalDateTime.of(2024, 1, 1, 0, 0, 0)

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("配信終了していないストラクトのとき")
        inner class NotEndedStructTest {
            @BeforeEach
            fun beforeEach() {
                doReturn(true).whenever(struct).isNotEndBy(any())
            }

            @Test
            @DisplayName("空き枠型ストラクト")
            fun isFiller() {
                assertTrue(sut.isDeliverableStruct(struct, emptyList(), emptyList(), emptyList(), now))
            }

            @ParameterizedTest
            @MethodSource("notEndedReservationParams")
            @DisplayName("未来のIMP予約が１つ以上ある")
            fun isNotEndedReservation(
                reserveDeliveryRatios: Collection<ReserveDeliveryRatio>,
                reserveTotalLimitImpressions: Collection<ReserveTotalLimitImpression>,
                reserveTotalTargetImpressions: Collection<ReserveTotalTargetImpression>
            ) {
                assertTrue(
                    sut.isDeliverableStruct(
                        struct,
                        reserveDeliveryRatios,
                        reserveTotalLimitImpressions,
                        reserveTotalTargetImpressions,
                        now
                    )
                )
            }

            private fun notEndedReservationParams() = listOf(
                Arguments.of(
                    listOf(mockReserveDeliveryRatio(true)),
                    emptyList<ReserveTotalLimitImpression>(),
                    emptyList<ReserveTotalTargetImpression>()
                ),
                Arguments.of(
                    emptyList<ReserveDeliveryRatio>(),
                    listOf(false, true).map { mockReserveTotalLimitImpression(it) },
                    emptyList<ReserveTotalTargetImpression>()
                ),
                Arguments.of(
                    emptyList<ReserveDeliveryRatio>(),
                    emptyList<ReserveTotalLimitImpression>(),
                    listOf(false, true, false).map { mockReserveTotalTargetImpression(it) }
                ),
                Arguments.of(
                    listOf(mockReserveDeliveryRatio(true)),
                    listOf(true, false).map { mockReserveTotalLimitImpression(it) },
                    listOf(false, false, true).map { mockReserveTotalTargetImpression(it) }
                )
            )

            @ParameterizedTest
            @MethodSource("allEndedReservationParams")
            @DisplayName("未来のIMP予約が１つもない")
            fun isAllEndedReservation(
                reserveDeliveryRatios: Collection<ReserveDeliveryRatio>,
                reserveTotalLimitImpressions: Collection<ReserveTotalLimitImpression>,
                reserveTotalTargetImpressions: Collection<ReserveTotalTargetImpression>
            ) {
                assertFalse(
                    sut.isDeliverableStruct(
                        struct,
                        reserveDeliveryRatios,
                        reserveTotalLimitImpressions,
                        reserveTotalTargetImpressions,
                        now
                    )
                )
            }

            private fun allEndedReservationParams() = listOf(
                Arguments.of(
                    listOf(mockReserveDeliveryRatio(false)),
                    emptyList<ReserveTotalLimitImpression>(),
                    emptyList<ReserveTotalTargetImpression>()
                ),
                Arguments.of(
                    emptyList<ReserveDeliveryRatio>(),
                    listOf(false, false).map { mockReserveTotalLimitImpression(it) },
                    emptyList<ReserveTotalTargetImpression>()
                ),
                Arguments.of(
                    emptyList<ReserveDeliveryRatio>(),
                    emptyList<ReserveTotalLimitImpression>(),
                    listOf(false, false, false).map { mockReserveTotalTargetImpression(it) }
                ),
                Arguments.of(
                    listOf(mockReserveDeliveryRatio(false)),
                    listOf(false, false).map { mockReserveTotalLimitImpression(it) },
                    listOf(false, false, false).map { mockReserveTotalTargetImpression(it) }
                )
            )
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("配信終了しているストラクトのとき")
        inner class EndedStructTest {
            @BeforeEach
            fun beforeEach() {
                doReturn(false).whenever(struct).isNotEndBy(any())
            }

            @Test
            @DisplayName("常にfalse")
            fun isAlwaysFalse() {
                assertFalse(sut.isDeliverableStruct(struct, mock(), mock(), mock(), now))
            }
        }

        fun mockReserveDeliveryRatio(isEnded: Boolean): ReserveDeliveryRatio = mock {
            on { this.isNotEndBy(any()) } doReturn isEnded
        }

        fun mockReserveTotalLimitImpression(isEnded: Boolean): ReserveTotalLimitImpression = mock {
            on { this.isNotEndBy(any()) } doReturn isEnded
        }

        fun mockReserveTotalTargetImpression(isEnded: Boolean): ReserveTotalTargetImpression = mock {
            on { this.isNotEndBy(any()) } doReturn isEnded
        }
    }
}
