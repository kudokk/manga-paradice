package jp.mangaka.ssp.infrastructure.datasource.dao.reservetotallimitimpression

import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class ReserveTotalLimitImpressionDaoImpl(
    @Autowired @Qualifier("CoreMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : ReserveTotalLimitImpressionDao {
    override fun selectByStructIds(structIds: Collection<StructId>): List<ReserveTotalLimitImpression> {
        if (structIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM reserve_total_limit_impression
                WHERE struct_id IN (:structIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("structIds", structIds),
            ReserveTotalLimitImpression::class
        )
    }
}
