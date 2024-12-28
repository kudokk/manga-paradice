package jp.mangaka.ssp.infrastructure.datasource.dao.spotnative

import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm

data class SpotNativeInsert(
    val spotId: SpotId,
    @Deprecated("マルチフォーマット対応で参照されなくなり廃止予定のカラムだが non-null のため暫定的な値で登録を行っている")
    val nativeTemplateId: NativeTemplateId
) {
    val placementCount: Int = 1

    companion object {
        /**
         * @param spotId 広告枠ID
         * @param form ネイティブ設定のForm
         * @return 生成したInsertオブジェクト
         */
        fun of(spotId: SpotId, form: NativeSettingForm): SpotNativeInsert = SpotNativeInsert(
            spotId,
            (form.standard?.nativeTemplateId ?: form.video?.nativeTemplateId)!!
        )
    }
}
