package jp.mangaka.ssp.infrastructure.datasource.dao.decoration

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class DecorationDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : DecorationDao {
    override fun selectById(decorationId: DecorationId): Decoration? = jdbcWrapper.queryForObject(
        """
            SELECT *
            FROM decoration
            WHERE decoration_id = :decorationId
        """.trimIndent(),
        CustomMapSqlParameterSource("decorationId", decorationId),
        Decoration::class
    )

    override fun selectByCoAccountIds(coAccountIds: Collection<CoAccountId>): List<Decoration> {
        if (coAccountIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM decoration
                WHERE co_account_id IN (:coAccountIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("coAccountIds", coAccountIds),
            Decoration::class
        )
    }
}
