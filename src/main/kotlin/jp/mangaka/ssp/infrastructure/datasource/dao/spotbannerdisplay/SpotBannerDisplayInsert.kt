package jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay

import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.BannerSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm

data class SpotBannerDisplayInsert(
    val spotId: SpotId,
    val positionBottom: Int?,
    val isScalable: String,
    val isDisplayScrolling: String,
    val decorationId: DecorationId?,
    val closeButtonType: Int?,
    val closeButtonSize: Int?,
    val closeButtonLineColor: String?,
    val closeButtonBgColor: String?,
    val closeButtonFrameColor: String?
) {
    companion object {
        /**
         * @param spotId 広告枠ID
         * @param form 広告枠作成のForm
         * @return 広告枠バナー表示設定のInsertオブジェクト
         */
        fun of(spotId: SpotId, form: SpotCreateForm): SpotBannerDisplayInsert =
            of(spotId, form.banner!!, form.basic.isDisplayControl)

        /**
         * @param spotId 広告枠ID
         * @param form 広告枠作成のForm
         * @param isDisplayControl 表示制御有無
         * @return 広告枠バナー表示設定のInsertオブジェクト
         */
        fun of(
            spotId: SpotId,
            form: BannerSettingForm,
            isDisplayControl: Boolean
        ): SpotBannerDisplayInsert = SpotBannerDisplayInsert(
            spotId,
            if (isDisplayControl) 0 else null,
            form.isScalable.toString(),
            form.isDisplayScrolling.toString(),
            form.decorationId,
            form.closeButton?.displayPosition,
            form.closeButton?.displaySize,
            form.closeButton?.lineColor?.rgba(),
            form.closeButton?.backgroundColor?.rgba(),
            form.closeButton?.frameColor?.rgba()
        )
    }
}
