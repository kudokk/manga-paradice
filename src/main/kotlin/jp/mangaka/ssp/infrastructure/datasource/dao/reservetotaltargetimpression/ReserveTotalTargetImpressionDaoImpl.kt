package jp.mangaka.ssp.infrastructure.datasource.dao.reservetotaltargetimpression

import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class ReserveTotalTargetImpressionDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : ReserveTotalTargetImpressionDao {
    override fun selectByStructIds(structIds: Collection<StructId>): List<ReserveTotalTargetImpression> {
        if (structIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM reserve_total_target_impression
                WHERE struct_id IN (:structIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("structIds", structIds),
            ReserveTotalTargetImpression::class
        )
    }
}
