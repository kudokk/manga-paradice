package jp.mangaka.ssp.infrastructure.datasource.dao.relaystructspot

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class RelayStructSpotDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : RelayStructSpotDao {
    override fun selectBySpotId(spotId: SpotId): List<RelayStructSpot> = jdbcWrapper.query(
        """
            SELECT *
            FROM relay_struct_spot
            WHERE spot_id = :spotId
        """.trimIndent(),
        CustomMapSqlParameterSource("spotId", spotId),
        RelayStructSpot::class
    )

    override fun selectBySpotIds(spotIds: Collection<SpotId>): List<RelayStructSpot> {
        if (spotIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM relay_struct_spot
                WHERE spot_id IN (:spotIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("spotIds", spotIds),
            RelayStructSpot::class
        )
    }

    override fun selectByStructIds(structIds: Collection<StructId>): List<RelayStructSpot> {
        if (structIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM relay_struct_spot
                WHERE struct_id IN (:structIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("structIds", structIds),
            RelayStructSpot::class
        )
    }
}
