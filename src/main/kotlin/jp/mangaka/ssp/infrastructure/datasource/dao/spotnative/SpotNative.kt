package jp.mangaka.ssp.infrastructure.datasource.dao.spotnative

import java.time.LocalTime
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.ContextSubTypeId
import jp.mangaka.ssp.application.valueobject.spot.ContextTypeId
import jp.mangaka.ssp.application.valueobject.spot.PlacementTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId

data class SpotNative(
    val spotId: SpotId,
    val nativeTemplateId: NativeTemplateId,
    val contextTypeId: ContextTypeId?,
    val contextSubTypeId: ContextSubTypeId?,
    val placementTypeId: PlacementTypeId?,
    val placementCount: Int,
    val rotationInterval: Int?,
    val durationMin: LocalTime?,
    val durationMax: LocalTime?,
)
