package jp.mangaka.ssp.presentation.controller.spot.form

data class BasicSettingEditForm(
    val spotName: String?,
    val spotMaxSize: SpotMaxSizeForm?,
    val description: String?,
    val pageUrl: String?
)
