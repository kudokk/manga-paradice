package jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm.ColorForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.BannerSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingCreateForm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@DisplayName("SpotBannerDisplayInsertのテスト")
private class SpotBannerDisplayInsertTest {
    companion object {
        val spotId = SpotId(1)
        val decorationId = DecorationId(1)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("ファクトリ関数のテスト")
    inner class OfTest {
        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("正常")
        fun isCorrect(form: SpotCreateForm, expected: SpotBannerDisplayInsert) {
            assertEquals(expected, SpotBannerDisplayInsert.of(spotId, form))
        }

        private fun correctParams() = listOf(
            Arguments.of(
                form(true, false, true, null, null),
                SpotBannerDisplayInsert(
                    spotId, 0, false.toString(), true.toString(), null, null, null, null, null, null
                )
            ),
            Arguments.of(
                form(false, true, false, decorationId, CloseButtonForm(1, null, null, null, null)),
                SpotBannerDisplayInsert(
                    spotId, null, true.toString(), false.toString(), decorationId, 1, null, null, null, null
                )
            ),
            Arguments.of(
                form(
                    true, true, false, decorationId,
                    CloseButtonForm(
                        1, 100, ColorForm(0, 0, 0, 0.0), ColorForm(10, 20, 30, 0.5), ColorForm(255, 255, 255, 1.0)
                    )
                ),
                SpotBannerDisplayInsert(
                    spotId, 0, true.toString(), false.toString(), decorationId, 1, 100,
                    "rgba(0,0,0,0.0)", "rgba(10,20,30,0.5)", "rgba(255,255,255,1.0)"
                )
            )
        )

        private fun form(
            isDisplayControl: Boolean,
            isScalable: Boolean,
            isDisplayScrolling: Boolean,
            decorationId: DecorationId?,
            closeButton: CloseButtonForm?
        ): SpotCreateForm {
            val basic: BasicSettingCreateForm = mock {
                on { this.isDisplayControl } doReturn isDisplayControl
            }
            val banner: BannerSettingForm = mock {
                on { this.isScalable } doReturn isScalable
                on { this.isDisplayScrolling } doReturn isDisplayScrolling
                on { this.decorationId } doReturn decorationId
                on { this.closeButton } doReturn closeButton
            }

            return mock {
                on { this.basic } doReturn basic
                on { this.banner } doReturn banner
            }
        }
    }
}
