package jp.mangaka.ssp.presentation.controller.spot.form

import com.fasterxml.jackson.annotation.JsonFormat
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm.VideoDisplayPositionElementForm.DirectionType
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm.VideoDisplayPositionElementForm.DirectionType.Horizontal
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm.VideoDisplayPositionElementForm.DirectionType.Vertical
import java.math.BigDecimal
import java.time.LocalDate

data class VideoSettingForm(
    val rotationMax: Int?,
    val isFixedRotationAspectRatio: Boolean,
    val prLabelType: Int?,
    val details: List<VideoDetailForm>
) {
    data class VideoDetailForm(
        val aspectRatioId: AspectRatioId?,
        val isScalable: Boolean,
        val videoPlayerWidth: Int?,
        val closeButton: CloseButtonForm?,
        val displayPosition: VideoDisplayPositionForm?,
        val isAllowedDrag: Boolean,
        val isRoundedRectangle: Boolean,
        val floorCpm: BigDecimal?,
        // 編集時のみ使用する隠しパラメータ
        @field:JsonFormat(pattern = "yyyy-MM-dd")
        val floorCpmStartDate: LocalDate?
    ) {
        data class VideoDisplayPositionForm(
            val vertical: VideoDisplayPositionElementForm<Vertical>?,
            val horizontal: VideoDisplayPositionElementForm<Horizontal>?
        ) {
            data class VideoDisplayPositionElementForm<T : DirectionType>(val direction: T, val distance: Int?) {
                sealed interface DirectionType {
                    enum class Vertical : DirectionType {
                        top, bottom
                    }

                    enum class Horizontal : DirectionType {
                        right, left
                    }
                }
            }
        }
    }
}
