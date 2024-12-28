package jp.mangaka.ssp.infrastructure.datasource.dao.spotnative

import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeStandardForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeVideoForm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SpotNativeInsertのテスト")
private class SpotNativeInsertTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
        val nativeTemplateId1 = NativeTemplateId(1)
        val nativeTemplateId2 = NativeTemplateId(2)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val standardForm = NativeStandardForm(nativeTemplateId1, null)
        val videoForm = NativeVideoForm(nativeTemplateId2, null, true)

        @Test
        @DisplayName("通常デザインのみ")
        fun isStandardOnly() {
            assertEquals(
                SpotNativeInsert.of(spotId1, NativeSettingForm(standardForm, null)),
                SpotNativeInsert(spotId1, nativeTemplateId1)
            )
        }

        @Test
        @DisplayName("動画デザインのみ")
        fun isVideoOnly() {
            assertEquals(
                SpotNativeInsert.of(spotId2, NativeSettingForm(null, videoForm)),
                SpotNativeInsert(spotId2, nativeTemplateId2)
            )
        }

        @Test
        @DisplayName("通常・ビデオ両方")
        fun isStandardAndVideo() {
            assertEquals(
                SpotNativeInsert.of(spotId2, NativeSettingForm(standardForm, videoForm)),
                SpotNativeInsert(spotId2, nativeTemplateId1)
            )
        }
    }
}
