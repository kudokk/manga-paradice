package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo

import java.time.LocalTime
import jp.mangaka.ssp.application.valueobject.spot.SpotId

data class SpotVideo(
    val spotId: SpotId,
    val durationMin: LocalTime?,
    val durationMax: LocalTime?,
    val isFixedRotationAspectRatio: Boolean,
)
