package jp.mangaka.ssp.presentation.controller.spot.form

data class SpotCreateForm(
    val basic: BasicSettingCreateForm,
    val dsps: List<DspForm>,
    val banner: BannerSettingForm?,
    val native: NativeSettingForm?,
    val video: VideoSettingForm?
)
