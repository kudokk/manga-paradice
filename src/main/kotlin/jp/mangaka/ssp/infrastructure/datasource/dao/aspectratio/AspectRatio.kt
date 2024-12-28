package jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId

data class AspectRatio(
    val aspectRatioId: AspectRatioId,
    val width: Int,
    val height: Int,
    val aspectRatioStatus: AspectRatioStatus
) {
    enum class AspectRatioStatus {
        active, stop
    }
}
