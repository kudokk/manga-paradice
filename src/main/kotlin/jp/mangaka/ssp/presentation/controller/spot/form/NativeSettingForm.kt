package jp.mangaka.ssp.presentation.controller.spot.form

import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId

data class NativeSettingForm(
    val standard: NativeStandardForm?,
    val video: NativeVideoForm?
) {
    data class NativeStandardForm(
        val nativeTemplateId: NativeTemplateId?,
        val closeButton: CloseButtonForm?
    )

    data class NativeVideoForm(
        val nativeTemplateId: NativeTemplateId?,
        val closeButton: CloseButtonForm?,
        val isScalable: Boolean
    )
}
