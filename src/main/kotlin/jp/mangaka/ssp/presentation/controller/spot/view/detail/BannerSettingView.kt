package jp.mangaka.ssp.presentation.controller.spot.view.detail

import jp.mangaka.ssp.infrastructure.datasource.dao.decoration.Decoration
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplay
import jp.mangaka.ssp.presentation.controller.spot.view.DecorationView
import jp.mangaka.ssp.presentation.controller.spot.view.SizeTypeInfoView

data class BannerSettingView(
    val sizeTypes: List<SizeTypeInfoView>,
    val isScalable: Boolean,
    val isDisplayScrolling: Boolean,
    val closeButton: CloseButtonView?,
    val decoration: DecorationView?
) {
    companion object {
        /**
         * @param spotBannerDisplay 広告枠バナー表示
         * @param sizeTypeInfos サイズ種別のリスト
         * @param decoration 帯情報
         * @return バナー設定のView
         */
        fun of(
            spotBannerDisplay: SpotBannerDisplay,
            sizeTypeInfos: Collection<SizeTypeInfo>,
            decoration: Decoration?
        ): BannerSettingView = BannerSettingView(
            SizeTypeInfoView.of(sizeTypeInfos),
            spotBannerDisplay.isScalable,
            spotBannerDisplay.isDisplayScrolling,
            if (spotBannerDisplay.closeButtonType != null) {
                CloseButtonView.of(
                    spotBannerDisplay.closeButtonType,
                    spotBannerDisplay.closeButtonSize,
                    spotBannerDisplay.closeButtonLineColor,
                    spotBannerDisplay.closeButtonBgColor,
                    spotBannerDisplay.closeButtonFrameColor
                )
            } else {
                null
            },
            decoration?.let { DecorationView.of(it) }
        )
    }
}
