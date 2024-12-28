package jp.mangaka.ssp.application.service.spot.validation._native

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jp.mangaka.ssp.application.service.spot.validation.CloseButtonValidation
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeStandardForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.jetbrains.annotations.TestOnly

data class NativeStandardValidation(
    @field:NotNull(message = "Validation.Input")
    private val nativeTemplateId: NativeTemplateId?,
    @field:Valid
    private val closeButton: CloseButtonValidation?
) {
    companion object {
        /**
         * @param form フォーム
         * @param userType ユーザー種別
         * @param isExistStandard 既存のネイティブデザイン設定が存在するか
         * @return ネイティブデザインのバリデーションオブジェクト
         */
        fun of(
            form: NativeStandardForm,
            userType: UserType,
            isExistStandard: Boolean
        ): NativeStandardValidation {
            // 仕様上あり得ない設定がある場合はパラメーター改ざんのためシステムエラー
            checkMaStaffOnly(form, userType, isExistStandard)

            return NativeStandardValidation(
                form.nativeTemplateId,
                form.closeButton?.let { CloseButtonValidation.of(it) }
            )
        }

        @TestOnly
        fun checkMaStaffOnly(form: NativeStandardForm, userType: UserType, isExistStandard: Boolean) {
            if (isExistStandard || userType.isMaStaff()) return
            if (form.closeButton != null) {
                throw CompassManagerException("マイクロアド社員以外では設定できない条件が入力されています。")
            }
        }
    }
}
