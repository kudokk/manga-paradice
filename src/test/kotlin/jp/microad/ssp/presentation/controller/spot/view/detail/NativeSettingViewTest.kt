package jp.mangaka.ssp.presentation.controller.spot.view.detail

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplay
import jp.mangaka.ssp.presentation.controller.spot.view.detail.CloseButtonView.ColorView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.NativeSettingView.NativeStandardView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.NativeSettingView.NativeVideoView
import jp.mangaka.ssp.presentation.controller.spot.view.nativedesign.NativeDesignsView.NativeDesignView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("NativeSettingViewのテスト")
private class NativeSettingViewTest {
    companion object {
        val nativeTemplateId1 = NativeTemplateId(1)
        val nativeTemplateId2 = NativeTemplateId(2)
        val nativeTemplateId3 = NativeTemplateId(3)
        val nativeTemplateId4 = NativeTemplateId(4)
        val platformId1 = PlatformId(1)
        val platformId2 = PlatformId(2)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val spotNativeDisplay1 = SpotNativeDisplay(
            mock(), nativeTemplateId1, null, null, null, null, 1, 100, "rgba(1,23,456,0.1)",
            "rgba(23,456,1,1.0)", "rgba(456,1,23,0.7)"
        )
        val spotNativeDisplay2 = SpotNativeDisplay(
            mock(), nativeTemplateId2, null, null, null, null, null, null, null, null, null
        )
        val spotNativeVideoDisplay3 = SpotNativeVideoDisplay(
            mock(), nativeTemplateId3, null, null, null, null, true, 3, 300, "rgba(987,65,4,1.0)",
            "rgba(65,4,987,0.1)", "rgba(4,987,65,0.2)"
        )
        val spotNativeVideoDisplay4 = SpotNativeVideoDisplay(
            mock(), nativeTemplateId4, null, null, null, null, false, null, null, null, null, null
        )
        val nativeTemplate1 = mockNativeTemplate(nativeTemplateId1, "template1", platformId1)
        val nativeTemplate2 = mockNativeTemplate(nativeTemplateId2, "template2", platformId2)
        val nativeTemplate3 = mockNativeTemplate(nativeTemplateId3, "template3", platformId2)
        val nativeTemplate4 = mockNativeTemplate(nativeTemplateId4, "template4", platformId1)

        @Test
        @DisplayName("全項目入力あり")
        fun isFull() {
            val actual = NativeSettingView.of(
                spotNativeDisplay1,
                nativeTemplate1,
                spotNativeVideoDisplay3,
                nativeTemplate3
            )

            assertEquals(
                NativeSettingView(
                    NativeStandardView(
                        NativeDesignView(nativeTemplateId1, "template1", platformId1),
                        CloseButtonView(
                            1, 100, ColorView(1, 23, 456, 0.1), ColorView(23, 456, 1, 1.0), ColorView(456, 1, 23, 0.7)
                        )
                    ),
                    NativeVideoView(
                        NativeDesignView(nativeTemplateId3, "template3", platformId2),
                        CloseButtonView(
                            3, 300, ColorView(987, 65, 4, 1.0), ColorView(65, 4, 987, 0.1), ColorView(4, 987, 65, 0.2)
                        ),
                        true
                    )
                ),
                actual
            )
        }

        @Test
        @DisplayName("必須項目のみ")
        fun isNotFull() {
            val actual = NativeSettingView.of(
                spotNativeDisplay2,
                nativeTemplate2,
                spotNativeVideoDisplay4,
                nativeTemplate4
            )

            assertEquals(
                NativeSettingView(
                    NativeStandardView(
                        NativeDesignView(nativeTemplateId2, "template2", platformId2), null
                    ),
                    NativeVideoView(
                        NativeDesignView(nativeTemplateId4, "template4", platformId1), null, false
                    )
                ),
                actual
            )
        }

        @Test
        @DisplayName("未設定")
        fun isEmpty() {
            // 本来はどちらもNULLはあり得ないがテストのため
            val actual = NativeSettingView.of(null, null, null, null)

            assertEquals(NativeSettingView(null, null), actual)
        }

        private fun mockNativeTemplate(
            nativeTemplateId: NativeTemplateId,
            nativeTemplateName: String,
            platformId: PlatformId
        ): NativeTemplate = mock {
            on { this.nativeTemplateId } doReturn nativeTemplateId
            on { this.nativeTemplateName } doReturn nativeTemplateName
            on { this.platformId } doReturn platformId
        }
    }
}
