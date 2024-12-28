package jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class CoAccountMasterDaoImpl(
    @Autowired @Qualifier("CoreMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : CoAccountMasterDao {
    override fun selectCompassCoAccounts(): List<CoAccountMaster> = jdbcWrapper.query(
        """
            SELECT *
            FROM co_account_master
            WHERE product_id = 2
              AND soft_delete_flag = 'open'
        """.trimIndent(),
        CoAccountMaster::class
    )

    override fun selectByCoAccountId(coAccountId: CoAccountId): CoAccountMaster? = jdbcWrapper.queryForObject(
        """
            SELECT *
            FROM co_account_master
            WHERE product_id = 2
              AND soft_delete_flag = 'open'
              AND co_account_id = :coAccountId
        """.trimIndent(),
        CustomMapSqlParameterSource("coAccountId", coAccountId),
        CoAccountMaster::class
    )

    override fun selectCompassCoAccountsByCoAccountIds(coAccountIds: Collection<CoAccountId>): List<CoAccountMaster> {
        if (coAccountIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM co_account_master
                WHERE product_id = 2
                  AND soft_delete_flag = 'open'
                  AND co_account_id IN (:coAccountIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("coAccountIds", coAccountIds),
            CoAccountMaster::class
        )
    }
}
