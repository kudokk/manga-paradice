package jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId

interface CoAccountMasterDao {
    /**
     * @return product_id=2のCoAccountMasterのリスト
     */
    fun selectCompassCoAccounts(): List<CoAccountMaster>

    /**
     * @param coAccountId CoアカウントID
     * @return 引数のCoアカウントIDに合致するproduct_id=2のCoAccountMaster
     */
    fun selectByCoAccountId(coAccountId: CoAccountId): CoAccountMaster?

    /**
     * @param coAccountIds CoアカウントIDのリスト
     * @return 引数のCoアカウントIDに合致するproduct_id=2のCoAccountMasterのリスト
     */
    fun selectCompassCoAccountsByCoAccountIds(coAccountIds: Collection<CoAccountId>): List<CoAccountMaster>
}
