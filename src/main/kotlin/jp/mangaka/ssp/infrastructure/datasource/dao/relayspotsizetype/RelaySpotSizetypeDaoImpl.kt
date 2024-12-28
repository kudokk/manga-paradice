package jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype

import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class RelaySpotSizetypeDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : RelaySpotSizetypeDao {
    override fun bulkInsert(relaySpotSizetypeInserts: Collection<RelaySpotSizetypeInsert>) {
        if (relaySpotSizetypeInserts.isEmpty()) return

        jdbcWrapper.insertExecuteBatch({ it.withTableName("relay_spot_sizetype") }, relaySpotSizetypeInserts)
    }

    override fun selectBySpotId(spotId: SpotId): List<RelaySpotSizetype> = jdbcWrapper.query(
        """
            SELECT *
            FROM relay_spot_sizetype
            WHERE spot_id = :spotId
        """.trimIndent(),
        CustomMapSqlParameterSource("spotId", spotId),
        RelaySpotSizetype::class
    )

    override fun selectBySpotIds(spotIds: Collection<SpotId>): List<RelaySpotSizetype> {
        if (spotIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM relay_spot_sizetype
                WHERE spot_id IN (:spotIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("spotIds", spotIds),
            RelaySpotSizetype::class
        )
    }

    override fun deleteBySpotIdAndSizeTypeIds(spotId: SpotId, sizeTypeIds: Collection<SizeTypeId>) {
        if (sizeTypeIds.isEmpty()) return

        jdbcWrapper.update(
            """
                DELETE FROM relay_spot_sizetype
                WHERE spot_id = :spotId
                  AND size_type_id IN (:sizeTypeIds)
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("spotId", spotId)
                .addValue("sizeTypeIds", sizeTypeIds)
        )
    }
}
