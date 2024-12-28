package jp.mangaka.ssp.presentation.controller.spot.view.nativedesign

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeElementId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElement
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElement.TrimType
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElement.ViewType
import jp.mangaka.ssp.presentation.controller.spot.view.nativedesign.NativeDesignPreviewView.NativeDesignElementView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("NativeDesignPreviewViewのテスト")
private class NativeDesignPreviewViewTest {
    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @Test
        @DisplayName("タイトル/説明文/CTAボタン/主体者表記/画像の設定あり")
        fun isCorrect1() {
            val nativeTemplate = mockNativeTemplate(1, "<div>html1</div>", "<style>css1</style>", "<div>adAttr1</div>")
            val nativeTemplateElements = listOf(
                mockNativeTemplateElement(
                    NativeElementId.title, 100, TrimType.dot, ViewType.cut, 100, 200
                ),
                mockNativeTemplateElement(
                    NativeElementId.descriptions, 200, TrimType.chunk, ViewType.minify, 110, 210
                ),
                mockNativeTemplateElement(
                    NativeElementId.ctaButton, null, TrimType.dot, ViewType.minify, null, null
                ),
                mockNativeTemplateElement(
                    NativeElementId.advertiser, null, TrimType.chunk, ViewType.cut, 130, null
                ),
                mockNativeTemplateElement(
                    NativeElementId.image, 300, TrimType.dot, ViewType.minify, null, 240
                )
            )

            val actual = NativeDesignPreviewView.of(nativeTemplate, nativeTemplateElements)

            assertEquals(
                NativeDesignPreviewView(
                    NativeTemplateId(1),
                    "&lt;div&gt;html1&lt;/div&gt;",
                    "&lt;style&gt;css1&lt;/style&gt;",
                    "&lt;div&gt;adAttr1&lt;/div&gt;",
                    NativeDesignElementView(100, TrimType.dot, ViewType.cut, 100, 200),
                    NativeDesignElementView(200, TrimType.chunk, ViewType.minify, 110, 210),
                    NativeDesignElementView(null, TrimType.dot, ViewType.minify, null, null),
                    NativeDesignElementView(null, TrimType.chunk, ViewType.cut, 130, null),
                    NativeDesignElementView(300, TrimType.dot, ViewType.minify, null, 240),
                    null,
                    null,
                    null,
                    null
                ),
                actual
            )
        }

        @Test
        @DisplayName("ロゴ/レーティング/プライス/広告主ドメインの設定あり")
        fun isCorrect2() {
            val nativeTemplate = mockNativeTemplate(2, "<div>html2</div>", "<style>css2</style>", "<div>adAttr2</div>")
            val nativeTemplateElements = listOf(
                mockNativeTemplateElement(
                    NativeElementId.logo, 100, TrimType.dot, ViewType.cut, 100, 200
                ),
                mockNativeTemplateElement(
                    NativeElementId.rating, null, TrimType.dot, ViewType.minify, null, null
                ),
                mockNativeTemplateElement(
                    NativeElementId.price, 200, TrimType.chunk, ViewType.cut, null, 220
                ),
                mockNativeTemplateElement(
                    NativeElementId.domain, 300, TrimType.chunk, ViewType.minify, 130, null
                )
            )

            val actual = NativeDesignPreviewView.of(nativeTemplate, nativeTemplateElements)

            assertEquals(
                NativeDesignPreviewView(
                    NativeTemplateId(2),
                    "&lt;div&gt;html2&lt;/div&gt;",
                    "&lt;style&gt;css2&lt;/style&gt;",
                    "&lt;div&gt;adAttr2&lt;/div&gt;",
                    null,
                    null,
                    null,
                    null,
                    null,
                    NativeDesignElementView(100, TrimType.dot, ViewType.cut, 100, 200),
                    NativeDesignElementView(null, TrimType.dot, ViewType.minify, null, null),
                    NativeDesignElementView(200, TrimType.chunk, ViewType.cut, null, 220),
                    NativeDesignElementView(300, TrimType.chunk, ViewType.minify, 130, null)
                ),
                actual
            )
        }
    }

    private fun mockNativeTemplate(
        nativeTemplateId: Int, htmlCode: String, cssCode: String, adAttrText: String
    ): NativeTemplate = mock {
        on { this.nativeTemplateId } doReturn NativeTemplateId(nativeTemplateId)
        on { this.htmlCode } doReturn htmlCode
        on { this.cssCode } doReturn cssCode
        on { this.adAttrText } doReturn adAttrText
    }

    private fun mockNativeTemplateElement(
        nativeElementId: NativeElementId,
        maxLength: Int?,
        trimType: TrimType,
        viewType: ViewType,
        width: Int?,
        height: Int?
    ): NativeTemplateElement = mock {
        on { this.nativeElementId } doReturn nativeElementId
        on { this.maxLength } doReturn maxLength
        on { this.trimType } doReturn trimType
        on { this.viewType } doReturn viewType
        on { this.width } doReturn width
        on { this.height } doReturn height
    }
}
