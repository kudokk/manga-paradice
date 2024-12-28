package jp.mangaka.ssp.presentation.controller.spot.view.nativedesign

import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate

data class NativeDesignsView(
    val standards: List<NativeDesignView>,
    val videos: List<NativeDesignView>
) {
    data class NativeDesignView(
        val nativeTemplateId: NativeTemplateId,
        val nativeTemplateName: String,
        val platformId: PlatformId
    ) {

        companion object {
            /**
             * @param nativeTemplates ネイティブテンプレートのEntityのリスト
             * @return ネイティブデザインのViewのリスト
             */
            fun of(nativeTemplates: Collection<NativeTemplate>): List<NativeDesignView> =
                nativeTemplates.map { of(it) }

            fun of(nativeTemplate: NativeTemplate): NativeDesignView = NativeDesignView(
                nativeTemplate.nativeTemplateId,
                nativeTemplate.nativeTemplateName,
                nativeTemplate.platformId
            )
        }
    }
}
