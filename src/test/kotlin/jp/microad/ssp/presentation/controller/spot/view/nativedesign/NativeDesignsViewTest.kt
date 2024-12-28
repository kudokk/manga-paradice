package jp.mangaka.ssp.presentation.controller.spot.view.nativedesign

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate
import jp.mangaka.ssp.presentation.controller.spot.view.nativedesign.NativeDesignsView.NativeDesignView
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("NativeDesignsViewのテスト")
private class NativeDesignsViewTest {
    @Nested
    @DisplayName("NativeDesignViewの一覧生成のテスト")
    inner class NativeDesignViewsFactoryTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = NativeDesignView.of(
                listOf(
                    mockNativeTemplate(1, null, "テンプレート1", 10),
                    mockNativeTemplate(2, 1, "テンプレート2", 10),
                    mockNativeTemplate(3, 1, "テンプレート3", 11),
                )
            )

            assertEquals(
                listOf(
                    nativeDesignView(1, "テンプレート1", 10),
                    nativeDesignView(2, "テンプレート2", 10),
                    nativeDesignView(3, "テンプレート3", 11),
                ),
                actual
            )
        }

        @Test
        @DisplayName("引数のEntityのリストが空")
        fun isEmptyNativeTemplates() {
            val actual = NativeDesignView.of(emptyList())

            assertEmpty(actual)
        }
    }

    private fun mockNativeTemplate(
        nativeTemplateId: Int, coAccountId: Int?, nativeTemplateName: String, platformId: Int
    ): NativeTemplate = mock {
        on { this.nativeTemplateId } doReturn NativeTemplateId(nativeTemplateId)
        on { this.coAccountId } doReturn coAccountId?.let { CoAccountId(it) }
        on { this.nativeTemplateName } doReturn nativeTemplateName
        on { this.platformId } doReturn PlatformId(platformId)
    }

    private fun nativeDesignView(
        nativeTemplateId: Int, nativeTemplateName: String, platformId: Int
    ) = NativeDesignView(NativeTemplateId(nativeTemplateId), nativeTemplateName, PlatformId(platformId))
}
