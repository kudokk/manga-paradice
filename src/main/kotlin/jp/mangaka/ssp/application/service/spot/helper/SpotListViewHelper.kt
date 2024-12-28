package jp.mangaka.ssp.application.service.spot.helper

import jp.mangaka.ssp.presentation.controller.spot.view.SpotReportCsvView
import jp.mangaka.ssp.application.service.spot.util.CryptUtils
import jp.mangaka.ssp.application.service.summary.SummaryHelper
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetypeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.site.SiteDao
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.SpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplayDao
import jp.mangaka.ssp.presentation.common.summary.SummaryRequest
import jp.mangaka.ssp.presentation.common.summary.SummaryView
import jp.mangaka.ssp.presentation.controller.spot.view.DeliveryFormatsView
import jp.mangaka.ssp.presentation.controller.spot.view.SiteView
import jp.mangaka.ssp.presentation.controller.spot.view.SizeTypeInfoView
import jp.mangaka.ssp.presentation.controller.spot.view.list.SpotListElementView
import jp.mangaka.ssp.util.localfile.LocalFileUtils
import jp.mangaka.ssp.util.localfile.valueobject.LocalFileType
import org.jetbrains.annotations.TestOnly
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SpotListViewHelper(
    private val relaySpotSizetypeDao: RelaySpotSizetypeDao,
    private val siteDao: SiteDao,
    private val sizeTypeInfoDao: SizeTypeInfoDao,
    private val spotDao: SpotDao,
    private val spotBannerDisplayDao: SpotBannerDisplayDao,
    private val spotNativeDisplayDao: SpotNativeDisplayDao,
    private val spotNativeVideoDisplayDao: SpotNativeVideoDisplayDao,
    private val spotVideoDisplayDao: SpotVideoDisplayDao,
    private val summaryHelper: SummaryHelper,
    private val cryptUtils: CryptUtils,
    private val localFileUtils: LocalFileUtils,
    @Value("\${app.constant.pagination-size.spots}")
    private val pageSize: Int
) {
    /**
     * 広告枠一覧のViewを取得する.
     *
     * @param coAccountId CoアカウントID
     * @param summaryRequest 集計リクエストの内容
     * @param pageNo ページ番号
     * @return 広告枠一覧のViewのリスト
     */
    fun getSpotListViews(
        coAccountId: CoAccountId,
        summaryRequest: SummaryRequest.ListView,
        pageNo: Int
    ): List<SpotListElementView> {
        val siteViewMap = getSiteViewMap(coAccountId)
        val spots = spotDao.selectBySiteIdsAndStatuses(
            siteViewMap.keys,
            Spot.SpotStatus.viewableStatuses,
            pageSize,
            pageSize * pageNo
        )

        if (spots.isEmpty()) return emptyList()

        val spotIds = spots.map { it.spotId }
        val spotSizeTypesMap = getSpotSizeTypesMap(spotIds)

        return SpotListElementView.of(
            spots,
            spotSizeTypesMap,
            siteViewMap,
            getSizeTypeInfoViewMap(spotSizeTypesMap.values.flatten().distinct()),
            getDeliveryFormatsViewMap(spotIds),
            getSummaryViewMap(coAccountId, spotIds, summaryRequest)
        )
    }

    /**
     * 広告枠配信実績CSVのViewを取得する.
     *
     * @param coAccountId CoアカウントID
     * @param summaryRequest 集計リクエストの内容
     * @return 広告枠配信実績CSVのViewのリスト
     */
    fun getSpotReportCsvViews(coAccountId: CoAccountId, summaryRequest: SummaryRequest.Csv): List<SpotReportCsvView> {
        val siteViewMap = getSiteViewMap(coAccountId)
        val spots = spotDao.selectBySiteIdsAndStatuses(
            siteViewMap.keys,
            Spot.SpotStatus.viewableStatuses
        )

        if (spots.isEmpty()) return emptyList()

        val spotIds = spots.map { it.spotId }

        return SpotReportCsvView.of(
            spots,
            siteViewMap,
            getDeliveryFormatsViewMap(spotIds),
            getSummaryViewMap(coAccountId, spotIds, summaryRequest),
            getDivIdMap(spotIds)
        )
    }

    /**
     * 広告枠とサイズ種別の紐づき情報を取得する.
     *
     * @param spotIds 広告枠IDのリスト
     * @return 広告枠とサイズ種別の紐づき情報のマップ
     */
    @TestOnly
    fun getSpotSizeTypesMap(spotIds: Collection<SpotId>): Map<SpotId, Collection<SizeTypeId>> =
        relaySpotSizetypeDao
            .selectBySpotIds(spotIds)
            .groupBy({ it.spotId }, { it.sizeTypeId })

    /**
     * Coアカウントに紐づくサイトのViewを取得する.
     *
     * @param coAccountId CoアカウントID
     * @return サイトIDとサイトのViewのマップ
     */
    @TestOnly
    fun getSiteViewMap(coAccountId: CoAccountId): Map<SiteId, SiteView> =
        siteDao
            .selectByCoAccountIdAndStatuses(coAccountId, SiteStatus.nonArchiveStatuses)
            .let { SiteView.of(it) }
            .associateBy { it.siteId }

    /**
     * サイズ種別のViewを取得する.
     *
     * @param sizeTypeIds サイズ種別IDのリスト
     * @return サイズ種別IDとサイズ種別のViewのマップ
     */
    @TestOnly
    fun getSizeTypeInfoViewMap(sizeTypeIds: Collection<SizeTypeId>): Map<SizeTypeId, SizeTypeInfoView> =
        sizeTypeInfoDao
            .selectByIds(sizeTypeIds)
            .let { SizeTypeInfoView.of(it) }
            .associateBy { it.sizeTypeId }

    /**
     * 配信フォーマットのViewを取得する.
     *
     * @param spotIds 広告枠IDのリスト
     * @return 広告枠IDと配信フォーマットのView
     */
    @TestOnly
    fun getDeliveryFormatsViewMap(spotIds: Collection<SpotId>): Map<SpotId, DeliveryFormatsView> {
        val activeBannerIds = getActiveBannerSpotIds(spotIds)
        val activeNativeIds = getActiveNativeSpotIds(spotIds)
        val activeVideoIds = getActiveVideoSpotIds(spotIds)

        return spotIds.associateWith {
            DeliveryFormatsView(
                activeBannerIds.contains(it),
                activeNativeIds.contains(it),
                activeVideoIds.contains(it)
            )
        }
    }

    /**
     * バナー設定が有効な広告枠を取得する.
     *
     * @param spotIds 広告枠ID
     * @return バナー設定が有効な広告枠のIDのリスト
     */
    @TestOnly
    fun getActiveBannerSpotIds(spotIds: Collection<SpotId>): List<SpotId> =
        spotBannerDisplayDao
            .selectByIds(spotIds)
            .map { it.spotId }

    /**
     * ネイティブ設定が有効な広告枠を取得する.
     *
     * @param spotIds 広告枠ID
     * @return ネイティブ設定が有効な広告枠のIDのリスト
     */
    @TestOnly
    fun getActiveNativeSpotIds(spotIds: Collection<SpotId>): List<SpotId> {
        val nativeIds = spotNativeDisplayDao
            .selectByIds(spotIds)
            .map { it.spotId }

        val nativeVideoIds = spotNativeVideoDisplayDao
            .selectBySpotIds(spotIds)
            .map { it.spotId }

        return (nativeIds + nativeVideoIds).distinct()
    }

    /**
     * ビデオ設定が有効な広告枠を取得する.
     *
     * @param spotIds 広告枠ID
     * @return ビデオ設定が有効な広告枠のIDのリスト
     */
    @TestOnly
    fun getActiveVideoSpotIds(spotIds: Collection<SpotId>): List<SpotId> =
        spotVideoDisplayDao
            .selectBySpotIds(spotIds)
            .map { it.spotId }.distinct()

    /**
     * 配信実績集計結果のViewを取得する.
     *
     * @param coAccountId CoアカウントID
     * @param spotIds 広告枠IDのリスト
     * @param summaryRequest 集計リクエストの内容
     * @return 広告枠IDと配信実績集計結果のView
     */
    @TestOnly
    fun getSummaryViewMap(
        coAccountId: CoAccountId,
        spotIds: Collection<SpotId>,
        summaryRequest: SummaryRequest
    ): Map<SpotId, SummaryView> {
        val summaries = summaryHelper.getSpotSummaries(coAccountId, spotIds, summaryRequest)
        val commonConfig = localFileUtils.loadConfig(LocalFileType.CommonConfig)

        val summaryCoMap = summaries.summaryCos.associateBy { it.spotId }
        val requestMap = summaries.requestSummaryCos.associateBy { it.spotId }

        return spotIds.associateWith {
            val summaryCo = summaryCoMap[it] ?: return@associateWith SummaryView.zero

            SummaryView.of(
                // 総計なのでリストの要素は1つの想定
                summaryCo,
                requestMap[it],
                summaryRequest.isTaxIncluded,
                commonConfig.taxRate
            )
        }
    }

    /**
     * 暗号化した広告枠IDを取得する.
     *
     * @param spotIds 広告枠IDのリスト
     * @return 広告枠IDと暗号化後の広告枠IDのマップ
     */
    @TestOnly
    fun getDivIdMap(spotIds: Collection<SpotId>): Map<SpotId, String> =
        spotIds.associateWith { cryptUtils.encryptForTag(it) }
}
