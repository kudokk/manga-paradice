package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import org.apache.commons.lang3.ObjectUtils

data class SpotVideoDisplay(
    val spotId: SpotId,
    val aspectRatioId: AspectRatioId,
    val width: Int,
    val positionTop: Int?,
    val positionBottom: Int?,
    val positionLeft: Int?,
    val positionRight: Int?,
    val isScalable: Boolean,
    val isAllowedDrag: Boolean,
    val isRoundedRectangle: Boolean,
    val closeButtonType: Int?,
    val closeButtonSize: Int?,
    val closeButtonLineColor: String?,
    val closeButtonBgColor: String?,
    val closeButtonFrameColor: String?,
    val prLabelType: Int?
) {
    /**
     * @return 表示制御がオンの場合は true
     */
    fun isDisplayControl(): Boolean = ObjectUtils.anyNotNull(positionTop, positionBottom, positionLeft, positionRight)
}
