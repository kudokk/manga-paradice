package jp.mangaka.ssp.application.service.spot.validation._native

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jp.mangaka.ssp.application.service.spot.validation.CloseButtonValidation
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeVideoForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.jetbrains.annotations.TestOnly

data class NativeVideoValidation(
    @field:NotNull(message = "Validation.Input")
    private val nativeTemplateId: NativeTemplateId?,
    @field:Valid
    private val closeButton: CloseButtonValidation?
) {
    companion object {
        /**
         * @param form フォーム
         * @param userType ユーザー種別
         * @param site サイト
         * @param isExistVideo 既存のネイティブ動画デザイン設定が存在するか
         * @return ネイティブ動画デザインのバリデーションオブジェクト
         */
        fun of(
            form: NativeVideoForm,
            userType: UserType,
            site: Site?,
            isExistVideo: Boolean = false
        ): NativeVideoValidation {
            val isPcSite = site?.platformId?.isPc() ?: false

            // 仕様上あり得ない設定がある場合はパラメーター改ざんのためシステムエラー
            checkMaStaffOnly(form, userType, isExistVideo)
            checkIsScalable(form, isPcSite)

            return NativeVideoValidation(
                form.nativeTemplateId,
                form.closeButton?.let { CloseButtonValidation.of(it) }
            )
        }

        @TestOnly
        fun checkMaStaffOnly(form: NativeVideoForm, userType: UserType, isExistVideo: Boolean) {
            if (isExistVideo || userType.isMaStaff()) return
            if (form.closeButton != null || form.isScalable) {
                throw CompassManagerException("マイクロアド社員以外では設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkIsScalable(form: NativeVideoForm, isPcSite: Boolean) {
            if (!form.isScalable) return
            if (isPcSite) {
                throw CompassManagerException("広告拡大を設定できない条件が入力されています。")
            }
        }
    }
}
