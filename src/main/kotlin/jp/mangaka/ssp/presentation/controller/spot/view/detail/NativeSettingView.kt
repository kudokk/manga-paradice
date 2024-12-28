package jp.mangaka.ssp.presentation.controller.spot.view.detail

import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplay
import jp.mangaka.ssp.presentation.controller.spot.view.nativedesign.NativeDesignsView.NativeDesignView

data class NativeSettingView(val standard: NativeStandardView?, val video: NativeVideoView?) {
    data class NativeStandardView(
        val nativeTemplate: NativeDesignView,
        val closeButton: CloseButtonView?
    )

    data class NativeVideoView(
        val nativeTemplate: NativeDesignView,
        val closeButton: CloseButtonView?,
        val isScalable: Boolean
    )

    companion object {
        /**
         * @param spotNativeDisplay 広告枠ネイティブ通常デザイン設定表示
         * @param nativeStandardTemplate ネイティブ通常デザイン
         * @param spotNativeVideoDisplay 広告枠ネイティブビデオデザイン設定表示
         * @param nativeVideoTemplate ネイティブビデオデザイン
         * @return ネイティブ設定のView
         */
        fun of(
            spotNativeDisplay: SpotNativeDisplay?,
            nativeStandardTemplate: NativeTemplate?,
            spotNativeVideoDisplay: SpotNativeVideoDisplay?,
            nativeVideoTemplate: NativeTemplate?,
        ): NativeSettingView = NativeSettingView(
            spotNativeDisplay?.let {
                NativeStandardView(
                    NativeDesignView.of(nativeStandardTemplate!!),
                    if (it.closeButtonType != null) {
                        CloseButtonView.of(
                            it.closeButtonType,
                            it.closeButtonSize,
                            it.closeButtonLineColor,
                            it.closeButtonBgColor,
                            it.closeButtonFrameColor
                        )
                    } else {
                        null
                    }
                )
            },
            spotNativeVideoDisplay?.let {
                NativeVideoView(
                    NativeDesignView.of(nativeVideoTemplate!!),
                    if (it.closeButtonType != null) {
                        CloseButtonView.of(
                            it.closeButtonType,
                            it.closeButtonSize,
                            it.closeButtonLineColor,
                            it.closeButtonBgColor,
                            it.closeButtonFrameColor
                        )
                    } else {
                        null
                    },
                    it.isScalable
                )
            }
        )
    }
}
