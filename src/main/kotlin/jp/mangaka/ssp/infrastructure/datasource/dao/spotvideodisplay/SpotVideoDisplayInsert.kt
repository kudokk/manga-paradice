package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm.VideoDisplayPositionElementForm.DirectionType.Horizontal
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm.VideoDisplayPositionElementForm.DirectionType.Vertical
import kotlin.math.ceil

data class SpotVideoDisplayInsert(
    val spotId: SpotId,
    val aspectRatioId: AspectRatioId,
    val width: Int,
    val height: Int,
    val positionTop: Int?,
    val positionBottom: Int?,
    val positionLeft: Int?,
    val positionRight: Int?,
    val isScalable: String,
    val isAllowedDrag: String,
    val isRoundedRectangle: String,
    val closeButtonType: Int?,
    val closeButtonSize: Int?,
    val closeButtonLineColor: String?,
    val closeButtonBgColor: String?,
    val closeButtonFrameColor: String?,
    val prLabelType: Int?
) {
    val isScrollEndControl: String = true.toString()
    val isDisplayScrolling: String = true.toString()
    val isAllowDrag: String = false.toString()

    companion object {
        /**
         * @param spotId 広告枠ID
         * @param form ビデオ設定のForm
         * @param aspectRatios アスペクト比のリスト
         * @return spot_video_displayのInsertオブジェクト
         */
        fun of(
            spotId: SpotId,
            form: VideoSettingForm,
            aspectRatios: Collection<AspectRatio>
        ): List<SpotVideoDisplayInsert> = of(spotId, form.details, form.prLabelType, aspectRatios)

        /**
         * @param spotId 広告枠ID
         * @param forms ビデオ詳細設定のFormのリスト
         * @param prLabelType PRラベル種別
         * @param aspectRatios アスペクト比のリスト
         * @return spot_video_displayのInsertオブジェクト
         */
        fun of(
            spotId: SpotId,
            forms: Collection<VideoDetailForm>,
            prLabelType: Int?,
            aspectRatios: Collection<AspectRatio>
        ): List<SpotVideoDisplayInsert> {
            // バリデーション後に呼び出される想定
            val aspectRatioMap = aspectRatios.associateBy { it.aspectRatioId }
            return forms.map { detail ->
                SpotVideoDisplayInsert(
                    spotId,
                    detail.aspectRatioId!!,
                    detail.videoPlayerWidth!!,
                    aspectRatioMap.getValue(detail.aspectRatioId).let {
                        ceil((detail.videoPlayerWidth * it.height) / it.width.toDouble()).toInt()
                    },
                    detail.displayPosition?.vertical?.let {
                        if (it.direction == Vertical.top) it.distance!! else null
                    },
                    detail.displayPosition?.vertical?.let {
                        if (it.direction == Vertical.bottom) it.distance!! else null
                    },
                    detail.displayPosition?.horizontal?.let {
                        if (it.direction == Horizontal.left) it.distance!! else null
                    },
                    detail.displayPosition?.horizontal?.let {
                        if (it.direction == Horizontal.right) it.distance!! else null
                    },
                    detail.isScalable.toString(),
                    detail.isAllowedDrag.toString(),
                    detail.isRoundedRectangle.toString(),
                    detail.closeButton?.displayPosition,
                    detail.closeButton?.displaySize,
                    detail.closeButton?.lineColor?.rgba(),
                    detail.closeButton?.backgroundColor?.rgba(),
                    detail.closeButton?.frameColor?.rgba(),
                    prLabelType
                )
            }
        }
    }
}
