package jp.mangaka.ssp.application.service.targeting.time.validation

import jakarta.validation.constraints.AssertTrue
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingStatusChangeForm

data class TimeTargetingStatusChangeValidation(
    private val currentStatus: TimeTargetingStatus,
    private val nextStatus: TimeTargetingStatus,
    private val structs: Collection<StructCo>
) {
    @AssertTrue(message = "Validation.RelayDeliveringStructs")
    fun isTimeTargetingStatus(): Boolean = when (nextStatus) {
        // 未変更 or アクティブへの変更
        TimeTargetingStatus.active, currentStatus -> true
        // アーカイブ or 削除済みへの変更
        TimeTargetingStatus.archive, TimeTargetingStatus.deleted ->
            // アクティブなストラクトに紐づく場合は変更不可
            structs.none { it.structStatus != StructStatus.archive }
    }

    companion object {
        /**
         * ファクトリ関数
         *
         * @param form 更新内容のフォーム
         * @param timeTargeting 現在のタイムターゲティング
         * @param structs 紐づくストラクトのリスト
         * @return 生成した TimeTargetingStatusChangeValidation のインスタンス
         */
        fun of(
            form: TimeTargetingStatusChangeForm,
            timeTargeting: TimeTargeting,
            structs: Collection<StructCo>
        ): TimeTargetingStatusChangeValidation {
            // 遷移可能なステータス変更かチェック
            timeTargeting.timeTargetingStatus.checkAllowedChange(form.timeTargetingStatus)

            return TimeTargetingStatusChangeValidation(
                timeTargeting.timeTargetingStatus,
                form.timeTargetingStatus,
                structs
            )
        }
    }
}
