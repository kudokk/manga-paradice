package jp.mangaka.ssp.presentation.controller.spot.view.nativedesign

import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeElementId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElement
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElement.TrimType
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElement.ViewType
import jp.mangaka.ssp.util.StringUtils.escapeHtml

data class NativeDesignPreviewView(
    val nativeTemplateId: NativeTemplateId,
    val htmlCode: String,
    val cssCode: String,
    // 広告表記のみDBの登録値でプレビュー表示される（それ以外の項目はフロントの固定値）
    val adAttrText: String,
    val title: NativeDesignElementView?,
    val description: NativeDesignElementView?,
    val ctaButton: NativeDesignElementView?,
    val advertiser: NativeDesignElementView?,
    val image: NativeDesignElementView?,
    val logo: NativeDesignElementView?,
    val rating: NativeDesignElementView?,
    val price: NativeDesignElementView?,
    val domain: NativeDesignElementView?
) {
    data class NativeDesignElementView(
        val maxLength: Int?,
        val trimType: TrimType,
        val viewType: ViewType,
        val width: Int?,
        val height: Int?
    ) {
        companion object {
            /**
             * @param nativeTemplateElement ネイティブテンプレート要素のEntity
             * @return ネイティブテンプレート要素のView
             */
            fun of(nativeTemplateElement: NativeTemplateElement): NativeDesignElementView =
                NativeDesignElementView(
                    nativeTemplateElement.maxLength,
                    nativeTemplateElement.trimType,
                    nativeTemplateElement.viewType,
                    nativeTemplateElement.width,
                    nativeTemplateElement.height
                )
        }
    }

    companion object {
        /**
         * @param nativeTemplate ネイティブテンプレートのEntity
         * @return ネイティブデザインのプレビュー用のView
         */
        fun of(
            nativeTemplate: NativeTemplate,
            nativeTemplateElements: Collection<NativeTemplateElement>
        ): NativeDesignPreviewView {
            val nativeTemplateElementMap = nativeTemplateElements.associateBy { it.nativeElementId }
            return NativeDesignPreviewView(
                nativeTemplate.nativeTemplateId,
                escapeHtml(nativeTemplate.htmlCode.trim()),
                escapeHtml(nativeTemplate.cssCode.trim()),
                escapeHtml(nativeTemplate.adAttrText.trim()),
                nativeTemplateElementMap[NativeElementId.title]?.let { NativeDesignElementView.of(it) },
                nativeTemplateElementMap[NativeElementId.descriptions]?.let { NativeDesignElementView.of(it) },
                nativeTemplateElementMap[NativeElementId.ctaButton]?.let { NativeDesignElementView.of(it) },
                nativeTemplateElementMap[NativeElementId.advertiser]?.let { NativeDesignElementView.of(it) },
                nativeTemplateElementMap[NativeElementId.image]?.let { NativeDesignElementView.of(it) },
                nativeTemplateElementMap[NativeElementId.logo]?.let { NativeDesignElementView.of(it) },
                nativeTemplateElementMap[NativeElementId.rating]?.let { NativeDesignElementView.of(it) },
                nativeTemplateElementMap[NativeElementId.price]?.let { NativeDesignElementView.of(it) },
                nativeTemplateElementMap[NativeElementId.domain]?.let { NativeDesignElementView.of(it) }
            )
        }
    }
}
