package jp.mangaka.ssp.presentation.controller.spot.form

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class SpotDspEditForm(
    val dsps: List<DspForm>,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val updateTime: LocalDateTime
)
