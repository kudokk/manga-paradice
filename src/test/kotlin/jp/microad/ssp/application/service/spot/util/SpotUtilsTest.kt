package jp.mangaka.ssp.application.service.spot.util

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBanner
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNative
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideo
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplay
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@DisplayName("SpotUtilsのテスト")
private class SpotUtilsTest {
    companion object {
        val spotId = SpotId(1)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("isDisplayControlのテスト")
    inner class IsDisplayControlTest {
        @ParameterizedTest
        @MethodSource("displayControlledParams")
        @DisplayName("表示制御オン")
        fun isDisplayControlled(
            spotBannerDisplay: SpotBannerDisplay?,
            spotNativeDisplay: SpotNativeDisplay?,
            spotNativeVideoDisplay: SpotNativeVideoDisplay?,
            spotVideoDisplays: Collection<SpotVideoDisplay>
        ) {
            assertTrue(
                SpotUtils.isDisplayControl(
                    spotBannerDisplay,
                    spotNativeDisplay,
                    spotNativeVideoDisplay,
                    spotVideoDisplays
                )
            )
        }

        private fun displayControlledParams() = listOf(
            Arguments.of(mockBanner(true), mockNative(true), mockNativeVideo(true), mockVideos(listOf(true, true))),
            Arguments.of(mockBanner(true), mockNative(false), mockNativeVideo(false), mockVideos(listOf(false, false))),
            Arguments.of(mockBanner(false), mockNative(true), mockNativeVideo(false), mockVideos(listOf(false, false))),
            Arguments.of(mockBanner(false), mockNative(false), mockNativeVideo(true), mockVideos(listOf(false, false))),
            Arguments.of(mockBanner(false), mockNative(false), mockNativeVideo(false), mockVideos(listOf(false, true))),
            Arguments.of(mockBanner(true), null, null, emptyList<Boolean>()),
            Arguments.of(null, mockNative(true), null, emptyList<Boolean>()),
            Arguments.of(null, null, mockNativeVideo(true), emptyList<Boolean>()),
            Arguments.of(null, null, null, mockVideos(listOf(false, true)))
        )

        @ParameterizedTest
        @MethodSource("notDisplayControlledParams")
        @DisplayName("表示制御オフ")
        fun isNotDisplayControlled(
            spotBannerDisplay: SpotBannerDisplay?,
            spotNativeDisplay: SpotNativeDisplay?,
            spotNativeVideoDisplay: SpotNativeVideoDisplay?,
            spotVideoDisplays: Collection<SpotVideoDisplay>
        ) {
            assertFalse(
                SpotUtils.isDisplayControl(
                    spotBannerDisplay,
                    spotNativeDisplay,
                    spotNativeVideoDisplay,
                    spotVideoDisplays
                )
            )
        }

        private fun notDisplayControlledParams() = listOf(
            Arguments.of(
                mockBanner(false),
                mockNative(false),
                mockNativeVideo(false),
                mockVideos(listOf(false, false))
            ),
            Arguments.of(null, null, null, emptyList<Boolean>())
        )
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkSpotBannerConsistencyのテスト")
    inner class CheckSpotBannerConsistencyTest {
        val spotBanner: SpotBanner = mock()
        val spotBannerDisplay: SpotBannerDisplay = mock()

        @Test
        @DisplayName("不正")
        fun isInvalid() {
            assertThrows<CompassManagerException> { SpotUtils.checkSpotBannerConsistency(spotId, spotBanner, null) }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(spotBanner: SpotBanner?, spotBannerDisplay: SpotBannerDisplay?) {
            assertDoesNotThrow { SpotUtils.checkSpotBannerConsistency(spotId, spotBanner, spotBannerDisplay) }
        }

        private fun validParams() = listOf(
            Arguments.of(null, null),
            Arguments.of(spotBanner, spotBannerDisplay)
        )
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkSpotNativeConsistencyのテスト")
    inner class CheckSpotNativeConsistencyTest {
        val spotNative: SpotNative = mock()
        val spotNativeDisplay: SpotNativeDisplay = mock()
        val spotNativeVideoDisplay: SpotNativeVideoDisplay = mock()

        @Test
        @DisplayName("不正")
        fun isInvalid() {
            assertThrows<CompassManagerException> {
                SpotUtils.checkSpotNativeConsistency(spotId, spotNative, null, null)
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(
            spotNative: SpotNative?,
            spotNativeDisplay: SpotNativeDisplay?,
            spotNativeVideoDisplay: SpotNativeVideoDisplay?
        ) {
            assertDoesNotThrow {
                SpotUtils.checkSpotNativeConsistency(spotId, spotNative, spotNativeDisplay, spotNativeVideoDisplay)
            }
        }

        private fun validParams() = listOf(
            Arguments.of(null, null, null),
            Arguments.of(spotNative, spotNativeDisplay, spotNativeVideoDisplay),
            Arguments.of(spotNative, null, spotNativeVideoDisplay),
            Arguments.of(spotNative, spotNativeDisplay, null)
        )
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkSpotVideoConsistencyのテスト")
    inner class CheckSpotVideoConsistencyTest {
        val spotVideo: SpotVideo = mock()
        val notEmptySpotVideoDisplays: Collection<SpotVideoDisplay> = listOf(mock())
        val emptySpotVideoDisplays: Collection<SpotVideoDisplay> = emptyList()

        @Test
        @DisplayName("不正")
        fun isInvalid() {
            assertThrows<CompassManagerException> {
                SpotUtils.checkSpotVideoConsistency(spotId, spotVideo, emptySpotVideoDisplays)
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(spotVideo: SpotVideo?, spotVideoDisplays: Collection<SpotVideoDisplay>) {
            assertDoesNotThrow {
                SpotUtils.checkSpotVideoConsistency(spotId, spotVideo, spotVideoDisplays)
            }
        }

        private fun validParams() = listOf(
            Arguments.of(null, emptySpotVideoDisplays),
            Arguments.of(spotVideo, notEmptySpotVideoDisplays)
        )
    }

    fun mockBanner(isDisplayControl: Boolean): SpotBannerDisplay = mock {
        on { this.isDisplayControl() } doReturn isDisplayControl
    }

    fun mockNative(isDisplayControl: Boolean): SpotNativeDisplay = mock {
        on { this.isDisplayControl() } doReturn isDisplayControl
    }

    fun mockNativeVideo(isDisplayControl: Boolean): SpotNativeVideoDisplay = mock {
        on { this.isDisplayControl() } doReturn isDisplayControl
    }

    fun mockVideos(isDisplayControls: List<Boolean>): List<SpotVideoDisplay> = isDisplayControls
        .map { isDisplayControl ->
            mock {
                on { this.isDisplayControl() } doReturn isDisplayControl
            }
        }
}
