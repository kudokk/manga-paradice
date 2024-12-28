package jp.mangaka.ssp.infrastructure.datasource.dao.decoration

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId

interface DecorationDao {
    /**
     * @param decorationId デコレーション設定ID
     * @return 引数のデコレーション設定IDに合致するDecoration
     */
    fun selectById(decorationId: DecorationId): Decoration?

    /**
     * @param coAccountIds CoアカウントIDのリスト
     * @return 引数のCoアカウントIDに紐づくDecorationのリスト
     */
    fun selectByCoAccountIds(coAccountIds: Collection<CoAccountId>): List<Decoration>
}
