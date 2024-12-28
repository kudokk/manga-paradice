package jp.mangaka.ssp.application.service.targeting.time.validation

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingEditForm
import org.jetbrains.annotations.TestOnly

sealed class BasicSettingValidation(
    @field:NotBlank(message = "Validation.Input")
    @field:Size(max = 255, message = "Validation.Text.Range")
    private val timeTargetingName: String?,
    @field:Size(max = 1023, message = "Validation.Text.Range")
    private val description: String?,
    private val country: CountryMaster
) {
    @AssertTrue(message = "Validation.Country.Unavailable")
    private fun isCountryId(): Boolean = country.isAvailableAtCompass()

    class BasicSettingCreateValidation @TestOnly constructor(
        timeTargetingName: String?,
        description: String?,
        country: CountryMaster
    ) : BasicSettingValidation(timeTargetingName, description, country) {
        companion object {
            /**
             * ファクトリ関数
             *
             * @param form フォーム
             * @param country 国
             * @return 生成した BasicSettingCreateValidation のインスタンス
             */
            fun of(form: BasicSettingCreateForm, country: CountryMaster): BasicSettingCreateValidation =
                BasicSettingCreateValidation(form.timeTargetingName, form.description, country)
        }
    }

    class BasicSettingEditValidation @TestOnly constructor(
        timeTargetingName: String?,
        description: String?,
        country: CountryMaster,
        private val timeTargetingStatus: TimeTargetingStatus,
        private val structs: Collection<StructCo>
    ) : BasicSettingValidation(timeTargetingName, description, country) {
        // アクティブなストラクトに紐づいている場合はアーカイブへの変更不可
        @AssertTrue(message = "Validation.RelayDeliveringStructs")
        fun isTimeTargetingStatus(): Boolean = if (timeTargetingStatus == TimeTargetingStatus.archive) {
            structs.none { it.structStatus != StructStatus.archive }
        } else {
            true
        }

        companion object {
            /**
             * ファクトリ関数
             *
             * @param form フォーム
             * @param country 国
             * @param structs タイムターゲティングに紐づくストラクトのリスト
             * @return 生成した BasicSettingEditValidation のインスタンス
             */
            fun of(
                form: BasicSettingEditForm,
                country: CountryMaster,
                structs: Collection<StructCo>
            ): BasicSettingEditValidation = BasicSettingEditValidation(
                form.timeTargetingName,
                form.description,
                country,
                form.timeTargetingStatus,
                structs
            )
        }
    }
}
