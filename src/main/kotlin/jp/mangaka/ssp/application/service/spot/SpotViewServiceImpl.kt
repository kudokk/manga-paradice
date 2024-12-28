package jp.mangaka.ssp.application.service.spot

import jp.mangaka.ssp.application.service.coaccount.CoAccountGetWithCheckHelper
import jp.mangaka.ssp.application.service.site.SiteGetWithCheckHelper
import jp.mangaka.ssp.application.service.spot.helper.SpotDetailViewHelper
import jp.mangaka.ssp.application.service.spot.helper.SpotGetWithCheckHelper
import jp.mangaka.ssp.application.service.spot.helper.SpotListViewHelper
import jp.mangaka.ssp.application.service.summary.SummaryHelper
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeElementId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio.AspectRatioStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatioDao
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster.CurrencyMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.datatime.DateTimeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.decoration.DecorationDao
import jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster.DspMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.fixedfloorcpm.FixedFloorCpmDao
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate.NativeTemplateStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplateDao
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElementDao
import jp.mangaka.ssp.infrastructure.datasource.dao.payment.Payment.PaymentType
import jp.mangaka.ssp.infrastructure.datasource.dao.payment.PaymentDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp.RelayDefaultCoAccountDspDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relayfixedfloorcpmspot.RelayFixedFloorCpmSpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.Reqgroup.ReqgroupStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.ReqgroupDao
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.site.SiteDao
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.SpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.common.summary.SummaryRequest
import jp.mangaka.ssp.presentation.common.summary.SummaryView
import jp.mangaka.ssp.presentation.controller.spot.view.AspectRatioView
import jp.mangaka.ssp.presentation.controller.common.view.CountrySelectElementView
import jp.mangaka.ssp.presentation.controller.spot.view.CurrencyView
import jp.mangaka.ssp.presentation.controller.spot.view.DecorationView
import jp.mangaka.ssp.presentation.controller.spot.view.DspView
import jp.mangaka.ssp.presentation.controller.spot.view.FixedCpmView
import jp.mangaka.ssp.presentation.controller.spot.view.SiteView
import jp.mangaka.ssp.presentation.controller.spot.view.SizeTypeInfoView
import jp.mangaka.ssp.presentation.controller.spot.view.SpotReportCsvView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.SpotDetailView
import jp.mangaka.ssp.presentation.controller.spot.view.list.SpotListElementView
import jp.mangaka.ssp.presentation.controller.spot.view.nativedesign.NativeDesignPreviewView
import jp.mangaka.ssp.presentation.controller.spot.view.nativedesign.NativeDesignsView
import jp.mangaka.ssp.util.TimeUtils
import jp.mangaka.ssp.util.localfile.LocalFileUtils
import jp.mangaka.ssp.util.localfile.valueobject.LocalFileType
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Service

@Service
internal class SpotViewServiceImpl(
    private val aspectRatioDao: AspectRatioDao,
    private val countryMasterDao: CountryMasterDao,
    private val currencyMasterDao: CurrencyMasterDao,
    private val dateTimeDao: DateTimeDao,
    private val decorationDao: DecorationDao,
    private val dspMasterDao: DspMasterDao,
    private val fixedFloorCpmDao: FixedFloorCpmDao,
    private val nativeTemplateDao: NativeTemplateDao,
    private val nativeTemplateElementDao: NativeTemplateElementDao,
    private val paymentDao: PaymentDao,
    private val relayDefaultCoAccountDspDao: RelayDefaultCoAccountDspDao,
    private val relayFixedFloorCpmSpotDao: RelayFixedFloorCpmSpotDao,
    private val reqgroupDao: ReqgroupDao,
    private val siteDao: SiteDao,
    private val spotDao: SpotDao,
    private val sizeTypeInfoDao: SizeTypeInfoDao,
    private val coAccountGetWithCheckHelper: CoAccountGetWithCheckHelper,
    private val siteGetWithCheckHelper: SiteGetWithCheckHelper,
    private val spotDetailViewHelper: SpotDetailViewHelper,
    private val spotGetWithCheckHelper: SpotGetWithCheckHelper,
    private val spotListViewHelper: SpotListViewHelper,
    private val summaryHelper: SummaryHelper,
    private val localFileUtils: LocalFileUtils
) : SpotViewService {
    override fun getSitesView(coAccountId: CoAccountId): List<SiteView> = siteDao
        .selectByCoAccountIdAndStatuses(coAccountId, listOf(SiteStatus.active, SiteStatus.requested, SiteStatus.ng))
        .let { SiteView.of(it) }

    override fun getCurrenciesView(): List<CurrencyView> =
        currencyMasterDao.selectAll().let { CurrencyView.of(it) }

    override fun getSizeTypeInfosView(coAccountId: CoAccountId): List<SizeTypeInfoView> {
        val standards = sizeTypeInfoDao.selectStandards()
        val userDefineds = sizeTypeInfoDao.selectUserDefinedsByCoAccountId(coAccountId)

        // 共通定義・ユーザー定義のサイズ種別情報をそれぞれ取得しマージ
        return (standards + userDefineds)
            // バナー設定での利用を想定しているのでネイティブのサイズは除外
            .filter { !it.sizeTypeId.isNative() }
            .let { SizeTypeInfoView.of(it) }
    }

    override fun getDecorationsView(coAccountId: CoAccountId): List<DecorationView> = decorationDao
        .selectByCoAccountIds(listOf(CoAccountId.zero, coAccountId))
        .let { DecorationView.of(it) }

    override fun getCountriesView(): List<CountrySelectElementView> = countryMasterDao
        .selectAll()
        .let { CountrySelectElementView.of(it) }

    override fun getDspsView(coAccountId: CoAccountId): List<DspView> {
        val dsps = dspMasterDao.selectAll()
        val dspCountryIds = getDspCountryIds(dsps.map { it.dspId })
        val defaultDsps = relayDefaultCoAccountDspDao.selectByCoAccountId(coAccountId).associateBy { it.dspId }

        return DspView.of(dsps, dspCountryIds, defaultDsps)
    }

    /**
     * @param dspIds DSPIDのリスト
     * @return DSPに紐づく国IDのMap
     */
    @TestOnly
    fun getDspCountryIds(dspIds: Collection<DspId>): Map<DspId, List<CountryId>> {
        val dspCountryIdsInDb = reqgroupDao
            .selectReqgroupDspCountryCosByDspIdsAndStatuses(dspIds, listOf(ReqgroupStatus.active))
            .groupBy({ it.dspId }, { it.countryId })

        return dspIds.associateWith { dspId ->
            // DSPと国の紐づきがない or country_id=0のレコードが存在する場合は「全ての国」を表す country_id=0 を紐づける
            dspCountryIdsInDb[dspId]?.takeIf { !it.contains(CountryId.zero) } ?: listOf(CountryId.zero)
        }
    }

    override fun getNativeDesignsView(coAccountId: CoAccountId): NativeDesignsView {
        val nativeTemplates = nativeTemplateDao.run {
            val statuses = listOf(NativeTemplateStatus.active)
            selectCommonsByStatuses(statuses) + selectPersonalsByCoAccountIdAndStatuses(coAccountId, statuses)
        }
        val videoNativeTemplateIds = getVideoNativeTemplateIds(nativeTemplates.map { it.nativeTemplateId })

        return NativeDesignsView(
            // 通常デザインの共通テンプレートは配信側で除外されている.
            // この仕様は廃止予定になっているが、まだ未対応のため画面でも選択不可にしている.
            nativeTemplates
                .filter { it.coAccountId != null && !videoNativeTemplateIds.contains(it.nativeTemplateId) }
                .let { NativeDesignsView.NativeDesignView.of(it) },
            nativeTemplates
                .filter { it.coAccountId == null && videoNativeTemplateIds.contains(it.nativeTemplateId) }
                .let { NativeDesignsView.NativeDesignView.of(it) },
        )
    }

    /**
     * @param nativeTemplateIds ネイティブテンプレートIDのリスト
     * @return ネイティブ動画デザインのネイティブテンプレートIDのリスト
     */
    @TestOnly
    fun getVideoNativeTemplateIds(
        nativeTemplateIds: Collection<NativeTemplateId>
    ): List<NativeTemplateId> = nativeTemplateElementDao
        .selectByNativeTemplateIds(nativeTemplateIds)
        .filter { it.nativeElementId == NativeElementId.video }
        .map { it.nativeTemplateId }
        .distinct()

    override fun getNativeDesignPreviewView(
        coAccountId: CoAccountId,
        nativeTemplateId: NativeTemplateId
    ): NativeDesignPreviewView {
        val nativeTemplate = spotGetWithCheckHelper
            .getNativeTemplateWithCheck(coAccountId, nativeTemplateId, listOf(NativeTemplateStatus.active))
        val nativeTemplateElements = nativeTemplateElementDao.selectByNativeTemplateIds(listOf(nativeTemplateId))

        return NativeDesignPreviewView.of(nativeTemplate, nativeTemplateElements)
    }

    override fun getAspectRatiosView(): List<AspectRatioView> = aspectRatioDao
        .selectByStatuses(listOf(AspectRatioStatus.active))
        .let { AspectRatioView.of(it) }

    override fun getSpotDetail(
        coAccountId: CoAccountId,
        spotId: SpotId,
        userType: UserType
    ): SpotDetailView = spotDetailViewHelper.getSpotDetail(coAccountId, spotId, userType)

    override fun getSpots(
        coAccountId: CoAccountId,
        summaryRequest: SummaryRequest.ListView,
        pageNo: Int
    ): List<SpotListElementView> = spotListViewHelper.getSpotListViews(coAccountId, summaryRequest, pageNo)

    override fun getFixedCpms(coAccountId: CoAccountId, spotId: SpotId): List<FixedCpmView> {
        val spot = spotGetWithCheckHelper.getSpotWithCheck(spotId, SpotStatus.entries)
        val coAccount = coAccountGetWithCheckHelper.getCoAccountWithCheck(coAccountId)
        val country = spotGetWithCheckHelper.getCountryWithCheck(coAccount.countryId)
        val payments = paymentDao.selectBySpotId(spotId).filter { it.paymentType == PaymentType.fixed_cpm_all }

        // すべて固定単価支払いの設定がない場合は以降のチェックはスキップ
        if (payments.isEmpty()) return emptyList()

        val diffCurrentDate = TimeUtils.fixedDate(dateTimeDao.selectCurrentDateTime(), country.timeDifference)
        val dealIds = relayFixedFloorCpmSpotDao.selectBySpotId(spotId).map { it.dealId }
        val fixedFloorCpms = fixedFloorCpmDao
            .selectByIds(dealIds)
            .filter { it.endDate == null || it.endDate >= diffCurrentDate }

        return FixedCpmView.of(spot, payments, fixedFloorCpms)
    }

    override fun getCoAccountSpotsTotalSummaryView(
        coAccountId: CoAccountId,
        summaryRequest: SummaryRequest.ListView
    ): SummaryView {
        val sites = siteDao.selectByCoAccountIdAndStatuses(coAccountId, SiteStatus.nonArchiveStatuses)
        val spots = spotDao.selectBySiteIdsAndStatuses(sites.map { it.siteId }, SpotStatus.viewableStatuses)
        val summaries = summaryHelper.getTotalSpotSummaries(coAccountId, spots.map { it.spotId }, summaryRequest)
        val commonConfig = localFileUtils.loadConfig(LocalFileType.CommonConfig)

        return SummaryView.of(
            // 総計なのでリストの要素は1つの想定
            summaries.summaryCos.single(),
            summaries.requestSummaryCos.single(),
            summaryRequest.isTaxIncluded,
            commonConfig.taxRate
        )
    }

    override fun getSpotTotalSummaryView(
        coAccountId: CoAccountId,
        spotId: SpotId,
        summaryRequest: SummaryRequest.ListView
    ): SummaryView {
        // IDの存在チェック＆必要なエンティティの取得
        val spot = spotGetWithCheckHelper.getSpotWithCheck(spotId, SpotStatus.viewableStatuses)
        siteGetWithCheckHelper.getSiteWithCheck(coAccountId, spot.siteId, SiteStatus.nonArchiveStatuses)

        val summaries = summaryHelper.getTotalSpotSummaries(coAccountId, listOf(spotId), summaryRequest)
        val commonConfig = localFileUtils.loadConfig(LocalFileType.CommonConfig)

        return SummaryView.of(
            // 総計なのでリストの要素は1つの想定
            summaries.summaryCos.single(),
            summaries.requestSummaryCos.single(),
            summaryRequest.isTaxIncluded,
            commonConfig.taxRate
        )
    }

    override fun getSpotReportCsvViews(
        coAccountId: CoAccountId,
        summaryRequest: SummaryRequest.Csv
    ): List<SpotReportCsvView> = spotListViewHelper.getSpotReportCsvViews(coAccountId, summaryRequest)
}
