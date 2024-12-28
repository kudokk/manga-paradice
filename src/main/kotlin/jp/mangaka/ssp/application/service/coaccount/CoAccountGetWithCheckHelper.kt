package jp.mangaka.ssp.application.service.coaccount

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster.CoAccountMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster.CoAccountMasterDao
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.springframework.stereotype.Component

@Component
class CoAccountGetWithCheckHelper(
    private val coAccountMasterDao: CoAccountMasterDao
) {
    /**
     * @param coAccountId CoアカウントID
     * @return 引数のCoアカウントに合致する有効なCoAccountMaster
     * @throws CompassManagerException 引数のCoアカウントに合致する有効なCoAccountMasterが取得できなかったとき
     */
    fun getCoAccountWithCheck(coAccountId: CoAccountId): CoAccountMaster = coAccountMasterDao
        .selectByCoAccountId(coAccountId)
        ?: throw CompassManagerException("CoアカウントID:${coAccountId}に合致するエンティティが取得できませんでした。")
}
