package jp.mangaka.ssp.application.service.spot.validation

import jakarta.validation.Valid
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotNull
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm.ColorForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.hibernate.validator.constraints.Range
import org.jetbrains.annotations.TestOnly

data class CloseButtonValidation @TestOnly constructor(
    @field:NotNull(message = "Validation.Input")
    private val displayPosition: Int?,
    @field:Range(min = 0, max = 255, message = "Validation.Number.Range")
    private val displaySize: Int?,
    @field:Valid
    private val lineColor: ColorValidation?,
    @field:Valid
    private val backgroundColor: ColorValidation?,
    @field:Valid
    private val frameColor: ColorValidation?
) {
    data class ColorValidation @TestOnly constructor(
        @field:NotNull(message = "Validation.Input")
        @field:Range(min = 0, max = 255, message = "Validation.Number.Range")
        val red: Int?,
        @field:NotNull(message = "Validation.Input")
        @field:Range(min = 0, max = 255, message = "Validation.Number.Range")
        val green: Int?,
        @field:NotNull(message = "Validation.Input")
        @field:Range(min = 0, max = 255, message = "Validation.Number.Range")
        val blue: Int?,
        @field:Digits(integer = 1, fraction = 3, message = "Validation.Number.Digits")
        val opacity: Double
    ) {
        companion object {
            /**
             * @param form 配色のフォーム
             * @return 配色のバリデーションオブジェクト
             * @throws CompassManagerException 仕様上設定できない内容が入力されているとき
             */
            fun of(form: ColorForm): ColorValidation {
                checkOpacity(form)

                return ColorValidation(form.red, form.green, form.blue, form.opacity)
            }

            @TestOnly
            fun checkOpacity(form: ColorForm) {
                if (form.opacity !in 0.0..1.0) {
                    throw CompassManagerException("透過度に設定できない値が入力されています。")
                }
            }
        }
    }

    companion object {
        /**
         * @param form 閉じるボタンのForm
         * @return 閉じるボタンのバリデーションオブジェクト
         * @throws CompassManagerException 仕様上設定できない内容が入力されているとき
         */
        fun of(form: CloseButtonForm): CloseButtonValidation {
            checkDisplayPosition(form)

            return CloseButtonValidation(
                form.displayPosition,
                form.displaySize,
                form.lineColor?.let { ColorValidation.of(it) },
                form.backgroundColor?.let { ColorValidation.of(it) },
                form.frameColor?.let { ColorValidation.of(it) }
            )
        }

        @TestOnly
        fun checkDisplayPosition(form: CloseButtonForm) {
            if (form.displayPosition == null) return
            if (form.displayPosition !in 1..18) {
                throw CompassManagerException("表示位置に設定できない値が入力されています。")
            }
        }
    }
}
