package jp.mangaka.ssp.presentation.controller.targeting.time

import jp.mangaka.ssp.application.service.targeting.time.TimeTargetingService
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingEditForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingStatusChangeForm
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingCreateResultView
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TimeTargetingPersistController(
    private val timeTargetingService: TimeTargetingService
) {
    /**
     * タイムターゲティングを新規登録するエンドポイント.
     *
     * @param coAccountId CoアカウントID
     * @param form 登録内容のフォーム
     * @return 登録結果のView
     */
    @PostMapping("/api/targetings/times")
    fun create(
        @RequestParam coAccountId: CoAccountId,
        @RequestBody form: TimeTargetingCreateForm
    ): TimeTargetingCreateResultView = timeTargetingService.create(coAccountId, form)

    /**
     * タイムターゲティングを更新するエンドポイント.
     *
     * @param coAccountId CoアカウントID
     * @param timeTargetingId タイムターゲティングID
     * @param form 更新内容のフォーム
     */
    @PostMapping("/api/targetings/times/{timeTargetingId}")
    fun edit(
        @RequestParam coAccountId: CoAccountId,
        @PathVariable timeTargetingId: TimeTargetingId,
        @RequestBody form: TimeTargetingEditForm
    ) {
        timeTargetingService.edit(coAccountId, timeTargetingId, form)
    }

    /**
     * タイムターゲティングステータスを更新する.
     *
     * @param coAccountId CoアカウントID
     * @param timeTargetingId タイムターゲティングID
     * @param form 更新内容のフォーム
     */
    @PostMapping("/api/targetings/times/{timeTargetingId}/status")
    fun editTimeTargetingStatus(
        @RequestParam coAccountId: CoAccountId,
        @PathVariable timeTargetingId: TimeTargetingId,
        @RequestBody form: TimeTargetingStatusChangeForm
    ) {
        timeTargetingService.editTimeTargetingStatus(coAccountId, timeTargetingId, form)
    }
}
