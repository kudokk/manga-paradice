package jp.mangaka.ssp.presentation.controller.spot.view

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType

data class SpotTagView(
    val spotId: SpotId,
    val upstreamType: UpstreamType,
    val isAmp: Boolean,
    val headerTag: String,
    val spotTag: String,
    val ampTag: String,
    val spotIdTag: String,
    val gamTag: String,
    val reactComponentCodeWithHeader: String,
    val vueComponentCodeWithHeader: String,
    val vueComponentCodeWithoutHeader: String
)
