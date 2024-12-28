package jp.mangaka.ssp.presentation.controller.targeting.time.form

import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingEditForm
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingCheckValue

data class TimeTargetingEditForm(
    val basic: BasicSettingEditForm,
    val dayTypePeriods: DayTypePeriodsSettingForm,
    val structIds: List<StructId>,
    val checkValue: TimeTargetingCheckValue
)
