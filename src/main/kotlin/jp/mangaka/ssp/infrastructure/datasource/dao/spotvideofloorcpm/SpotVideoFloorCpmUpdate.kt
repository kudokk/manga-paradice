package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm
import java.math.BigDecimal
import java.time.LocalDate

data class SpotVideoFloorCpmUpdate(
    val spotId: SpotId,
    val aspectRatioId: AspectRatioId,
    val startDate: LocalDate,
    val floorCpm: BigDecimal,
) {
    companion object {
        /**
         * @param spotId 広告枠ID
         * @param form ビデオ設定のフォーム
         * @return spot_video_floor_cpmのSpotVideoFloorCpmUpdateオブジェクトのリスト
         */
        fun of(spotId: SpotId, form: VideoSettingForm): List<SpotVideoFloorCpmUpdate> = of(spotId, form.details)

        /**
         * @param spotId 広告枠ID
         * @param forms ビデオ詳細設定のフォームのリスト
         * @return spot_video_floor_cpmのUpdateオブジェクトのリスト
         */
        fun of(spotId: SpotId, forms: Collection<VideoDetailForm>): List<SpotVideoFloorCpmUpdate> =
            // バリデーション後に呼び出される想定
            forms
                // フロアCPMが未入力のときはDBへ登録を行わないようにフィルターする
                .filter { floorCpmDao -> floorCpmDao.floorCpm != null }
                // バリデーション後に呼び出される想定なので強制キャスト
                .map { SpotVideoFloorCpmUpdate(spotId, it.aspectRatioId!!, it.floorCpmStartDate!!, it.floorCpm!!) }
    }
}
