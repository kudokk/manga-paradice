package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import java.time.LocalTime

data class SpotVideoInsert(
    val spotId: SpotId,
    val isFixedRotationAspectRatio: String
) {
    val durationMin: LocalTime? = null
    val durationMax: LocalTime? = null

    companion object {
        /**
         * @param spotId 広告枠ID
         * @param form ビデオ設定のForm
         * @return spot_videoのInsertオブジェクト
         */
        fun of(spotId: SpotId, form: VideoSettingForm): SpotVideoInsert = SpotVideoInsert(
            spotId,
            form.isFixedRotationAspectRatio.toString()
        )
    }
}
