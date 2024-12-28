package jp.mangaka.ssp.infrastructure.datasource.dao.spot

import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class SpotDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : SpotDao {
    override fun insert(spot: SpotInsert): SpotId = jdbcWrapper.insertExecuteAndReturnId(
        """
            INSERT INTO spot (
              site_id, spot_name, spot_status, spot_type, platform_id, display_type, upstream_type, delivery_method,
              width, height, rotation_max, is_amp, propriety_dsp_id, descriptions, page_url, create_time
            ) VALUES (
              :siteId, :spotName, :spotStatus, :spotType, :platformId, :displayType, :upstreamType, :deliveryMethod,
              :width, :height, :rotationMax, :isAmp, :proprietyDspId, :descriptions, :pageUrl, NOW()
            )
        """.trimIndent(),
        spot
    ).let { SpotId(it.toInt()) }

    override fun selectByIdAndStatus(spotId: SpotId, statuses: Collection<SpotStatus>): Spot? {
        if (statuses.isEmpty()) return null

        return jdbcWrapper.queryForObject(
            """
                SELECT *
                FROM spot
                WHERE spot_id = :spotId
                  AND spot_status IN (:statuses)
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("spotId", spotId)
                .addValue("statuses", statuses),
            Spot::class
        )
    }

    override fun selectBySiteIdsAndStatuses(
        siteIds: Collection<SiteId>,
        statuses: Collection<SpotStatus>,
        limit: Int,
        offset: Int
    ): List<Spot> {
        if (siteIds.isEmpty() || statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM spot
                WHERE site_id IN (:siteIds)
                  AND spot_status IN (:statuses)
                ORDER BY spot_id
                LIMIT :limit OFFSET :offset
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("siteIds", siteIds)
                .addValue("statuses", statuses)
                .addValue("limit", limit)
                .addValue("offset", offset),
            Spot::class
        )
    }

    override fun update(spot: SpotUpdate) {
        jdbcWrapper.update(
            """
                UPDATE spot
                SET spot_name = :spotName
                   ,width = :width
                   ,height = :height
                   ,descriptions = :descriptions
                   ,page_url = :pageUrl
                WHERE spot_id = :spotId
            """.trimIndent(),
            spot
        )
    }

    override fun updateRotationMaxById(spotId: SpotId, rotationMax: Int) {
        jdbcWrapper.update(
            """
                UPDATE spot
                SET rotation_max = :rotationMax
                WHERE spot_id = :spotId
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("rotationMax", rotationMax)
                .addValue("spotId", spotId)
        )
    }
}
