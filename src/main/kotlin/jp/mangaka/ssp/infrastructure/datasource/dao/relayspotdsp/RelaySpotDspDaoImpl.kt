package jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp

import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class RelaySpotDspDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : RelaySpotDspDao {
    override fun bulkInsert(dsps: Collection<RelaySpotDspInsert>) {
        if (dsps.isEmpty()) return

        jdbcWrapper.insertExecuteBatch({ it.withTableName("relay_spot_dsp") }, dsps)
    }

    override fun selectBySpotId(spotId: SpotId): List<RelaySpotDsp> = jdbcWrapper.query(
        """
            SELECT *
            FROM relay_spot_dsp
            WHERE spot_id = :spotId
        """.trimIndent(),
        CustomMapSqlParameterSource("spotId", spotId),
        RelaySpotDsp::class
    )

    override fun bulkUpdate(dsps: Collection<RelaySpotDspInsert>) {
        if (dsps.isEmpty()) return

        jdbcWrapper.bachUpdate(
            """
                UPDATE relay_spot_dsp
                SET bid_adjust = :bidAdjust
                   ,priority = :priority
                   ,floor_cpm = :floorCpm
                WHERE spot_id = :spotId
                  AND dsp_id = :dspId
            """.trimIndent(),
            dsps
        )
    }

    override fun deleteBySpotIdAndDspIds(spotId: SpotId, dspIds: Collection<DspId>) {
        if (dspIds.isEmpty()) return

        jdbcWrapper.update(
            """
                DELETE FROM relay_spot_dsp
                WHERE spot_id = :spotId
                  AND dsp_id IN (:dspIds)
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("spotId", spotId)
                .addValue("dspIds", dspIds)
        )
    }
}
