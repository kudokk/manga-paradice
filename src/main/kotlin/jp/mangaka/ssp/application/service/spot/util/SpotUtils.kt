package jp.mangaka.ssp.application.service.spot.util

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBanner
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNative
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideo
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplay
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.apache.commons.lang3.ObjectUtils

object SpotUtils {
    /**
     * @param spotBannerDisplay バナー設定
     * @param spotNativeDisplay ネイティブ設定
     * @param spotNativeVideoDisplay ネイティブビデオ設定
     * @param spotVideoDisplays ビデオ設定
     * @return 表示制御ありの場合は true
     */
    fun isDisplayControl(
        spotBannerDisplay: SpotBannerDisplay?,
        spotNativeDisplay: SpotNativeDisplay?,
        spotNativeVideoDisplay: SpotNativeVideoDisplay?,
        spotVideoDisplays: Collection<SpotVideoDisplay>,
    ): Boolean = spotBannerDisplay?.isDisplayControl() == true ||
        spotNativeDisplay?.isDisplayControl() == true ||
        spotNativeVideoDisplay?.isDisplayControl() == true ||
        spotVideoDisplays.any { it.isDisplayControl() }

    /**
     * 広告枠に紐づくバナーの整合性を確認する.
     *
     * @param spotId 広告枠ID
     * @param spotBanner 広告枠バナー設定
     * @param spotBannerDisplay 広告枠バナー詳細設定
     * @throws CompassManagerException 広告枠に紐づくバナーの状態が不正な場合に投げられる
     */
    fun checkSpotBannerConsistency(
        spotId: SpotId,
        spotBanner: SpotBanner?,
        spotBannerDisplay: SpotBannerDisplay?
    ) {
        if (spotBanner != null && spotBannerDisplay == null) {
            throw CompassManagerException("広告枠：${spotId}のバナー設定の詳細設定が存在しません。")
        }
    }

    /**
     * 広告枠に紐づくネイティブの整合性を確認する.
     *
     * @param spotId 広告枠ID
     * @param spotNative 広告枠ネイティブ設定
     * @param spotNativeDisplay 広告枠ネイティブ詳細設定
     * @param spotNativeVideoDisplay 広告枠ネイティブビデオ詳細設定
     * @throws CompassManagerException 広告枠に紐づくネイティブの状態が不正な場合に投げられる
     */
    fun checkSpotNativeConsistency(
        spotId: SpotId,
        spotNative: SpotNative?,
        spotNativeDisplay: SpotNativeDisplay?,
        spotNativeVideoDisplay: SpotNativeVideoDisplay?
    ) {
        if (spotNative != null && ObjectUtils.allNull(spotNativeDisplay, spotNativeVideoDisplay)) {
            throw CompassManagerException("広告枠：${spotId}のネイティブ設定の詳細設定が存在しません。")
        }
    }

    /**
     * 広告枠に紐づくビデオの整合性を確認する.
     *
     * @param spotId 広告枠ID
     * @param spotVideo 広告枠ビデオ設定
     * @param spotVideoDisplays 広告枠ビデオ詳細設定
     * @throws CompassManagerException 広告枠に紐づくビデオの状態が不正な場合に投げられる
     */
    fun checkSpotVideoConsistency(
        spotId: SpotId,
        spotVideo: SpotVideo?,
        spotVideoDisplays: Collection<SpotVideoDisplay>
    ) {
        if (spotVideo != null && spotVideoDisplays.isEmpty()) {
            throw CompassManagerException("広告枠：${spotId}のビデオ設定の詳細設定が存在しません。")
        }
    }
}
