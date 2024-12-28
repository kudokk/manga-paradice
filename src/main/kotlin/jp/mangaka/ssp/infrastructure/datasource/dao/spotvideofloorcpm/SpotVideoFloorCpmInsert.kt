package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm
import java.math.BigDecimal
import java.time.LocalDate

data class SpotVideoFloorCpmInsert(
    val spotId: SpotId,
    val aspectRatioId: AspectRatioId,
    val floorCpm: BigDecimal
) {
    val startDate: LocalDate = LocalDate.now()
    val endDate: LocalDate? = null

    companion object {
        /**
         * @param spotId 広告枠ID
         * @param form ビデオ設定のフォーム
         * @return spot_video_floor_cpmのInsertオブジェクトのリスト
         */
        fun of(spotId: SpotId, form: VideoSettingForm): List<SpotVideoFloorCpmInsert> = of(spotId, form.details)

        /**
         * @param spotId 広告枠ID
         * @param forms ビデオ詳細設定のフォームのリスト
         * @return spot_video_floor_cpmのInsertオブジェクトのリスト
         */
        fun of(spotId: SpotId, forms: Collection<VideoDetailForm>): List<SpotVideoFloorCpmInsert> =
            forms
                // フロアCPMが未入力のときはDBへ登録を行わないようにフィルターする
                .filter { floorCpmDao -> floorCpmDao.floorCpm != null }
                // バリデーション後に呼び出される想定なので強制キャスト
                .map { SpotVideoFloorCpmInsert(spotId, it.aspectRatioId!!, it.floorCpm!!) }
    }
}
