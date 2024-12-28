package jp.mangaka.ssp.presentation.controller.spot.view.detail

import com.fasterxml.jackson.annotation.JsonFormat
import jp.mangaka.ssp.presentation.controller.common.view.StructSelectElementView
import java.time.LocalDateTime

data class SpotDetailView(
    val basic: BasicSettingView,
    val dsps: List<DspSettingListItemView>,
    val banner: BannerSettingView?,
    val native: NativeSettingView?,
    val video: VideoSettingView?,
    val structs: List<StructSelectElementView>,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val updateTime: LocalDateTime
)
