package jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay

import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeStandardForm

data class SpotNativeDisplayInsert(
    val spotId: SpotId,
    val nativeTemplateId: NativeTemplateId,
    val closeButtonType: Int?,
    val closeButtonSize: Int?,
    val closeButtonLineColor: String?,
    val closeButtonBgColor: String?,
    val closeButtonFrameColor: String?
) {
    companion object {
        /**
         * @param spotId 広告枠ID
         * @param form ネイティブデザインのForm
         * @return 生成したInsertオブジェクト
         */
        fun of(spotId: SpotId, form: NativeStandardForm): SpotNativeDisplayInsert = SpotNativeDisplayInsert(
            spotId,
            // 呼び出し側でチェックされている想定なので強制キャスト
            form.nativeTemplateId!!,
            form.closeButton?.displayPosition,
            form.closeButton?.displaySize,
            form.closeButton?.lineColor?.rgba(),
            form.closeButton?.backgroundColor?.rgba(),
            form.closeButton?.frameColor?.rgba()
        )
    }
}
