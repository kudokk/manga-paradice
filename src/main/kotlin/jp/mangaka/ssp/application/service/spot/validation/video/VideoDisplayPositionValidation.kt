package jp.mangaka.ssp.application.service.spot.validation.video

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm.VideoDisplayPositionElementForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm.VideoDisplayPositionElementForm.DirectionType
import org.hibernate.validator.constraints.Range

data class VideoDisplayPositionValidation(
    @field:Valid
    private val vertical: VideoDisplayPositionElementValidation?,
    @field:Valid
    private val horizontal: VideoDisplayPositionElementValidation?
) {
    /**
     * 親クラスのバリデーションで使用する
     * @return 垂直・水平方向がいずれかに設定がある場合は true
     */
    fun isNotEmpty(): Boolean = vertical != null || horizontal != null

    data class VideoDisplayPositionElementValidation(
        @field:NotNull(message = "Validation.Input")
        @field:Range(min = 0, max = 1000, message = "Validation.Number.Range")
        private val distance: Int?
    )

    companion object {
        /**
         * @param form 表示位置のForm
         * @return 表示位置のバリデーションオブジェクト
         */
        fun of(form: VideoDisplayPositionForm): VideoDisplayPositionValidation =
            VideoDisplayPositionValidation(form.vertical?.toValidation(), form.horizontal?.toValidation())

        // 方向種別が未入力ならその方向は未入力扱い
        private fun <T : DirectionType> VideoDisplayPositionElementForm<T>.toValidation() =
            VideoDisplayPositionElementValidation(this.distance)
    }
}
