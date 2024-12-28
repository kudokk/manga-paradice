package jp.mangaka.ssp.presentation.controller.spot

import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import jp.mangaka.ssp.application.service.spot.SpotViewService
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.SessionUtils
import jp.mangaka.ssp.presentation.common.summary.SummaryRequest
import jp.mangaka.ssp.presentation.common.summary.SummaryView
import jp.mangaka.ssp.presentation.controller.createFilename
import jp.mangaka.ssp.presentation.controller.setCsvHeader
import jp.mangaka.ssp.presentation.controller.spot.csv.SpotReportCsvGenerator
import jp.mangaka.ssp.presentation.controller.spot.view.AspectRatioView
import jp.mangaka.ssp.presentation.controller.common.view.CountrySelectElementView
import jp.mangaka.ssp.presentation.controller.spot.view.CurrencyView
import jp.mangaka.ssp.presentation.controller.spot.view.DecorationView
import jp.mangaka.ssp.presentation.controller.spot.view.DspView
import jp.mangaka.ssp.presentation.controller.spot.view.FixedCpmView
import jp.mangaka.ssp.presentation.controller.spot.view.SiteView
import jp.mangaka.ssp.presentation.controller.spot.view.SizeTypeInfoView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.SpotDetailView
import jp.mangaka.ssp.presentation.controller.spot.view.list.SpotListElementView
import jp.mangaka.ssp.presentation.controller.spot.view.nativedesign.NativeDesignPreviewView
import jp.mangaka.ssp.presentation.controller.spot.view.nativedesign.NativeDesignsView
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.util.Locale

@RestController
class SpotFetchController(
    private val spotViewService: SpotViewService,
    private val spotReportCsvGenerator: SpotReportCsvGenerator,
    private val session: HttpSession
) {
    /**
     * @param coAccountId CoアカウントID
     * @return サイト一覧のView
     */
    @GetMapping("/api/spots/forms/sites")
    fun getSites(@RequestParam coAccountId: CoAccountId): List<SiteView> =
        spotViewService.getSitesView(coAccountId)

    /**
     * @return 通貨一覧のView
     */
    @GetMapping("/api/spots/forms/currencies")
    fun getCurrencies(): List<CurrencyView> = spotViewService.getCurrenciesView()

    /**
     * @param coAccountId CoアカウントID
     * @return サイズ種別情報一覧のView
     */
    @GetMapping("/api/spots/forms/size-type-infos")
    fun getSizeTypeInfos(coAccountId: CoAccountId): List<SizeTypeInfoView> =
        spotViewService.getSizeTypeInfosView(coAccountId)

    /**
     * @param coAccountId CoアカウントID
     * @return デコレーション設定一覧のView
     */
    @GetMapping("/api/spots/forms/decorations")
    fun getDecorations(coAccountId: CoAccountId): List<DecorationView> =
        spotViewService.getDecorationsView(coAccountId)

    /**
     * @return 国一覧のView
     */
    @GetMapping("/api/spots/forms/countries")
    fun getCountries(): List<CountrySelectElementView> = spotViewService.getCountriesView()

    /**
     * @param coAccountId CoアカウントID
     * @return DSP一覧のView
     */
    @GetMapping("/api/spots/forms/dsps")
    fun getDsps(coAccountId: CoAccountId): List<DspView> = spotViewService.getDspsView(coAccountId)

    /**
     * @param coAccountId CoアカウントID
     * @return ネイティブデザイン一覧のView
     */
    @GetMapping("/api/spots/forms/native-designs")
    fun getNativeDesigns(coAccountId: CoAccountId): NativeDesignsView =
        spotViewService.getNativeDesignsView(coAccountId)

    /**
     * @param coAccountId CoアカウントID
     * @param nativeTemplateId ネイティブテンプレートID
     * @return ネイティブデザインのプレビュー用のView
     */
    @GetMapping("/api/spots/forms/native-designs/{nativeTemplateId}")
    fun getNativeDesignPreview(
        coAccountId: CoAccountId,
        @PathVariable nativeTemplateId: NativeTemplateId
    ): NativeDesignPreviewView = spotViewService.getNativeDesignPreviewView(coAccountId, nativeTemplateId)

    /**
     * @return アスペクト比一覧の View
     */
    @GetMapping("/api/spots/forms/aspect-ratios")
    fun getAspectRatios(): List<AspectRatioView> = spotViewService.getAspectRatiosView()

    /**
     * @return 広告枠詳細の View
     */
    @GetMapping("/api/spots/{spotId}")
    fun getSpotDetail(
        coAccountId: CoAccountId,
        @PathVariable spotId: SpotId,
    ): SpotDetailView =
        spotViewService.getSpotDetail(coAccountId, spotId, SessionUtils.getUserType(coAccountId, session))

    /**
     * 広告枠一覧を取得する.
     *
     * @param coAccountId CoアカウントID
     * @param summaryRequest 集計リクエストの内容
     * @param pageNo ページ番号
     * @return 広告枠一覧のViewのリスト
     */
    @GetMapping("/api/spots")
    fun getSpots(
        coAccountId: CoAccountId,
        summaryRequest: SummaryRequest.ListView,
        pageNo: Int
    ): List<SpotListElementView> = spotViewService.getSpots(coAccountId, summaryRequest, pageNo)

    /**
     * @return 固定単価CPMの View のリスト
     */
    @GetMapping("/api/spots/{spotId}/fixed-cpms")
    fun getSpotFixedCpms(
        coAccountId: CoAccountId,
        @PathVariable spotId: SpotId,
    ): List<FixedCpmView> = spotViewService.getFixedCpms(coAccountId, spotId)

    @GetMapping("/api/spots/summary")
    fun getCoAccountSpotsTotalSummary(
        coAccountId: CoAccountId,
        summaryRequest: SummaryRequest.ListView
    ): SummaryView =
        spotViewService.getCoAccountSpotsTotalSummaryView(coAccountId, summaryRequest)

    /**
     * 特定の広告枠の配信実績合計を取得する.
     *
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @param summaryRequest 集計リクエストの内容
     * @return 配信実績のView
     */
    @GetMapping("/api/spots/{spotId}/summary")
    fun getSpotTotalSummary(
        coAccountId: CoAccountId,
        summaryRequest: SummaryRequest.ListView,
        @PathVariable spotId: SpotId
    ): SummaryView = spotViewService.getSpotTotalSummaryView(coAccountId, spotId, summaryRequest)

    /**
     * 広告枠配信実績CSVをダウンロードする.
     *
     * @param coAccountId CoアカウントID
     * @param summaryRequest 集計リクエストの内容
     * @param response レスポンス
     * @param locale ロケール
     */
    @GetMapping("/api/spots/summary/csv")
    fun downloadSpotReportCsv(
        coAccountId: CoAccountId,
        summaryRequest: SummaryRequest.Csv,
        response: HttpServletResponse,
        locale: Locale
    ) {
        val views = spotViewService.getSpotReportCsvViews(coAccountId, summaryRequest)
        val csv = spotReportCsvGenerator.generate(summaryRequest, views, locale)

        // CSVファイル名
        val csvFileName = "${createFilename("compass_report_")}.csv"
        response.setCsvHeader(csvFileName, locale)

        try {
            response.writer.use { it.write(csv) }
        } catch (e: IOException) {
            throw CompassManagerException("CSVダウンロード中にエラーが発生しました。", e)
        }
    }
}
