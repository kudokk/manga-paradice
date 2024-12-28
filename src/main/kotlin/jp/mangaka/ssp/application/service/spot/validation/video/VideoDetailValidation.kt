package jp.mangaka.ssp.application.service.spot.validation.video

import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Null
import jp.mangaka.ssp.application.service.spot.validation.CloseButtonValidation
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import jp.mangaka.ssp.util.validation.ValidationUtils.DecimalConfig
import jp.mangaka.ssp.util.validation.ValidationUtils.validateBigDecimal
import org.hibernate.validator.constraints.Range
import org.jetbrains.annotations.TestOnly
import java.math.BigDecimal

data class VideoDetailValidation(
    @field:NotNull(message = "Validation.Input")
    private val aspectRatioId: AspectRatioId?,
    @field:NotNull(message = "Validation.Input")
    @field:Range(min = 1, max = 65535, message = "Validation.Number.Range")
    private val videoPlayerWidth: Int?,
    @field:Valid
    private val closeButton: CloseButtonValidation?,
    @field:Valid
    private val displayPosition: VideoDisplayPositionValidation?,
    private val floorCpm: BigDecimal?,
    private val isOverlayDisplayControl: Boolean
) {
    // 表示制御オンのオーバーレイ広告の場合は表示位置のいずれかが入力必須
    @AssertTrue(message = "Validation.Input")
    private fun isDisplayPosition(): Boolean = !isOverlayDisplayControl || displayPosition?.isNotEmpty() == true

    @Null(message = "\${validatedValue}")
    private fun getFloorCpm() = floorCpm?.let { validateBigDecimal(it, floorCpmConfig) }

    companion object {
        private val floorCpmConfig =
            DecimalConfig(BigDecimal("0.00000000"), BigDecimal("9999999999.99999999"), 10, 8)

        /**
         * @param form フォーム
         * @param userType ユーザー種別
         * @param site サイト
         * @param displayType 表示種別
         * @param isDisplayControl 表示制御フラグ
         * @param isExistVideo 既存の設定が存在するか
         * @return ビデオ詳細設定のバリデーションオブジェクト
         */
        fun of(
            form: VideoDetailForm,
            userType: UserType,
            site: Site?,
            displayType: Spot.DisplayType?,
            isDisplayControl: Boolean,
            isExistVideo: Boolean = false
        ): VideoDetailValidation {
            val isOverlay = displayType?.isOverlay() ?: false
            val isOverlayAndDisplayControlled = isOverlay && isDisplayControl

            // 仕様上あり得ない設定がある場合はパラメーター改ざんのためシステムエラー
            checkMaStaffOnly(form, userType, isExistVideo)
            checkNonPcSiteOnly(form, site?.platformId?.isPc() ?: false)
            checkOverlayOnly(form, isOverlay)
            checkOverlayDisplayControlledOnly(form, isOverlayAndDisplayControlled)

            return VideoDetailValidation(
                form.aspectRatioId,
                form.videoPlayerWidth,
                form.closeButton?.let { CloseButtonValidation.of(it) },
                form.displayPosition?.let { VideoDisplayPositionValidation.of(it) },
                form.floorCpm,
                isOverlayAndDisplayControlled
            )
        }

        @TestOnly
        fun checkMaStaffOnly(form: VideoDetailForm, userType: UserType, isExistVideo: Boolean) {
            if (isExistVideo || userType.isMaStaff()) return

            if (form.isScalable || form.closeButton != null) {
                throw CompassManagerException("マイクロアド社員以外では設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkNonPcSiteOnly(form: VideoDetailForm, isPcSite: Boolean) {
            if (!isPcSite) return

            if (form.isScalable) {
                throw CompassManagerException("PCサイトで設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkOverlayOnly(form: VideoDetailForm, isOverlay: Boolean) {
            if (isOverlay) return

            if (form.closeButton != null) {
                throw CompassManagerException("オーバーレイ広告以外で設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkOverlayDisplayControlledOnly(form: VideoDetailForm, isOverlayDisplayControl: Boolean) {
            if (isOverlayDisplayControl) return

            if (form.displayPosition != null || form.isRoundedRectangle || form.isAllowedDrag) {
                throw CompassManagerException("表示制御ありのオーバーレイ広告以外で設定できない条件が入力されています。")
            }
        }
    }
}
