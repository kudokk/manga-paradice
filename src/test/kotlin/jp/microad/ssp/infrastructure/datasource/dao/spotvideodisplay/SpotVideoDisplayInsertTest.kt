package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm.ColorForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm.VideoDisplayPositionElementForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm.VideoDisplayPositionElementForm.DirectionType.Horizontal
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm.VideoDisplayPositionElementForm.DirectionType.Vertical
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SpotVideoDisplayInsertのテスト")
private class SpotVideoDisplayInsertTest {
    companion object {
        val spotId = SpotId(1)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val aspectRatios = listOf(
            mockAspectRatio(1, 16, 9),
            mockAspectRatio(2, 32, 5)
        )

        @Test
        @DisplayName("正常 - 全項目入力あり")
        fun isCorrectAndInputAll() {
            val details = listOf(
                VideoDetailForm(
                    aspectRatios[0].aspectRatioId,
                    true,
                    1920,
                    CloseButtonForm(
                        1,
                        200,
                        ColorForm(10, 11, 12, 0.1),
                        ColorForm(20, 21, 22, 0.2),
                        ColorForm(30, 31, 32, 0.3)
                    ),
                    VideoDisplayPositionForm(
                        VideoDisplayPositionElementForm(Vertical.top, 100),
                        VideoDisplayPositionElementForm(Horizontal.left, 200),
                    ),
                    true,
                    false,
                    mock(),
                    mock()
                ),
                VideoDetailForm(
                    aspectRatios[0].aspectRatioId,
                    false,
                    // heightの切り上げ確認
                    710,
                    CloseButtonForm(
                        2,
                        300,
                        ColorForm(40, 41, 42, 0.4),
                        ColorForm(50, 51, 52, 0.5),
                        ColorForm(60, 61, 62, 0.6)
                    ),
                    VideoDisplayPositionForm(
                        VideoDisplayPositionElementForm(Vertical.bottom, 300),
                        VideoDisplayPositionElementForm(Horizontal.right, 400),
                    ),
                    false,
                    true,
                    mock(),
                    mock()
                )
            )
            val form = VideoSettingForm(null, true, 4, details)

            val actual = SpotVideoDisplayInsert.of(spotId, form, aspectRatios)

            assertEquals(
                listOf(
                    SpotVideoDisplayInsert(
                        spotId, aspectRatios[0].aspectRatioId, 1920, 1080, 100, null, 200, null, true.toString(),
                        true.toString(), false.toString(), 1, 200, "rgba(10,11,12,0.1)", "rgba(20,21,22,0.2)",
                        "rgba(30,31,32,0.3)", 4
                    ),
                    SpotVideoDisplayInsert(
                        spotId, aspectRatios[0].aspectRatioId, 710, 400, null, 300, null, 400, false.toString(),
                        false.toString(), true.toString(), 2, 300, "rgba(40,41,42,0.4)", "rgba(50,51,52,0.5)",
                        "rgba(60,61,62,0.6)", 4
                    )
                ),
                actual
            )
        }

        @Test
        @DisplayName("正常 - 任意項目のみ")
        fun isCorrectAndNotInputAll() {
            val details = listOf(
                VideoDetailForm(
                    aspectRatios[1].aspectRatioId,
                    true,
                    640,
                    null,
                    // 実際にはあり得ないが確認のために両方 NULL
                    VideoDisplayPositionForm(null, null),
                    false,
                    false,
                    mock(),
                    mock()
                ),
                VideoDetailForm(
                    aspectRatios[1].aspectRatioId,
                    false,
                    // heightの切り上げ確認
                    200,
                    null,
                    null,
                    true,
                    true,
                    mock(),
                    mock()
                )
            )
            val form = VideoSettingForm(null, false, null, details)

            val actual = SpotVideoDisplayInsert.of(spotId, form, aspectRatios)

            assertEquals(
                listOf(
                    SpotVideoDisplayInsert(
                        spotId, aspectRatios[1].aspectRatioId, 640, 100, null, null, null, null, true.toString(),
                        false.toString(), false.toString(), null, null, null, null, null, null
                    ),
                    SpotVideoDisplayInsert(
                        spotId, aspectRatios[1].aspectRatioId, 200, 32, null, null, null, null, false.toString(),
                        true.toString(), true.toString(), null, null, null, null, null, null
                    )
                ),
                actual
            )
        }

        private fun mockAspectRatio(aspectRatioId: Int, width: Int, height: Int): AspectRatio = mock {
            on { this.aspectRatioId } doReturn AspectRatioId(aspectRatioId)
            on { this.width } doReturn width
            on { this.height } doReturn height
        }
    }
}
