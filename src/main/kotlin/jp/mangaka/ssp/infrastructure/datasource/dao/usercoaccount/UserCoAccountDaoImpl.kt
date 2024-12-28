package jp.mangaka.ssp.infrastructure.datasource.dao.usercoaccount

import jp.mangaka.ssp.application.valueobject.user.UserId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class UserCoAccountDaoImpl(
    @Autowired @Qualifier("CoreMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : UserCoAccountDao {
    override fun selectCompassUserCoAccountsByUserId(userId: UserId): List<UserCoAccount> =
        jdbcWrapper.query(
            """
                SELECT *
                FROM user_co_account
                WHERE user_id = :userId
                  AND product_id = 2
            """.trimIndent(),
            CustomMapSqlParameterSource("userId", userId),
            UserCoAccount::class
        )
}
