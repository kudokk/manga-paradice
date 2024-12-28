package jp.mangaka.ssp.application.service.authentication

import jakarta.servlet.http.HttpSession
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster.CoAccountMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.usercoaccount.UserCoAccountDao
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster
import jp.mangaka.ssp.presentation.config.SessionConfig
import jp.mangaka.ssp.presentation.config.valueobject.SessionCoAccount
import org.springframework.stereotype.Service

@Service
class AuthenticationServiceImpl(
    private val coAccountMasterDao: CoAccountMasterDao,
    private val userCoAccountDao: UserCoAccountDao
) : AuthenticationService {
    override fun addSessionCoAccountInfoList(user: UserMaster, session: HttpSession) {
        val coAccountToRoleMap = userCoAccountDao
            .selectCompassUserCoAccountsByUserId(user.userId)
            .associateBy({ it.coAccountId }, { it.roleId })

        // user_co_accountにco_account_id=0のレコードがある場合は全Coアカウントが利用可能
        val sessionCoAccounts = if (coAccountToRoleMap.containsKey(CoAccountId.zero)) {
            coAccountMasterDao
                .selectCompassCoAccounts()
                // 全アカウントの場合はユーザーの権限を設定する
                .map { SessionCoAccount(it.coAccountId, it.coAccountName, user.userType) }
        } else {
            coAccountMasterDao
                .selectCompassCoAccountsByCoAccountIds(coAccountToRoleMap.keys)
                .map {
                    SessionCoAccount(
                        it.coAccountId,
                        it.coAccountName,
                        coAccountToRoleMap.getValue(it.coAccountId).toUserType()
                    )
                }
        }

        session.setAttribute(SessionConfig.SessionKey.CO_ACCOUNT_LIST.name, sessionCoAccounts)
    }
}
