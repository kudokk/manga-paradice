package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class SpotVideoDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : SpotVideoDao {
    override fun insert(spotVideo: SpotVideoInsert) {
        jdbcWrapper.insertExecute({ it.withTableName("spot_video") }, spotVideo)
    }

    override fun selectById(spotId: SpotId): SpotVideo? {
        return jdbcWrapper.queryForObject(
            """
            SELECT *
            FROM spot_video
            WHERE spot_id = :spotId
            """.trimIndent(),
            CustomMapSqlParameterSource("spotId", spotId),
            SpotVideo::class
        )
    }

    override fun update(spotVideo: SpotVideoUpdate) {
        jdbcWrapper.update(
            """
                UPDATE spot_video
                SET is_fixed_rotation_aspect_ratio = :isFixedRotationAspectRatio
                WHERE spot_id = :spotId
            """.trimIndent(),
            spotVideo
        )
    }

    override fun deleteById(spotId: SpotId) {
        jdbcWrapper.update(
            """
                DELETE FROM spot_video
                WHERE spot_id = :spotId
            """.trimIndent(),
            CustomMapSqlParameterSource("spotId", spotId)
        )
    }
}
