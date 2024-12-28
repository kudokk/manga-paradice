package jp.mangaka.ssp.presentation.controller.targeting.time.form

import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus

sealed class BasicSettingForm {
    abstract val timeTargetingName: String?
    abstract val countryId: CountryId
    abstract val description: String?

    data class BasicSettingCreateForm(
        override val timeTargetingName: String?,
        override val countryId: CountryId,
        override val description: String?
    ) : BasicSettingForm()

    data class BasicSettingEditForm(
        override val timeTargetingName: String?,
        override val countryId: CountryId,
        override val description: String?,
        val timeTargetingStatus: TimeTargetingStatus
    ) : BasicSettingForm()
}
