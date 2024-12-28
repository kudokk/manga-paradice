package jp.mangaka.ssp.presentation.controller.spot.view

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio

data class AspectRatioView(
    val aspectRatioId: AspectRatioId,
    val width: Int,
    val height: Int,
    val videoType: VideoType
) {
    enum class VideoType {
        Inline, Wipe, FullWide;

        companion object {
            /**
             * @param aspectRatioId アスペクト比ID
             * @return 動画種別
             */
            fun of(aspectRatioId: AspectRatioId): VideoType = when {
                aspectRatioId.isWipeVideo() -> Wipe
                aspectRatioId.isFullWideVideo() -> FullWide
                else -> Inline
            }
        }
    }

    companion object {
        /**
         * @param entities アスペクト比の Entity のリスト
         * @return アスペクト比の View のリスト
         */
        fun of(entities: Collection<AspectRatio>): List<AspectRatioView> = entities.map {
            AspectRatioView(it.aspectRatioId, it.width, it.height, VideoType.of(it.aspectRatioId))
        }
    }
}
