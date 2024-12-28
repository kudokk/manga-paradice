package jp.mangaka.ssp.presentation.controller.spot.view.detail

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideo
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpm
import jp.mangaka.ssp.presentation.controller.spot.view.AspectRatioView
import jp.mangaka.ssp.presentation.controller.spot.view.AspectRatioView.VideoType
import jp.mangaka.ssp.presentation.controller.spot.view.detail.CloseButtonView.ColorView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.VideoSettingView.VideoDetailView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.VideoSettingView.VideoDetailView.DisplayPositionView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.VideoSettingView.VideoDetailView.DisplayPositionView.VideoDisplayPositionElementView
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

@DisplayName("VideoSettingViewのテスト")
private class VideoSettingViewTest {
    companion object {
        val aspectRatioId1 = AspectRatioId(1)
        val aspectRatioId2 = AspectRatioId(2)
        val aspectRatioId3 = AspectRatioId(3)
        val aspectRatioId4 = AspectRatioId(4)
        val today = LocalDate.of(2024, 1, 15)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val spot: Spot = mock {
            on { this.rotationMax } doReturn 30
        }
        val spotVideoDisplays1 = listOf(
            SpotVideoDisplay(
                mock(), aspectRatioId1, 100, 10, null, 20, null, true, false, true, 1, 200, "rgba(1,23,456,0.1)",
                "rgba(23,456,1,1.0)", "rgba(456,1,23,0.7)", 5
            ),
            SpotVideoDisplay(
                mock(), aspectRatioId2, 300, null, 11, null, 21, false, true, false, 2, 400, "rgba(987,65,4,1.0)",
                "rgba(65,4,987,0.5)", "rgba(4,987,65,0.1)", 5
            )
        )
        val spotVideoDisplays2 = listOf(
            SpotVideoDisplay(
                mock(), aspectRatioId3, 400, null, null, null, null, false, false, true, null, null, null, null,
                null, null
            ),
            SpotVideoDisplay(
                mock(), aspectRatioId4, 500, null, null, null, null, true, true, false, null, null, null, null,
                null, null
            )
        )
        val spotVideoFloorCpms1 = listOf(
            // aspectRatioId1
            SpotVideoFloorCpm(mock(), aspectRatioId1, today.plusDays(-10), today.plusDays(-9), BigDecimal("123.456")),
            SpotVideoFloorCpm(mock(), aspectRatioId1, today.plusDays(-2), null, BigDecimal("234.567")),
            // 表示対象
            SpotVideoFloorCpm(mock(), aspectRatioId1, today.plusDays(-1), null, BigDecimal("345.678")),
            SpotVideoFloorCpm(mock(), aspectRatioId1, today.plusDays(1), null, BigDecimal("456.789")),
            // aspectRatioId2
            SpotVideoFloorCpm(mock(), aspectRatioId2, today.plusDays(-3), today.plusDays(-2), BigDecimal("567.890")),
            SpotVideoFloorCpm(mock(), aspectRatioId2, today.plusDays(-1), null, BigDecimal("678.901")),
            // 表示対象
            SpotVideoFloorCpm(mock(), aspectRatioId2, today, null, BigDecimal("789.012")),
            SpotVideoFloorCpm(mock(), aspectRatioId2, today.plusDays(1), null, BigDecimal("890.123"))
        )
        val spotVideoFloorCpms2 = listOf(
            // すべて過去、aspectRatioId4はレコードなし
            SpotVideoFloorCpm(mock(), aspectRatioId3, today.plusDays(-4), today.plusDays(-3), BigDecimal("123.456")),
            SpotVideoFloorCpm(mock(), aspectRatioId3, today.plusDays(-3), today.plusDays(-2), BigDecimal("123.456"))
        )
        val aspectRatios = listOf(
            AspectRatio(aspectRatioId1, 16, 9, mock()),
            AspectRatio(aspectRatioId2, 16, 5, mock()),
            AspectRatio(aspectRatioId3, 32, 5, mock()),
            AspectRatio(aspectRatioId4, 1, 2, mock()),
        )

        @BeforeEach
        fun beforeEach() {
            mockkStatic(LocalDate::class)
            every { LocalDate.now() } returns today
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("全項目入力あり")
        fun isFull() {
            val actual = VideoSettingView.of(
                spot,
                SpotVideo(mock(), null, null, true),
                spotVideoDisplays1,
                spotVideoFloorCpms1,
                aspectRatios
            )

            assertEquals(
                VideoSettingView(
                    30,
                    true,
                    5,
                    listOf(
                        VideoDetailView(
                            AspectRatioView(aspectRatioId1, 16, 9, VideoType.Wipe),
                            true,
                            100,
                            CloseButtonView(
                                1, 200, ColorView(1, 23, 456, 0.1), ColorView(23, 456, 1, 1.0),
                                ColorView(456, 1, 23, 0.7)
                            ),
                            DisplayPositionView(
                                VideoDisplayPositionElementView("top", 10),
                                VideoDisplayPositionElementView("left", 20)
                            ),
                            false,
                            true,
                            "345.678",
                            today.plusDays(-1)
                        ),
                        VideoDetailView(
                            AspectRatioView(aspectRatioId2, 16, 5, VideoType.FullWide),
                            false,
                            300,
                            CloseButtonView(
                                2, 400, ColorView(987, 65, 4, 1.0), ColorView(65, 4, 987, 0.5),
                                ColorView(4, 987, 65, 0.1)
                            ),
                            DisplayPositionView(
                                VideoDisplayPositionElementView("bottom", 11),
                                VideoDisplayPositionElementView("right", 21)
                            ),
                            true,
                            false,
                            "789.012",
                            today
                        )
                    )
                ),
                actual
            )
        }

        @Test
        @DisplayName("必須項目のみ")
        fun isNotFull() {
            val actual = VideoSettingView.of(
                spot,
                SpotVideo(mock(), null, null, false),
                spotVideoDisplays2,
                spotVideoFloorCpms2,
                aspectRatios
            )

            assertEquals(
                VideoSettingView(
                    30,
                    false,
                    null,
                    listOf(
                        VideoDetailView(
                            AspectRatioView(aspectRatioId3, 32, 5, VideoType.FullWide),
                            false,
                            400,
                            null,
                            null,
                            false,
                            true,
                            null,
                            null
                        ),
                        VideoDetailView(
                            AspectRatioView(aspectRatioId4, 1, 2, VideoType.Inline),
                            true,
                            500,
                            null,
                            null,
                            true,
                            false,
                            null,
                            null
                        )
                    )
                ),
                actual
            )
        }
    }
}
