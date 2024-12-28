package jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm.ColorForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeVideoForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("SpotNativeVideoDisplayInsertのテスト")
private class SpotNativeVideoDisplayInsertTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
        val aspectRatioId1 = AspectRatioId(1)
        val nativeTemplateId51 = NativeTemplateId(51)
        val nativeTemplateId52 = NativeTemplateId(52)
        val nativeTemplateId53 = NativeTemplateId(53)
        val nativeTemplateId54 = NativeTemplateId(54)
        val nativeTemplateId55 = NativeTemplateId(55)
        val nativeTemplateId56 = NativeTemplateId(56)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val emptyCustomButton = CloseButtonForm(null, null, null, null, null)
        val fullCustomButton = CloseButtonForm(
            10, 11, ColorForm(255, 0, 0, 1.0), ColorForm(0, 255, 0, 0.5), ColorForm(0, 0, 255, 0.1)
        )

        @Test
        @DisplayName("ネイティブテンプレートID:51")
        fun isNativeTemplate51() {
            assertEquals(
                // 閉じるボタンなし、広告拡大オフ、表示制御オフ
                SpotNativeVideoDisplayInsert.of(spotId1, NativeVideoForm(nativeTemplateId51, null, false), false),
                SpotNativeVideoDisplayInsert(
                    spotId1, aspectRatioId1, nativeTemplateId51, 88, 50, 320, 50,
                    null, false.toString(), null, null, null, null, null
                )
            )
        }

        @Test
        @DisplayName("ネイティブテンプレートID:52")
        fun isNativeTemplate52() {
            assertEquals(
                // 閉じるボタンが空、広告拡大オン、表示制御オン
                SpotNativeVideoDisplayInsert.of(
                    spotId1, NativeVideoForm(nativeTemplateId52, emptyCustomButton, true), true
                ),
                SpotNativeVideoDisplayInsert(
                    spotId1, aspectRatioId1, nativeTemplateId52, 88, 50, 320, 50,
                    0, true.toString(), null, null, null, null, null
                )
            )
        }

        @Test
        @DisplayName("ネイティブテンプレートID:53")
        fun isNativeTemplate53() {
            assertEquals(
                // 閉じるボタン全入力、広告拡大オン、表示制御オフ
                SpotNativeVideoDisplayInsert.of(
                    spotId1, NativeVideoForm(nativeTemplateId53, fullCustomButton, true), false
                ),
                SpotNativeVideoDisplayInsert(
                    spotId1, aspectRatioId1, nativeTemplateId53, 178, 100, 320, 100,
                    null, true.toString(), 10, 11, "rgba(255,0,0,1.0)", "rgba(0,255,0,0.5)", "rgba(0,0,255,0.1)"
                )
            )
        }

        @Test
        @DisplayName("ネイティブテンプレートID:54")
        fun isNativeTemplate54() {
            assertEquals(
                // 閉じるボタンなし、広告拡大オフ、表示制御オン
                SpotNativeVideoDisplayInsert.of(
                    spotId2, NativeVideoForm(nativeTemplateId54, null, false), true
                ),
                SpotNativeVideoDisplayInsert(
                    spotId2, aspectRatioId1, nativeTemplateId54, 178, 100, 320, 100,
                    0, false.toString(), null, null, null, null, null
                )
            )
        }

        @Test
        @DisplayName("ネイティブテンプレートID:55")
        fun isNativeTemplate55() {
            assertEquals(
                // 閉じるボタンが空、広告拡大オフ、表示制御オフ
                SpotNativeVideoDisplayInsert.of(
                    spotId2, NativeVideoForm(nativeTemplateId55, emptyCustomButton, false), false
                ),
                SpotNativeVideoDisplayInsert(
                    spotId2, aspectRatioId1, nativeTemplateId55, 300, 169, 300, 250,
                    null, false.toString(), null, null, null, null, null
                )
            )
        }

        @Test
        @DisplayName("ネイティブテンプレートID:56")
        fun isNativeTemplate56() {
            assertEquals(
                // 閉じるボタン全入力、広告拡大オン、表示制御オン
                SpotNativeVideoDisplayInsert.of(
                    spotId2, NativeVideoForm(nativeTemplateId56, fullCustomButton, true), true
                ),
                SpotNativeVideoDisplayInsert(
                    spotId2, aspectRatioId1, nativeTemplateId56, 300, 169, 320, 250,
                    0, true.toString(), 10, 11, "rgba(255,0,0,1.0)", "rgba(0,255,0,0.5)", "rgba(0,0,255,0.1)"
                )
            )
        }

        @Test
        @DisplayName("対象外のネイティブテンプレートID")
        fun isInvalidNativeTemplateId() {
            assertThrows<CompassManagerException> {
                SpotNativeVideoDisplayInsert.of(spotId1, NativeVideoForm(NativeTemplateId(99), null, false), false)
            }
        }
    }
}
