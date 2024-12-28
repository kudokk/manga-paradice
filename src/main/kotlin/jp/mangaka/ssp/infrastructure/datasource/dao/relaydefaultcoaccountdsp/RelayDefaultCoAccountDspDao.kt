package jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId

interface RelayDefaultCoAccountDspDao {
    /**
     * @param coAccountId CoアカウントID
     * @return 引数のCoアカウントIDに合致するRelayDefaultCoAccountDspのリスト
     */
    fun selectByCoAccountId(coAccountId: CoAccountId): List<RelayDefaultCoAccountDsp>
}
