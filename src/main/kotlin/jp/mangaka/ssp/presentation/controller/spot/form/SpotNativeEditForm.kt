package jp.mangaka.ssp.presentation.controller.spot.form

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class SpotNativeEditForm(
    val native: NativeSettingForm?,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val updateTime: LocalDateTime
)
