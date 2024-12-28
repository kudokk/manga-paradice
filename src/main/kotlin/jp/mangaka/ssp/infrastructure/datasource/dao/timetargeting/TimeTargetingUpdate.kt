package jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting

import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingEditForm

data class TimeTargetingUpdate(
    val timeTargetingId: TimeTargetingId,
    val timeTargetingName: String,
    val timeTargetingStatus: TimeTargetingStatus,
    val countryId: CountryId,
    private val _isActiveHoliday: Boolean,
    val description: String?
) {
    val isActiveHoliday: String = _isActiveHoliday.toString()

    companion object {
        /**
         * ファクトリ関数
         *
         * @param timeTargetingId タイムターゲティングID
         * @param form 更新内容のForm
         * @return 生成した TimeTargetingUpdate のインスタンス
         */
        fun of(
            timeTargetingId: TimeTargetingId,
            form: TimeTargetingEditForm
        ): TimeTargetingUpdate = TimeTargetingUpdate(
            timeTargetingId,
            // バリデーション済みなので強制キャスト
            form.basic.timeTargetingName!!,
            form.basic.timeTargetingStatus,
            form.basic.countryId,
            form.dayTypePeriods.isActiveHoliday,
            form.basic.description
        )
    }
}
