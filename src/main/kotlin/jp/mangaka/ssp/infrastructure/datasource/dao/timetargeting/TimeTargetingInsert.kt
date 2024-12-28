package jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingCreateForm

data class TimeTargetingInsert(
    val coAccountId: CoAccountId,
    val timeTargetingName: String,
    val countryId: CountryId,
    private val _isActiveHoliday: Boolean,
    val description: String?
) {
    val isActiveHoliday: String = _isActiveHoliday.toString()
    val timeTargetingStatus: TimeTargetingStatus = TimeTargetingStatus.active

    companion object {
        /**
         * ファクトリ関数
         *
         * @param coAccountId CoアカウントID
         * @param form 登録内容のフォーム
         * @return 生成した TimeTargetingInsert のインスタンス
         */
        fun of(
            coAccountId: CoAccountId,
            form: TimeTargetingCreateForm
        ): TimeTargetingInsert = TimeTargetingInsert(
            coAccountId,
            // バリデーション済みなので強制キャスト
            form.basic.timeTargetingName!!,
            form.basic.countryId,
            form.dayTypePeriods.isActiveHoliday,
            form.basic.description
        )
    }
}
