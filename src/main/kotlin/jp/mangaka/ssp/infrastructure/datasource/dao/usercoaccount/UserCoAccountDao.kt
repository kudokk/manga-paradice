package jp.mangaka.ssp.infrastructure.datasource.dao.usercoaccount

import jp.mangaka.ssp.application.valueobject.user.UserId

interface UserCoAccountDao {
    /**
     * @param userId ユーザーID
     * @return ユーザーに紐づくproduct_id=2のUserCoAccountのリスト
     */
    fun selectCompassUserCoAccountsByUserId(userId: UserId): List<UserCoAccount>
}
