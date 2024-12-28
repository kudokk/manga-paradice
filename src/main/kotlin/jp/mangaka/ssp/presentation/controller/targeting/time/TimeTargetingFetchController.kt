package jp.mangaka.ssp.presentation.controller.targeting.time

import jp.mangaka.ssp.application.service.targeting.time.TimeTargetingViewService
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.presentation.controller.common.view.StructSelectElementView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.CountriesView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingDetailView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingListElementView
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TimeTargetingFetchController(
    private val timeTargetingViewService: TimeTargetingViewService
) {
    /**
     * タイムターゲティングの一覧を取得する.
     *
     * @param coAccountId CoアカウントID
     * @param pageNo ページ番号
     * @return タイムターゲティングの一覧のView
     */
    @GetMapping("/api/targetings/times")
    fun getTimeTargetings(
        @RequestParam coAccountId: CoAccountId,
        @RequestParam pageNo: Int
    ): List<TimeTargetingListElementView> = timeTargetingViewService.getTimeTargetingViews(coAccountId, pageNo)

    /**
     * タイムターゲティングの詳細情報を取得する.
     *
     * @param coAccountId CoアカウントID
     * @param timeTargetingId タイムターゲティングID
     * @return タイムターゲティングの詳細情報のView
     */
    @GetMapping("/api/targetings/times/{timeTargetingId}")
    fun getTimeTargeting(
        @RequestParam coAccountId: CoAccountId,
        @PathVariable timeTargetingId: TimeTargetingId
    ): TimeTargetingDetailView = timeTargetingViewService.getTimeTargetingView(coAccountId, timeTargetingId)

    /**
     * 国選択の一覧を取得する.
     *
     * @param coAccountId CoアカウントID
     * @return 国選択の一覧のView
     */
    @GetMapping("/api/targetings/times/forms/countries")
    fun getCountries(
        @RequestParam coAccountId: CoAccountId
    ): CountriesView = timeTargetingViewService.getCountriesView(coAccountId)

    /**
     * ストラクト選択の一覧を取得する.
     *
     * @param coAccountId CoアカウントID
     * @param timeTargetingId タイムターゲティングID（編集時のみ指定あり）
     * @return ストラクト選択の一覧のView
     */
    @GetMapping("/api/targetings/times/forms/structs")
    fun getStructs(
        @RequestParam coAccountId: CoAccountId,
        @RequestParam timeTargetingId: TimeTargetingId?
    ): List<StructSelectElementView> = timeTargetingViewService.getStructViews(coAccountId, timeTargetingId)
}
