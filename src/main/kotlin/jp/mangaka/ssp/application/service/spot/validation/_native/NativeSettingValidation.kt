package jp.mangaka.ssp.application.service.spot.validation._native

import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.apache.commons.lang3.ObjectUtils
import org.jetbrains.annotations.TestOnly

data class NativeSettingValidation(
    @field:Valid
    private val standard: NativeStandardValidation?,
    @field:Valid
    private val video: NativeVideoValidation?
) {
    @AssertTrue(message = "Validation.Formats")
    private fun isFormats(): Boolean = ObjectUtils.anyNotNull(standard, video)

    companion object {
        /**
         * @param form フォーム
         * @param userType ユーザー種別
         * @param site サイト
         * @param isDisplayControl 基本設定の表示制御
         * @param isExistStandard 既存のネイティブデザイン設定が存在するか
         * @param isExistVideo 既存のネイティブ動画デザイン設定が存在するか
         * @return ネイティブ設定のバリデーションオブジェクト
         */
        fun of(
            form: NativeSettingForm,
            userType: UserType,
            site: Site?,
            isDisplayControl: Boolean,
            isExistStandard: Boolean = false,
            isExistVideo: Boolean = false
        ): NativeSettingValidation {
            checkNativeStandard(form, isDisplayControl)

            return NativeSettingValidation(
                form.standard?.let { NativeStandardValidation.of(it, userType, isExistStandard) },
                form.video?.let { NativeVideoValidation.of(it, userType, site, isExistVideo) }
            )
        }

        @TestOnly
        fun checkNativeStandard(form: NativeSettingForm, isDisplayControl: Boolean) {
            if (isDisplayControl && form.standard != null) {
                throw CompassManagerException("ネイティブデザインを設定できない条件が入力されています。")
            }
        }
    }
}
