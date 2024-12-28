package jp.mangaka.ssp.application.service.targeting.time

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingEditForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingStatusChangeForm
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingCreateResultView

interface TimeTargetingService {
    /**
     * タイムターゲティングを新規登録する.
     *
     * @param coAccountId CoアカウントID
     * @param form 登録内容
     * @return 登録結果のView
     */
    fun create(coAccountId: CoAccountId, form: TimeTargetingCreateForm): TimeTargetingCreateResultView

    /**
     * タイムターゲティングを更新する.
     *
     * @param coAccountId CoアカウントID
     * @param timeTargetingId タイムターゲティングID
     * @param form 更新内容
     */
    fun edit(coAccountId: CoAccountId, timeTargetingId: TimeTargetingId, form: TimeTargetingEditForm)

    /**
     * タイムターゲティングステータスを更新する.
     *
     * @param coAccountId CoアカウントID
     * @param timeTargetingId タイムターゲティングID
     * @param form 更新内容のフォーム
     */
    fun editTimeTargetingStatus(
        coAccountId: CoAccountId,
        timeTargetingId: TimeTargetingId,
        form: TimeTargetingStatusChangeForm
    )
}
