package jp.mangaka.ssp.application.service.spot.validation.banner

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Null
import jp.mangaka.ssp.application.service.spot.validation.CloseButtonValidation
import jp.mangaka.ssp.application.service.spot.validation.CloseButtonValidation.ColorValidation
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster
import jp.mangaka.ssp.presentation.controller.spot.form.BannerSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotMaxSizeForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.jetbrains.annotations.TestOnly

data class BannerSettingValidation @TestOnly constructor(
    @field:NotEmpty(message = "Validation.List.NotEmpty")
    @field:Valid
    private val sizeTypes: List<SizeTypeValidation>,
    private val isScalable: Boolean,
    private val isDisplayScrolling: Boolean,
    @field:Valid
    private val closeButton: CloseButtonValidation?,
    private val decorationId: DecorationId?,
    private val isPcSite: Boolean,
    private val spotMaxSizeForm: SpotMaxSizeForm?
) {
    @Null(message = "\${validatedValue}")
    private fun getSizeTypes(): String? = when {
        sizeTypes.any { it.height == 0 } -> "Validation.SizeTypes.Height.Max"
        isPcSite && sizeTypes.any { it.width == 0 } -> "Validation.SizeTypes.Width.PC"
        spotMaxSizeForm?.width?.let { max -> sizeTypes.maxOf { it.width } > max } ?: false ->
            "Validation.SizeTypes.Width"

        spotMaxSizeForm?.height?.let { max -> sizeTypes.maxOf { it.height } > max } ?: false ->
            "Validation.SizeTypes.Height"

        else -> null
    }

    companion object {
        /**
         * @param form フォーム
         * @param userType ユーザー種別
         * @param site サイト
         * @param upstreamType 上位の販売者種別
         * @param isAmp 基本設定のAMP対応
         * @param isDisplayControl 基本設定の表示制御
         * @param isJsByOverlay 配信手法がJS かつ 表示種別がオーバーレイが設定されているか
         * @param isJsOrInline 配信手法がJS もしくは 表示種別がインライン で設定されているか
         * @param spotMaxSizeForm 基本設定の横幅x縦幅のForm
         * @param isExistBanner 既存のバナー設定が存在するか
         * @return バナー設定のバリデーションオブジェクト
         * @throws CompassManagerException 仕様上設定できない内容が入力されているとき
         */
        fun of(
            form: BannerSettingForm,
            userType: UserMaster.UserType,
            site: Site?,
            upstreamType: UpstreamType,
            isAmp: Boolean,
            isDisplayControl: Boolean,
            isJsByOverlay: Boolean,
            isJsOrInline: Boolean,
            spotMaxSizeForm: SpotMaxSizeForm?,
            isExistBanner: Boolean = false
        ): BannerSettingValidation {
            val isPcSite = site?.platformId?.isPc() ?: false

            // 仕様上あり得ない設定がある場合はパラメーター改ざんのためシステムエラー
            checkMaStaffOnly(form, userType, isExistBanner)
            checkIsScalable(form, upstreamType, isAmp, isJsOrInline, isPcSite)
            checkIsDisplayScrolling(form, isDisplayControl, isJsByOverlay)
            checkCloseButton(form, upstreamType, isJsByOverlay)
            checkDecorationId(form, isDisplayControl, isJsByOverlay)

            return BannerSettingValidation(
                form.sizeTypes.map { SizeTypeValidation(it.width, it.height) },
                form.isScalable,
                form.isDisplayScrolling,
                form.closeButton?.let { closeButton ->
                    CloseButtonValidation(
                        closeButton.displayPosition,
                        closeButton.displaySize,
                        closeButton.lineColor?.let { ColorValidation(it.red, it.green, it.blue, it.opacity) },
                        closeButton.backgroundColor?.let { ColorValidation(it.red, it.green, it.blue, it.opacity) },
                        closeButton.frameColor?.let { ColorValidation(it.red, it.green, it.blue, it.opacity) }
                    )
                },
                form.decorationId,
                isPcSite,
                spotMaxSizeForm
            )
        }

        @TestOnly
        fun checkMaStaffOnly(form: BannerSettingForm, userType: UserMaster.UserType, isExistBanner: Boolean) {
            if (isExistBanner || userType.isMaStaff()) return
            if (form.isScalable || form.closeButton != null) {
                throw CompassManagerException("マイクロアド社員以外では設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkIsScalable(
            form: BannerSettingForm,
            upstreamType: UpstreamType,
            isAmp: Boolean,
            isJsOrInline: Boolean,
            isPcSite: Boolean
        ) {
            if (!form.isScalable) return
            if (!upstreamType.isNone() || isAmp || !isJsOrInline || isPcSite) {
                throw CompassManagerException("広告拡大を設定できない条件が入力されています")
            }
        }

        @TestOnly
        fun checkIsDisplayScrolling(form: BannerSettingForm, isDisplayControl: Boolean, isJsByOverlay: Boolean) {
            if (form.isDisplayScrolling) return

            if (!isDisplayControl || !isJsByOverlay) {
                throw CompassManagerException("スクロール中表示を無効にできない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkCloseButton(form: BannerSettingForm, upstreamType: UpstreamType, isJsByOverlay: Boolean) {
            if (form.closeButton == null) return

            if (!upstreamType.isNone() || !isJsByOverlay) {
                throw CompassManagerException("閉じるボタンを設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkDecorationId(form: BannerSettingForm, isDisplayControl: Boolean, isJsByOverlay: Boolean) {
            if (form.decorationId == null) return
            if (!isDisplayControl || !isJsByOverlay) {
                throw CompassManagerException("帯を設定できない条件が入力されています。")
            }
        }
    }
}
