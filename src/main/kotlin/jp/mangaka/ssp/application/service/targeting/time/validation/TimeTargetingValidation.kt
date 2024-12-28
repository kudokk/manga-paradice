package jp.mangaka.ssp.application.service.targeting.time.validation

import jakarta.validation.Valid
import jakarta.validation.constraints.Null
import jp.mangaka.ssp.application.service.targeting.time.validation.BasicSettingValidation.BasicSettingCreateValidation
import jp.mangaka.ssp.application.service.targeting.time.validation.BasicSettingValidation.BasicSettingEditValidation
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingEditForm
import org.jetbrains.annotations.TestOnly

sealed class TimeTargetingValidation(
    @field:Valid
    private val basic: BasicSettingValidation,
    @field:Valid
    private val dayTypePeriods: DayTypePeriodsSettingValidation,
    protected val structs: Collection<StructCo>,
) {
    abstract fun isContainsOtherTargetingStruct(): Boolean

    @Null(message = "\${validatedValue}")
    private fun getStructIds(): String? = when {
        isContainsOtherTargetingStruct() -> "Validation.RelayOtherTargetingStructs"
        else -> null
    }

    class TimeTargetingCreateValidation @TestOnly constructor(
        basic: BasicSettingCreateValidation,
        dayTypePeriods: DayTypePeriodsSettingValidation,
        structs: Collection<StructCo>
    ) : TimeTargetingValidation(basic, dayTypePeriods, structs) {
        override fun isContainsOtherTargetingStruct(): Boolean = structs.any { it.timeTargetingId != null }

        companion object {
            /**
             * ファクトリ関数
             *
             * @param form フォーム
             * @param country 国
             * @param structs タイムターゲティングに紐づけるストラクトのリスト
             * @return 生成した TimeTargetingCreateValidation のインスタンス
             */
            fun of(
                form: TimeTargetingCreateForm,
                country: CountryMaster,
                structs: Collection<StructCo>
            ): TimeTargetingCreateValidation = TimeTargetingCreateValidation(
                BasicSettingCreateValidation.of(form.basic, country),
                DayTypePeriodsSettingValidation.of(form.dayTypePeriods),
                structs
            )
        }
    }

    class TimeTargetingEditValidation @TestOnly constructor(
        basic: BasicSettingEditValidation,
        dayTypePeriods: DayTypePeriodsSettingValidation,
        structs: Collection<StructCo>,
        private val timeTargetingId: TimeTargetingId
    ) : TimeTargetingValidation(basic, dayTypePeriods, structs) {
        override fun isContainsOtherTargetingStruct(): Boolean =
            structs.any { it.timeTargetingId != null && it.timeTargetingId != timeTargetingId }

        companion object {
            /**
             * ファクトリ関数
             *
             * @param form フォーム
             * @param timeTargeting 現在のタイムターゲティング
             * @param country 国
             * @param structs タイムターゲティングに紐づけるストラクトのリスト
             * @return 生成した TimeTargetingEditValidation のインスタンス
             */
            fun of(
                form: TimeTargetingEditForm,
                timeTargeting: TimeTargeting,
                country: CountryMaster,
                structs: Collection<StructCo>
            ): TimeTargetingEditValidation {
                // 遷移可能なステータス変更かチェック
                timeTargeting.timeTargetingStatus.checkAllowedChange(form.basic.timeTargetingStatus)

                return TimeTargetingEditValidation(
                    BasicSettingEditValidation.of(form.basic, country, structs),
                    DayTypePeriodsSettingValidation.of(form.dayTypePeriods),
                    structs,
                    timeTargeting.timeTargetingId
                )
            }
        }
    }
}
