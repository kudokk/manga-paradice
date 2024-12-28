package jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement

import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId

interface NativeTemplateElementDao {
    /**
     * @param nativeTemplateIds ネイティブテンプレートIDのリスト
     * @return 引数のネイティブテンプレートIDに合致するNativeTemplateElementのリスト
     */
    fun selectByNativeTemplateIds(nativeTemplateIds: Collection<NativeTemplateId>): List<NativeTemplateElement>
}
