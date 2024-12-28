package jp.mangaka.ssp.infrastructure.datasource.dao.fixedfloorcpm

import jp.mangaka.ssp.application.valueobject.deal.DealId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class FixedFloorCpmDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : FixedFloorCpmDao {
    override fun selectByIds(dealIds: Collection<DealId>): List<FixedFloorCpm> {
        if (dealIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM fixed_floor_cpm
                WHERE deal_id IN (:dealIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("dealIds", dealIds),
            FixedFloorCpm::class
        )
    }
}
