package jp.mangaka.ssp.application.service.spot.validation

import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jp.mangaka.ssp.application.service.spot.validation.banner.BannerSettingValidation
import jp.mangaka.ssp.application.service.spot.validation.banner.SpotSizeTypeDeleteRule
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBannerEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotMaxSizeForm

data class SpotBannerEditValidation(
    @field:Valid
    private val banner: BannerSettingValidation?,
    @field:Valid
    private val structSizeTypes: List<StructSizeTypeError>,
    private val hasOtherFormat: Boolean
) {
    @AssertTrue(message = "Validation.Spot.Format.NotEmpty")
    fun isFormats(): Boolean = banner != null || hasOtherFormat

    companion object {
        /**
         * @param form 広告枠バナー設定編集のForm
         * @param userType ユーザー種別
         * @param spot 広告枠
         * @param site サイト
         * @param isDisplayControl 表示制御有無
         * @param hasOtherFormat バナー設定以外のフォーマットの有無
         * @param deleteSpotSizeTypes 削除対象のサイズ種別のリスト
         * @param spotSizeTypeDeleteRule サイズ種別の削除ルール
         * @param isExistBanner 既存のバナー設定が存在するか
         * @return 広告枠バナー設定編集のValidationオブジェクト
         */
        fun of(
            form: SpotBannerEditForm,
            userType: UserType,
            spot: Spot,
            site: Site,
            isDisplayControl: Boolean,
            hasOtherFormat: Boolean,
            deleteSpotSizeTypes: Collection<SizeTypeInfo>,
            spotSizeTypeDeleteRule: SpotSizeTypeDeleteRule,
            isExistBanner: Boolean
        ): SpotBannerEditValidation = SpotBannerEditValidation(
            form.banner?.let { banner ->
                BannerSettingValidation.of(
                    banner,
                    userType,
                    site,
                    spot.upstreamType,
                    spot.isAmp,
                    isDisplayControl,
                    spot.deliveryMethod.isJs() && spot.displayType.isOverlay(),
                    spot.deliveryMethod.isJs() || spot.displayType.isInline(),
                    SpotMaxSizeForm(spot.width, spot.height),
                    isExistBanner
                )
            },
            StructSizeTypeError.of(spot.spotId, deleteSpotSizeTypes, spotSizeTypeDeleteRule),
            hasOtherFormat
        )
    }
}
