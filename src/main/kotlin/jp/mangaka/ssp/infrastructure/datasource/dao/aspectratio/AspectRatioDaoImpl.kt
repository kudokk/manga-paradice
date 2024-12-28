package jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class AspectRatioDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : AspectRatioDao {
    override fun selectByStatuses(statuses: Collection<AspectRatio.AspectRatioStatus>): List<AspectRatio> {
        if (statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM aspect_ratio
                WHERE aspect_ratio_status IN (:statuses)
            """.trimIndent(),
            CustomMapSqlParameterSource("statuses", statuses),
            AspectRatio::class
        )
    }

    override fun selectByAspectRatioIdsAndStatuses(
        aspectRatioIds: Collection<AspectRatioId>,
        statuses: Collection<AspectRatio.AspectRatioStatus>
    ): List<AspectRatio> {
        if (aspectRatioIds.isEmpty() || statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM aspect_ratio
                WHERE aspect_ratio_id IN (:aspectRatioIds)
                  AND aspect_ratio_status IN (:statuses)
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("aspectRatioIds", aspectRatioIds)
                .addValue("statuses", statuses),
            AspectRatio::class
        )
    }
}
