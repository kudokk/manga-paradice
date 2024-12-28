package jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate.NativeTemplateStatus

interface NativeTemplateDao {
    /**
     * @param statuses ネイティブテンプレートステータスのリスト
     * @return 引数のステータスに合致する全ユーザー共通のNativeTemplateのリスト
     */
    fun selectCommonsByStatuses(statuses: Collection<NativeTemplateStatus>): List<NativeTemplate>

    /**
     * @param coAccountId CoアカウントID
     * @param statuses ネイティブテンプレートステータスのリスト
     * @return 引数のCoアカウントID・ステータスに合致するNativeTemplateのリスト
     */
    fun selectPersonalsByCoAccountIdAndStatuses(
        coAccountId: CoAccountId,
        statuses: Collection<NativeTemplateStatus>
    ): List<NativeTemplate>

    /**
     * 共通定義・ユーザー定義の区別なく取得します.
     *
     * @param nativeTemplateId ネイティブテンプレートID
     * @param statuses ネイティブテンプレートステータスのリスト
     * @return 引数のネイティブテンプレートID・ステータスに合致するNativeTemplate
     */
    fun selectByIdAndStatues(
        nativeTemplateId: NativeTemplateId,
        statuses: Collection<NativeTemplateStatus>
    ): NativeTemplate?
}
