package jp.mangaka.ssp.application.service.spot.validation

import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jp.mangaka.ssp.application.service.spot.validation._native.NativeSettingValidation
import jp.mangaka.ssp.application.service.spot.validation.banner.SpotSizeTypeDeleteRule
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.SpotNativeEditForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.jetbrains.annotations.TestOnly

data class SpotNativeEditValidation(
    @field:Valid
    val native: NativeSettingValidation?,
    @field:Valid
    private val structSizeTypes: List<StructSizeTypeError>,
    private val hasOtherFormat: Boolean
) {
    @AssertTrue(message = "Validation.Spot.Format.NotEmpty")
    fun isFormats(): Boolean = native != null || hasOtherFormat

    companion object {
        /**
         * ファクトリ関数
         *
         * @param form フォーム
         * @param userType ユーザー種別
         * @param spot 広告枠
         * @param site サイト
         * @param isDisplayControl 表示制御フラグ
         * @param hasOtherFormat ネイティブ以外のフォーマットが設定されているか
         * @param deleteSpotSizeTypes 広告枠から紐づけを削除するサイズ種別
         * @param spotSizeTypeDeleteRule サイズの駆除ルール
         * @param isExistStandard 既存のネイティブデザイン設定が存在するか
         * @param isExistVideo 既存のネイティブ動画デザイン設定が存在するか
         * @return 生成したバリデーションオブジェクト
         */
        fun of(
            form: SpotNativeEditForm,
            userType: UserType,
            spot: Spot,
            site: Site,
            isDisplayControl: Boolean,
            hasOtherFormat: Boolean,
            deleteSpotSizeTypes: Collection<SizeTypeInfo>,
            spotSizeTypeDeleteRule: SpotSizeTypeDeleteRule,
            isExistStandard: Boolean,
            isExistVideo: Boolean
        ): SpotNativeEditValidation {
            // 仕様上あり得ない設定がある場合はパラメーター改ざんのためシステムエラー
            checkAllowNative(form, spot)

            return SpotNativeEditValidation(
                form.native?.let {
                    NativeSettingValidation.of(
                        it,
                        userType,
                        site,
                        isDisplayControl,
                        isExistStandard,
                        isExistVideo
                    )
                },
                StructSizeTypeError.of(spot.spotId, deleteSpotSizeTypes, spotSizeTypeDeleteRule),
                hasOtherFormat
            )
        }

        @TestOnly
        fun checkAllowNative(form: SpotNativeEditForm, spot: Spot) {
            if (form.native == null) return

            if (!spot.isAllowNative()) {
                throw CompassManagerException("ネイティブを設定できない条件が入力されています。")
            }
        }
    }
}
