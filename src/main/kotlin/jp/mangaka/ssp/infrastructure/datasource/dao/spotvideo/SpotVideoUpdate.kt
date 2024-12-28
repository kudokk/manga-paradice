package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm

data class SpotVideoUpdate(
    val spotId: SpotId,
    val isFixedRotationAspectRatio: String
) {
    companion object {
        /**
         * @param spotId 広告枠ID
         * @param form ビデオ設定のForm
         * @return spot_videoのUpdateオブジェクト
         */
        fun of(spotId: SpotId, form: VideoSettingForm): SpotVideoUpdate = SpotVideoUpdate(
            spotId,
            form.isFixedRotationAspectRatio.toString()
        )
    }
}
