package jp.mangaka.ssp.application.service.spot.validation

import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jp.mangaka.ssp.application.service.spot.validation._native.NativeSettingValidation
import jp.mangaka.ssp.application.service.spot.validation.banner.BannerSettingValidation
import jp.mangaka.ssp.application.service.spot.validation.basic.BasicSettingCreateValidation
import jp.mangaka.ssp.application.service.spot.validation.dsp.DspValidation
import jp.mangaka.ssp.application.service.spot.validation.video.VideoSettingValidation
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.apache.commons.lang3.ObjectUtils
import org.jetbrains.annotations.TestOnly

data class SpotCreateValidation @TestOnly constructor(
    @field:Valid
    private val basic: BasicSettingCreateValidation,
    @field:Valid
    private val dsps: List<DspValidation>,
    @field:Valid
    private val banner: BannerSettingValidation?,
    @field:Valid
    private val native: NativeSettingValidation?,
    @field:Valid
    private val video: VideoSettingValidation?
) {
    @AssertTrue(message = "Validation.Spot.Format.NotEmpty")
    fun isFormats(): Boolean = ObjectUtils.anyNotNull(banner, native, video)

    companion object {
        /**
         * @param form フォーム
         * @param userType ユーザー種別
         * @param site サイト
         * @return 広告枠作成のバリデーションオブジェクト
         * @throws CompassManagerException 仕様上設定できない内容が入力されているとき
         */
        fun of(form: SpotCreateForm, userType: UserType, site: Site?): SpotCreateValidation {
            // 仕様上あり得ない設定がある場合はパラメーター改ざんのためシステムエラー
            checkMaStaffOnly(form, userType)
            checkOptionalFormat(form)

            return SpotCreateValidation(
                form.basic.let { BasicSettingCreateValidation.of(it, userType, site) },
                form.dsps.map { DspValidation(it.dspId, it.bidAdjust, it.floorCpm) },
                form.banner?.let {
                    BannerSettingValidation.of(
                        it,
                        userType,
                        site,
                        form.basic.upstreamType,
                        form.basic.isAmp,
                        form.basic.isDisplayControl,
                        form.basic.deliveryMethod?.isJs() == true && form.basic.displayType?.isOverlay() == true,
                        form.basic.deliveryMethod?.isJs() == true || form.basic.displayType?.isInline() == true,
                        form.basic.spotMaxSize
                    )
                },
                form.native?.let {
                    NativeSettingValidation.of(it, userType, site, form.basic.isDisplayControl)
                },
                form.video?.let {
                    VideoSettingValidation.of(it, userType, site, form.basic.displayType, form.basic.isDisplayControl)
                }
            )
        }

        @TestOnly
        fun checkMaStaffOnly(form: SpotCreateForm, userType: UserType) {
            if (userType.isMaStaff()) return
            if (form.dsps.isNotEmpty()) {
                throw CompassManagerException("マイクロアド社員以外では設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkOptionalFormat(form: SpotCreateForm) {
            if (ObjectUtils.allNull(form.native, form.video)) return

            if (isNotAllowOptionalFormat(form.basic)) {
                throw CompassManagerException("ネイティブ・ビデオを設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun isNotAllowOptionalFormat(form: BasicSettingCreateForm) =
            form.upstreamType.isPrebidjs() || form.deliveryMethod?.isSdk() == true || form.isAmp
    }
}
