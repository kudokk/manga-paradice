package jp.mangaka.ssp.presentation.controller.coaccount.view

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount

data class CoAccountView(val coAccountId: CoAccountId, val coAccountName: String) {
    companion object {
        /**
         * @param coAccounts セッションに保存されているCoアカウント一覧
         * @return Coアカウント一覧のView
         */
        fun of(coAccounts: Collection<SessionCoAccount>): List<CoAccountView> =
            coAccounts.map { CoAccountView(it.coAccountId, it.coAccountName) }
    }
}
