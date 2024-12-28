package jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class SpotBannerDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : SpotBannerDao {
    override fun insert(spotBanner: SpotBannerInsert) {
        jdbcWrapper.insertExecute({ it.withTableName("spot_banner") }, spotBanner)
    }

    override fun selectById(spotId: SpotId): SpotBanner? {
        return jdbcWrapper.queryForObject(
            """
            SELECT *
            FROM spot_banner
            WHERE spot_id = :spotId
            """.trimIndent(),
            CustomMapSqlParameterSource("spotId", spotId),
            SpotBanner::class
        )
    }

    override fun deleteById(spotId: SpotId) {
        jdbcWrapper.update(
            """
                DELETE FROM spot_banner
                WHERE spot_id = :spotId
            """.trimIndent(),
            CustomMapSqlParameterSource("spotId", spotId)
        )
    }
}
