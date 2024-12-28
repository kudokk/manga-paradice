package jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay

import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm.ColorForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeStandardForm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SpotNativeDisplayInsertのテスト")
private class SpotNativeDisplayInsertTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
        val nativeTemplateId1 = NativeTemplateId(1)
        val nativeTemplateId2 = NativeTemplateId(2)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @Test
        @DisplayName("閉じるボタンなし")
        fun isNoCloseButton() {
            assertEquals(
                SpotNativeDisplayInsert.of(spotId1, NativeStandardForm(nativeTemplateId1, null)),
                SpotNativeDisplayInsert(spotId1, nativeTemplateId1, null, null, null, null, null)
            )
        }

        @Test
        @DisplayName("閉じるボタンが空")
        fun isEmptyCloseButton() {
            val emptyCustomButton = CloseButtonForm(null, null, null, null, null)

            assertEquals(
                SpotNativeDisplayInsert.of(spotId2, NativeStandardForm(nativeTemplateId1, emptyCustomButton)),
                SpotNativeDisplayInsert(spotId2, nativeTemplateId1, null, null, null, null, null)
            )
        }

        @Test
        @DisplayName("閉じるボタン全入力")
        fun isFullCloseButton() {
            val fullCustomButton = CloseButtonForm(
                10, 11, ColorForm(255, 0, 0, 1.0), ColorForm(0, 255, 0, 0.5), ColorForm(0, 0, 255, 0.1)
            )

            assertEquals(
                SpotNativeDisplayInsert.of(spotId2, NativeStandardForm(nativeTemplateId2, fullCustomButton)),
                SpotNativeDisplayInsert(
                    spotId2, nativeTemplateId2, 10, 11, "rgba(255,0,0,1.0)", "rgba(0,255,0,0.5)", "rgba(0,0,255,0.1)"
                )
            )
        }
    }
}
