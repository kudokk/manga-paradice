package jp.mangaka.ssp.infrastructure.datasource.dao.relayfixedfloorcpmspot

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class RelayFixedFloorCpmSpotDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : RelayFixedFloorCpmSpotDao {
    override fun selectBySpotIds(spotIds: Collection<SpotId>): List<RelayFixedFloorCpmSpot> {
        if (spotIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM relay_fixed_floor_cpm_spot
                WHERE spot_id IN (:spotIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("spotIds", spotIds),
            RelayFixedFloorCpmSpot::class
        )
    }
}
