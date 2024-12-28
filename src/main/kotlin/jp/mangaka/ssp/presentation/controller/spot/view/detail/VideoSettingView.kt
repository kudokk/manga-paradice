package jp.mangaka.ssp.presentation.controller.spot.view.detail

import com.fasterxml.jackson.annotation.JsonFormat
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideo
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpm
import jp.mangaka.ssp.presentation.controller.spot.view.AspectRatioView
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

data class VideoSettingView(
    val rotationMax: Int,
    val isFixedRotationAspectRatio: Boolean,
    val prLabelType: Int?,
    val details: List<VideoDetailView>
) {
    data class VideoDetailView(
        val aspectRatio: AspectRatioView,
        val isScalable: Boolean,
        val videoPlayerWidth: Int,
        val closeButton: CloseButtonView?,
        val displayPosition: DisplayPositionView?,
        val isAllowedDrag: Boolean,
        val isRoundedRectangle: Boolean,
        val floorCpm: String?,
        @field:JsonFormat(pattern = "yyyy-MM-dd")
        val floorCpmStartDate: LocalDate?
    ) {
        data class DisplayPositionView(
            val vertical: VideoDisplayPositionElementView?,
            val horizontal: VideoDisplayPositionElementView?
        ) {
            data class VideoDisplayPositionElementView(val direction: String, val distance: Int)

            companion object {
                /**
                 * @param spotBannerDisplay 広告枠ビデオ表示
                 * @return 表示位置のView
                 */
                fun of(spotBannerDisplay: SpotVideoDisplay): DisplayPositionView = DisplayPositionView(
                    spotBannerDisplay.toVertical(),
                    spotBannerDisplay.toHorizontal()
                )

                private fun SpotVideoDisplay.toVertical() = when {
                    positionTop != null -> VideoDisplayPositionElementView("top", positionTop)
                    positionBottom != null -> VideoDisplayPositionElementView("bottom", positionBottom)
                    else -> null
                }

                private fun SpotVideoDisplay.toHorizontal() = when {
                    positionLeft != null -> VideoDisplayPositionElementView("left", positionLeft)
                    positionRight != null -> VideoDisplayPositionElementView("right", positionRight)
                    else -> null
                }
            }
        }

        companion object {
            /**
             * @param spotVideoDisplays 広告枠ビデオ表示のリスト
             * @param spotVideoFloorCpms 広告枠ビデオ固定単価のリスト
             * @param aspectRatios アスペクト比のリスト
             * @return ビデオ詳細設定のViwのリスト
             */
            fun of(
                spotVideoDisplays: Collection<SpotVideoDisplay>,
                spotVideoFloorCpms: Collection<SpotVideoFloorCpm>,
                aspectRatios: Collection<AspectRatio>
            ): List<VideoDetailView> {
                val today = LocalDate.now()
                val spotVideoFloorCpmMap = spotVideoFloorCpms
                    .groupBy { it.aspectRatioId }
                    .mapValues { (_, floorCpms) ->
                        // アスペクト比に対して複数の期間の設定が存在し得るので、期間内で直近の1件を表示する
                        floorCpms
                            .filter { it.containsPeriod(today) }
                            .minByOrNull { ChronoUnit.DAYS.between(today, it.startDate).absoluteValue }
                    }
                val aspectRatioViewMap = AspectRatioView.of(aspectRatios).associateBy { it.aspectRatioId }

                return spotVideoDisplays.map {
                    VideoDetailView(
                        aspectRatioViewMap.getValue(it.aspectRatioId),
                        it.isScalable,
                        it.width,
                        if (it.closeButtonType != null) {
                            CloseButtonView.of(
                                it.closeButtonType,
                                it.closeButtonSize,
                                it.closeButtonLineColor,
                                it.closeButtonBgColor,
                                it.closeButtonFrameColor
                            )
                        } else {
                            null
                        },
                        if (it.isDisplayControl()) {
                            DisplayPositionView.of(it)
                        } else {
                            null
                        },
                        it.isAllowedDrag,
                        it.isRoundedRectangle,
                        spotVideoFloorCpmMap[it.aspectRatioId]?.floorCpm?.toPlainString(),
                        spotVideoFloorCpmMap[it.aspectRatioId]?.startDate
                    )
                }
            }
        }
    }

    companion object {
        /**
         * @param spot 広告枠
         * @param spotVideo 広告枠ビデオ設定
         * @param spotVideoDisplays 広告枠ビデオ表示のリスト
         * @param spotVideoFloorCpms 広告枠ビデオ固定単価のリスト
         * @param aspectRatios アスペクト比のリスト
         * @return 広告枠ビデオ設定のView
         */
        fun of(
            spot: Spot,
            spotVideo: SpotVideo,
            spotVideoDisplays: Collection<SpotVideoDisplay>,
            spotVideoFloorCpms: Collection<SpotVideoFloorCpm>,
            aspectRatios: Collection<AspectRatio>
        ): VideoSettingView = VideoSettingView(
            spot.rotationMax,
            spotVideo.isFixedRotationAspectRatio,
            // PRラベル表示はすべて同じものが設定されている想定
            spotVideoDisplays.first().prLabelType,
            VideoDetailView.of(spotVideoDisplays, spotVideoFloorCpms, aspectRatios)
        )
    }
}
