package jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeVideoForm
import jp.mangaka.ssp.util.exception.CompassManagerException

data class SpotNativeVideoDisplayInsert(
    val spotId: SpotId,
    val aspectRatioId: AspectRatioId,
    val nativeTemplateId: NativeTemplateId,
    val playerWidth: Int,
    val playerHeight: Int,
    val width: Int?,
    val height: Int?,
    val positionBottom: Int?,
    val isScalable: String,
    val closeButtonType: Int?,
    val closeButtonSize: Int?,
    val closeButtonLineColor: String?,
    val closeButtonBgColor: String?,
    val closeButtonFrameColor: String?
) {
    companion object {
        /**
         * @param spotId 広告枠ID
         * @param form ネイティブ動画デザインのForm
         * @param isDisplayControl 基本設定の表示制御
         * @return 生成したInsertオブジェクト
         */
        fun of(
            spotId: SpotId,
            form: NativeVideoForm,
            isDisplayControl: Boolean
        ): SpotNativeVideoDisplayInsert {
            // 呼び出し側でチェックされている想定なので強制キャスト
            val videoDisplay = VideoDisplay.valueOf(form.nativeTemplateId!!)

            return SpotNativeVideoDisplayInsert(
                spotId,
                videoDisplay.aspectRatioId,
                videoDisplay.nativeTemplateId,
                videoDisplay.playerWidth,
                videoDisplay.playerHeight,
                videoDisplay.width,
                videoDisplay.height,
                if (isDisplayControl) 0 else null,
                form.isScalable.toString(),
                form.closeButton?.displayPosition,
                form.closeButton?.displaySize,
                form.closeButton?.lineColor?.rgba(),
                form.closeButton?.backgroundColor?.rgba(),
                form.closeButton?.frameColor?.rgba()
            )
        }

        private enum class VideoDisplay(
            val nativeTemplateId: NativeTemplateId,
            val aspectRatioId: AspectRatioId,
            val playerWidth: Int,
            val playerHeight: Int,
            val width: Int,
            val height: Int,
        ) {
            TEMPLATE_51(NativeTemplateId(51), AspectRatioId(1), 88, 50, 320, 50),
            TEMPLATE_52(NativeTemplateId(52), AspectRatioId(1), 88, 50, 320, 50),
            TEMPLATE_53(NativeTemplateId(53), AspectRatioId(1), 178, 100, 320, 100),
            TEMPLATE_54(NativeTemplateId(54), AspectRatioId(1), 178, 100, 320, 100),
            TEMPLATE_55(NativeTemplateId(55), AspectRatioId(1), 300, 169, 300, 250),
            TEMPLATE_56(NativeTemplateId(56), AspectRatioId(1), 300, 169, 320, 250);

            companion object {
                /**
                 * @param nativeTemplateId ネイティブテンプレートID
                 * @return ネイティブテンプレートIDに合致する VideoDisplay
                 */
                fun valueOf(nativeTemplateId: NativeTemplateId): VideoDisplay = entries
                    .firstOrNull { it.nativeTemplateId == nativeTemplateId }
                    ?: throw CompassManagerException("利用できないネイティブテンプレートID:${nativeTemplateId}です。")
            }
        }
    }
}
