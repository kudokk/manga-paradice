package jp.mangaka.ssp.application.service.spot.helper

import jp.mangaka.ssp.application.service.coaccount.CoAccountGetWithCheckHelper
import jp.mangaka.ssp.application.service.spot.util.SpotUtils
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio.AspectRatioStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.CampaignStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignDao
import jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster.CurrencyMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate.NativeTemplateStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp.RelaySpotDspDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetypeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relaystructspot.RelayStructSpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.Reqgroup.ReqgroupStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.ReqgroupDao
import jp.mangaka.ssp.infrastructure.datasource.dao.reservedeliveryratio.ReserveDeliveryRatio
import jp.mangaka.ssp.infrastructure.datasource.dao.reservedeliveryratio.ReserveDeliveryRatioDao
import jp.mangaka.ssp.infrastructure.datasource.dao.reservetotallimitimpression.ReserveTotalLimitImpression
import jp.mangaka.ssp.infrastructure.datasource.dao.reservetotallimitimpression.ReserveTotalLimitImpressionDao
import jp.mangaka.ssp.infrastructure.datasource.dao.reservetotaltargetimpression.ReserveTotalTargetImpression
import jp.mangaka.ssp.infrastructure.datasource.dao.reservetotaltargetimpression.ReserveTotalTargetImpressionDao
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
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
import jp.mangaka.ssp.infrastructure.datasource.dao.spotupstreamcurrency.SpotUpstreamCurrencyDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideo
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpmDao
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructDao
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.view.detail.BannerSettingView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.BasicSettingView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.DspSettingListItemView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.NativeSettingView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.SpotDetailView
import jp.mangaka.ssp.presentation.controller.common.view.StructSelectElementView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.VideoSettingView
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SpotDetailViewHelper(
    private val campaignDao: CampaignDao,
    private val relaySpotDspDao: RelaySpotDspDao,
    private val relaySpotSizetypeDao: RelaySpotSizetypeDao,
    private val relayStructSpotDao: RelayStructSpotDao,
    private val reqgroupDao: ReqgroupDao,
    private val reserveDeliveryRatioDao: ReserveDeliveryRatioDao,
    private val reserveTotalLimitImpressionDao: ReserveTotalLimitImpressionDao,
    private val reserveTotalTargetImpressionDao: ReserveTotalTargetImpressionDao,
    private val spotBannerDao: SpotBannerDao,
    private val spotBannerDisplayDao: SpotBannerDisplayDao,
    private val spotNativeDao: SpotNativeDao,
    private val spotNativeDisplayDao: SpotNativeDisplayDao,
    private val spotNativeVideoDisplayDao: SpotNativeVideoDisplayDao,
    private val spotVideoDao: SpotVideoDao,
    private val spotVideoDisplayDao: SpotVideoDisplayDao,
    private val spotVideoFloorCpmDao: SpotVideoFloorCpmDao,
    private val spotUpstreamCurrencyDao: SpotUpstreamCurrencyDao,
    private val structDao: StructDao,
    private val coAccountGetWithCheckHelper: CoAccountGetWithCheckHelper,
    private val spotGetWithCheckHelper: SpotGetWithCheckHelper
) {
    /**
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @param userType ユーザー種別
     * @return 広告枠詳細のView
     */
    fun getSpotDetail(coAccountId: CoAccountId, spotId: SpotId, userType: UserType): SpotDetailView {
        val spot = spotGetWithCheckHelper.getSpotWithCheck(spotId, SpotStatus.entries, userType)
        val site = spotGetWithCheckHelper.getSiteWithCheck(coAccountId, spot.siteId, SiteStatus.entries)
        val spotBanner = spotBannerDao.selectById(spotId)
        val spotBannerDisplay = spotBanner?.let { spotBannerDisplayDao.selectById(spot.spotId) }
        val spotNative = spotNativeDao.selectById(spotId)
        val spotNativeDisplay = spotNative?.let { spotNativeDisplayDao.selectById(spot.spotId) }
        val spotNativeVideoDisplay = spotNative?.let { spotNativeVideoDisplayDao.selectBySpotId(spot.spotId) }
        val spotVideo = spotVideoDao.selectById(spotId)
        val spotVideoDisplays = spotVideo?.let { spotVideoDisplayDao.selectBySpotId(spot.spotId) } ?: emptyList()

        return SpotDetailView(
            getBasicView(
                coAccountId,
                spot,
                site,
                spotBannerDisplay,
                spotNativeDisplay,
                spotNativeVideoDisplay,
                spotVideoDisplays
            ),
            getDspViews(spot),
            getBannerView(coAccountId, spot, spotBanner, spotBannerDisplay),
            getNativeView(coAccountId, spot, site, spotNative, spotNativeDisplay, spotNativeVideoDisplay),
            getVideoView(spot, spotVideo, spotVideoDisplays),
            getStructViews(spot),
            spot.updateTime
        )
    }

    @TestOnly
    fun getBasicView(
        coAccountId: CoAccountId,
        spot: Spot,
        site: Site,
        spotBannerDisplay: SpotBannerDisplay?,
        spotNativeDisplay: SpotNativeDisplay?,
        spotNativeVideoDisplay: SpotNativeVideoDisplay?,
        spotVideoDisplays: Collection<SpotVideoDisplay>,
    ): BasicSettingView = BasicSettingView.of(
        spot,
        site,
        spotBannerDisplay,
        spotNativeDisplay,
        spotNativeVideoDisplay,
        spotVideoDisplays,
        getSpotUpstreamCurrency(coAccountId, spot)
    )

    /**
     * 広告枠に紐づくヘッダービディング通貨を取得する.
     *
     * @param coAccountId CoアカウントID
     * @param spot 広告枠
     * @return 広告枠に紐づくヘッダービディング通貨（紐づきがない場合は null )
     */
    @TestOnly
    fun getSpotUpstreamCurrency(coAccountId: CoAccountId, spot: Spot): CurrencyMaster? {
        if (!spot.upstreamType.isPrebidjs()) return null

        // 関連テーブルにレコードがない場合はデフォルトなので、co_account_masterから取得
        val currencyId = spotUpstreamCurrencyDao
            .selectById(spot.spotId)
            ?.currencyId
            ?: coAccountGetWithCheckHelper.getCoAccountWithCheck(coAccountId).currencyId

        return spotGetWithCheckHelper.getCurrencyWithCheck(currencyId)
    }

    @TestOnly
    fun getDspViews(spot: Spot): List<DspSettingListItemView> {
        val relaySpotDsps = relaySpotDspDao.selectBySpotId(spot.spotId)

        val dspIds = relaySpotDsps.map { it.dspId }
        val dsps = spotGetWithCheckHelper.getDspsWithCheck(dspIds)
        val reqgroupDspCountryCos = reqgroupDao
            .selectReqgroupDspCountryCosByDspIdsAndStatuses(dspIds, ReqgroupStatus.entries)
        val countries = spotGetWithCheckHelper.getCountiesWithCheck(
            // 全ての国を表す country_id=0 が含まれるためフィルタリング
            reqgroupDspCountryCos.map { it.countryId }.filter { it != CountryId.zero }
        )

        return DspSettingListItemView.of(
            relaySpotDsps,
            reqgroupDspCountryCos,
            dsps,
            countries
        )
    }

    @TestOnly
    fun getBannerView(
        coAccountId: CoAccountId,
        spot: Spot,
        spotBanner: SpotBanner?,
        spotBannerDisplay: SpotBannerDisplay?
    ): BannerSettingView? {
        if (spotBanner == null) return null

        SpotUtils.checkSpotBannerConsistency(spot.spotId, spotBanner, spotBannerDisplay)

        val relaySpotSizetypes = relaySpotSizetypeDao.selectBySpotId(spot.spotId)
        val sizetypeInfos = spotGetWithCheckHelper.getSizeTypeInfosWithCheck(
            // ネイティブ用のサイズは除外
            relaySpotSizetypes.map { it.sizeTypeId }.filter { !it.isNative() }
        )
        // 存在チェック済みなので強制キャスト
        val decoration = spotBannerDisplay!!.decorationId?.let {
            spotGetWithCheckHelper.getDecorationWithCheck(coAccountId, it)
        }

        return BannerSettingView.of(
            spotBannerDisplay,
            sizetypeInfos,
            decoration
        )
    }

    @TestOnly
    fun getNativeView(
        coAccountId: CoAccountId,
        spot: Spot,
        site: Site,
        spotNative: SpotNative?,
        spotNativeDisplay: SpotNativeDisplay?,
        spotNativeVideoDisplay: SpotNativeVideoDisplay?
    ): NativeSettingView? {
        if (spotNative == null) return null

        SpotUtils.checkSpotNativeConsistency(spot.spotId, spotNative, spotNativeDisplay, spotNativeVideoDisplay)

        val nativeStandardTemplate = spotNativeDisplay?.nativeTemplateId?.let {
            spotGetWithCheckHelper.getNativeStandardTemplateWithCheck(
                coAccountId,
                it,
                NativeTemplateStatus.entries,
                site.platformId
            )
        }
        val nativeVideoTemplate = spotNativeVideoDisplay?.nativeTemplateId?.let {
            spotGetWithCheckHelper.getNativeVideoTemplateWithCheck(it, NativeTemplateStatus.entries)
        }

        return NativeSettingView.of(
            spotNativeDisplay,
            nativeStandardTemplate,
            spotNativeVideoDisplay,
            nativeVideoTemplate
        )
    }

    @TestOnly
    fun getVideoView(
        spot: Spot,
        spotVideo: SpotVideo?,
        spotVideoDisplays: Collection<SpotVideoDisplay>
    ): VideoSettingView? {
        if (spotVideo == null) return null

        SpotUtils.checkSpotVideoConsistency(spot.spotId, spotVideo, spotVideoDisplays)

        val spotVideoFloorCpms = spotVideoFloorCpmDao.selectBySpotId(spot.spotId)
        val aspectRatios = spotGetWithCheckHelper.getAspectRatiosWithCheck(
            spotVideoDisplays.map { it.aspectRatioId },
            AspectRatioStatus.entries
        )

        return VideoSettingView.of(
            spot,
            spotVideo,
            spotVideoDisplays,
            spotVideoFloorCpms,
            aspectRatios
        )
    }

    @TestOnly
    fun getStructViews(spot: Spot): List<StructSelectElementView> {
        val relayStructSpots = relayStructSpotDao.selectBySpotId(spot.spotId)
        val structIds = relayStructSpots.map { it.structId }
        val structs = structDao.selectByIdsAndStatuses(structIds, StructStatus.viewableStatuses)
        val campaigns = campaignDao.selectByIdsAndStatuses(structs.map { it.campaignId }, CampaignStatus.entries)

        val reserveDeliveryRatios = reserveDeliveryRatioDao.selectByStructIds(structIds)
        val reserveTotalLimitImpressions = reserveTotalLimitImpressionDao.selectByStructIds(structIds)
        val reserveTotalTargetImpressions = reserveTotalTargetImpressionDao.selectByStructIds(structIds)

        return StructSelectElementView.of(
            filterDeliverableStructs(
                structs,
                reserveDeliveryRatios,
                reserveTotalLimitImpressions,
                reserveTotalTargetImpressions
            ),
            campaigns
        )
    }

    @TestOnly
    fun filterDeliverableStructs(
        structs: Collection<StructCo>,
        reserveDeliveryRatios: Collection<ReserveDeliveryRatio>,
        reserveTotalLimitImpressions: Collection<ReserveTotalLimitImpression>,
        reserveTotalTargetImpressions: Collection<ReserveTotalTargetImpression>
    ): List<StructCo> {
        val now = LocalDateTime.now()
        val reserveDeliveryRatioMap = reserveDeliveryRatios.groupBy { it.structId }
        val reserveTotalLimitImpressionMap = reserveTotalLimitImpressions.groupBy { it.structId }
        val reserveTotalTargetImpressionMap = reserveTotalTargetImpressions.groupBy { it.structId }

        return structs.filter {
            isDeliverableStruct(
                it,
                reserveDeliveryRatioMap[it.structId] ?: emptyList(),
                reserveTotalLimitImpressionMap[it.structId] ?: emptyList(),
                reserveTotalTargetImpressionMap[it.structId] ?: emptyList(),
                now
            )
        }
    }

    @TestOnly
    fun isDeliverableStruct(
        struct: StructCo,
        reserveDeliveryRatios: Collection<ReserveDeliveryRatio>,
        reserveTotalLimitImpressions: Collection<ReserveTotalLimitImpression>,
        reserveTotalTargetImpressions: Collection<ReserveTotalTargetImpression>,
        now: LocalDateTime
    ): Boolean {
        // ストラクト自体が配信終了日に到達している場合には、その期間内で予約レコードも終了しているはずなので早期リターン
        if (!struct.isNotEndBy(now)) return false

        if (
            reserveDeliveryRatios.isEmpty() &&
            reserveTotalLimitImpressions.isEmpty() &&
            reserveTotalTargetImpressions.isEmpty()
        ) {
            // 空き枠型ストラクト（Filler）の場合はここでチェック終了
            return true
        }

        val today = now.toLocalDate()
        val isDeliverableRatio = reserveDeliveryRatios.any { it.isNotEndBy(today) }
        val isDeliverableLimitImpression = reserveTotalLimitImpressions.any { it.isNotEndBy(today) }
        val isDeliverableTargetImpression = reserveTotalTargetImpressions.any { it.isNotEndBy(today) }

        // 未来のIMP予約が１つでもあれば true
        return isDeliverableRatio || isDeliverableLimitImpression || isDeliverableTargetImpression
    }
}
