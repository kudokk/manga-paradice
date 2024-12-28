package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class SpotVideoFloorCpmDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : SpotVideoFloorCpmDao {
    override fun inserts(spotVideoFloorCpms: Collection<SpotVideoFloorCpmInsert>) {
        if (spotVideoFloorCpms.isEmpty()) return

        jdbcWrapper.insertExecuteBatch({ it.withTableName("spot_video_floor_cpm") }, spotVideoFloorCpms)
    }

    override fun selectBySpotId(spotId: SpotId): List<SpotVideoFloorCpm> = jdbcWrapper.query(
        """
            SELECT *
            FROM spot_video_floor_cpm
            WHERE spot_id = :spotId
        """.trimIndent(),
        CustomMapSqlParameterSource("spotId", spotId),
        SpotVideoFloorCpm::class
    )

    override fun updates(spotVideoFloorCpms: Collection<SpotVideoFloorCpmUpdate>) {
        if (spotVideoFloorCpms.isEmpty()) return

        jdbcWrapper.bachUpdate(
            """
                UPDATE spot_video_floor_cpm
                SET floor_cpm = :floorCpm
                WHERE spot_id = :spotId
                  AND aspect_ratio_id = :aspectRatioId
                  AND start_date = :startDate
            """.trimIndent(),
            spotVideoFloorCpms
        )
    }

    override fun deleteBySpotIdAndAspectRatioIds(spotId: SpotId, aspectRatioIds: Collection<AspectRatioId>) {
        if (aspectRatioIds.isEmpty()) return

        jdbcWrapper.update(
            """
                DELETE FROM spot_video_floor_cpm
                WHERE spot_id = :spotId
                  AND aspect_ratio_id IN (:aspectRatioIds)
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("spotId", spotId)
                .addValue("aspectRatioIds", aspectRatioIds)
        )
    }
}
