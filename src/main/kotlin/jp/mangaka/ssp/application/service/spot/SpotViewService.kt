package jp.mangaka.ssp.application.service.spot

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
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

interface SpotViewService {
    /**
     * @param coAccountId CoアカウントID
     * @return サイト一覧のView
     */
    fun getSitesView(coAccountId: CoAccountId): List<SiteView>

    /**
     * @return 通貨一覧のView
     */
    fun getCurrenciesView(): List<CurrencyView>

    /**
     * @param coAccountId CoアカウントID
     * @return サイズ種別情報一覧のView
     */
    fun getSizeTypeInfosView(coAccountId: CoAccountId): List<SizeTypeInfoView>

    /**
     * @param coAccountId CoアカウントID
     * @return デコレーション設定一覧のView
     */
    fun getDecorationsView(coAccountId: CoAccountId): List<DecorationView>

    /**
     * @return 国一覧のView
     */
    fun getCountriesView(): List<CountrySelectElementView>

    /**
     * @param coAccountId CoアカウントID
     * @return DSP一覧のView
     */
    fun getDspsView(coAccountId: CoAccountId): List<DspView>

    /**
     * @param coAccountId CoアカウントID
     * @return ネイティブデザイン一覧のView
     */
    fun getNativeDesignsView(coAccountId: CoAccountId): NativeDesignsView

    /**
     * @param coAccountId CoアカウントID
     * @param nativeTemplateId ネイティブテンプレートID
     * @return ネイティブデザインのプレビュー用のView
     */
    fun getNativeDesignPreviewView(
        coAccountId: CoAccountId,
        nativeTemplateId: NativeTemplateId
    ): NativeDesignPreviewView

    /**
     * @return アスペクト比一覧の View
     */
    fun getAspectRatiosView(): List<AspectRatioView>

    /**
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @param userType ユーザー種別
     * @return 広告枠詳細のView
     */
    fun getSpotDetail(coAccountId: CoAccountId, spotId: SpotId, userType: UserType): SpotDetailView

    /**
     * 広告枠一覧を取得する.
     *
     * @param coAccountId CoアカウントID
     * @param summaryRequest 集計リクエストの内容
     * @param pageNo ページ番号
     * @return 広告枠一覧のViewのリスト
     */
    fun getSpots(
        coAccountId: CoAccountId,
        summaryRequest: SummaryRequest.ListView,
        pageNo: Int
    ): List<SpotListElementView>

    /**
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @return 固定単価CPMの View のリスト
     */
    fun getFixedCpms(coAccountId: CoAccountId, spotId: SpotId): List<FixedCpmView>

    /**
     * Coアカウントに紐づく広告枠の配信実績合計を取得する.
     *
     * @param coAccountId CoアカウントID
     * @param summaryRequest 集計リクエストの内容
     * @return 広告枠の配信実績集計結果のView
     */
    fun getCoAccountSpotsTotalSummaryView(
        coAccountId: CoAccountId,
        summaryRequest: SummaryRequest.ListView
    ): SummaryView

    /**
     * 特定の広告枠の配信実績合計を取得する.
     *
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @param summaryRequest 集計リクエストの内容
     * @return 広告枠の配信実績集計結果のView
     */
    fun getSpotTotalSummaryView(
        coAccountId: CoAccountId,
        spotId: SpotId,
        summaryRequest: SummaryRequest.ListView
    ): SummaryView

    /**
     * Coアカウントに紐づく広告枠の配信実績CSV情報を取得する.
     *
     * @param coAccountId CoアカウントID
     * @param summaryRequest 集計リクエストの内容
     * @return 広告枠配信実績CSVのViewのリスト
     */
    fun getSpotReportCsvViews(coAccountId: CoAccountId, summaryRequest: SummaryRequest.Csv): List<SpotReportCsvView>
}
