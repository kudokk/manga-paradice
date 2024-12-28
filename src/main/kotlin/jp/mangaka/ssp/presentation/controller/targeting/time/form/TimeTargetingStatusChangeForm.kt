package jp.mangaka.ssp.presentation.controller.targeting.time.form

import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingCheckValue

data class TimeTargetingStatusChangeForm(
    val timeTargetingStatus: TimeTargetingStatus,
    val checkValue: TimeTargetingCheckValue
)
