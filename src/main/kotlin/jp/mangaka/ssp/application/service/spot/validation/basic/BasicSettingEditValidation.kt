package jp.mangaka.ssp.application.service.spot.validation.basic

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Size
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingEditForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.apache.commons.lang3.ObjectUtils
import org.hibernate.validator.constraints.URL
import org.jetbrains.annotations.TestOnly

data class BasicSettingEditValidation(
    @field:NotBlank(message = "Validation.Input")
    @field:Size(max = 85, message = "Validation.Text.Range")
    private val spotName: String?,
    @field:Valid
    private val spotMaxSize: SpotMaxSizeValidation?,
    @field:Size(max = 85, message = "Validation.Text.Range")
    private val description: String?,
    @field:URL(message = "Validation.Url.Format")
    @field:Size(max = 1024, message = "Validation.Text.Range")
    private val pageUrl: String?,
    private val sizeTypes: Collection<SizeTypeInfo>
) {
    @Null(message = "\${validatedValue}")
    private fun getSizeTypes(): String? = when {
        sizeTypes.isEmpty() -> null
        spotMaxSize?.width?.let { max -> sizeTypes.maxOf { it.width } > max } ?: false ->
            "Validation.SizeTypes.Width"
        spotMaxSize?.height?.let { max -> sizeTypes.maxOf { it.height } > max } ?: false ->
            "Validation.SizeTypes.Height"
        else -> null
    }

    companion object {
        /**
         * @param form 基本設定編集のForm
         * @param spot 広告枠
         * @param userType ユーザー種別
         * @param isDisplayControl 表示制御有無
         * @param sizeTypes サイズ種別のリスト
         * @return 基本設定のバリデーションオブジェクト
         */
        fun of(
            form: BasicSettingEditForm,
            spot: Spot,
            userType: UserType,
            isDisplayControl: Boolean,
            sizeTypes: Collection<SizeTypeInfo>
        ): BasicSettingEditValidation {
            // 仕様上あり得ない設定がある場合はパラメーター改ざんのためシステムエラー
            checkSpotMaxSize(form, spot, isDisplayControl)

            return BasicSettingEditValidation(
                form.spotName,
                form.spotMaxSize?.let { SpotMaxSizeValidation(it.width, it.height) },
                form.description,
                form.pageUrl,
                sizeTypes
            )
        }

        @TestOnly
        fun checkMaStaffOnly(form: BasicSettingEditForm, userType: UserType) {
            if (userType.isMaStaff()) return
            if (form.pageUrl != null) {
                throw CompassManagerException("マイクロアド社員以外では設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkSpotMaxSize(form: BasicSettingEditForm, spot: Spot, isDisplayControl: Boolean) {
            if (ObjectUtils.anyNull(form.spotMaxSize, spot.deliveryMethod, spot.displayType)) return

            if (!spot.upstreamType.isNone() ||
                !spot.deliveryMethod.isJs() ||
                !(spot.displayType.isInline() || (spot.displayType.isOverlay() && !isDisplayControl))
            ) {
                throw CompassManagerException("固定表示（横ｘ縦）を設定できない条件が入力されています。")
            }
        }
    }
}
