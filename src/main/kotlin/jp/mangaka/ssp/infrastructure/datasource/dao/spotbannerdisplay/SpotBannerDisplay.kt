package jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay

import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import org.apache.commons.lang3.ObjectUtils

data class SpotBannerDisplay(
    val spotId: SpotId,
    val positionTop: Int?,
    val positionBottom: Int?,
    val positionLeft: Int?,
    val positionRight: Int?,
    val isScalable: Boolean,
    val isDisplayScrolling: Boolean,
    val decorationId: DecorationId?,
    val closeButtonType: Int?,
    val closeButtonSize: Int?,
    val closeButtonLineColor: String?,
    val closeButtonBgColor: String?,
    val closeButtonFrameColor: String?
) {
    /**
     * @return 表示制御がオンの場合は true
     */
    fun isDisplayControl(): Boolean = ObjectUtils.anyNotNull(positionTop, positionBottom, positionLeft, positionRight)
}
