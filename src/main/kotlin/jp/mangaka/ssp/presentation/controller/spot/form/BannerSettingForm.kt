package jp.mangaka.ssp.presentation.controller.spot.form

import jp.mangaka.ssp.application.valueobject.decoration.DecorationId

data class BannerSettingForm(
    val sizeTypes: List<SizeTypeForm>,
    val isScalable: Boolean,
    val isDisplayScrolling: Boolean,
    val closeButton: CloseButtonForm?,
    val decorationId: DecorationId?
)
