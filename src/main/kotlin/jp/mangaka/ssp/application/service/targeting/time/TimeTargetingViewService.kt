package jp.mangaka.ssp.application.service.targeting.time

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.presentation.controller.common.view.StructSelectElementView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.CountriesView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingDetailView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingListElementView

interface TimeTargetingViewService {
    /**
     * タイムターゲティング一覧のViewを取得する.
     *
     * @param coAccountId CoアカウントID
     * @param pageNo ページ番号
     * @return タイムターゲティング一覧のViewのリスト
     */
    fun getTimeTargetingViews(coAccountId: CoAccountId, pageNo: Int): List<TimeTargetingListElementView>

    /**
     * タイムターゲティング詳細のViewを取得する.
     *
     * @param coAccountId CoアカウントID
     * @param timeTargetingId タイムターゲティングID
     * @return タイムターゲティング詳細のView
     */
    fun getTimeTargetingView(coAccountId: CoAccountId, timeTargetingId: TimeTargetingId): TimeTargetingDetailView

    /**
     * 国一覧のViewを取得する.
     *
     * @param coAccountId CoアカウントID
     * @return 国一覧のView
     */
    fun getCountriesView(coAccountId: CoAccountId): CountriesView

    /**
     * ストラクト一覧のViewを取得する.
     *
     * @param coAccountId CoアカウントID
     * @param timeTargetingId タイムターゲティングID
     * @return ストラクト一覧のViewのリスト
     */
    fun getStructViews(coAccountId: CoAccountId, timeTargetingId: TimeTargetingId?): List<StructSelectElementView>
}
