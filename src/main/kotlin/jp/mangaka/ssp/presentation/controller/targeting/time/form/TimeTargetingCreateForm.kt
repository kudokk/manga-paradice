package jp.mangaka.ssp.presentation.controller.targeting.time.form

import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingCreateForm

data class TimeTargetingCreateForm(
    val basic: BasicSettingCreateForm,
    val dayTypePeriods: DayTypePeriodsSettingForm,
    val structIds: List<StructId>
)
