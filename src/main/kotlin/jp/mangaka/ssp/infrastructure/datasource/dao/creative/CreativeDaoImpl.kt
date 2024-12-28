package jp.mangaka.ssp.infrastructure.datasource.dao.creative

import jp.mangaka.ssp.application.valueobject.creative.CreativeId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.dao.creative.Creative.CreativeStatus
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class CreativeDaoImpl(
    @Autowired @Qualifier("CoreMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : CreativeDao {
    override fun selectByIdsAndStatuses(
        creativeIds: Collection<CreativeId>,
        statuses: Collection<CreativeStatus>
    ): List<Creative> {
        if (creativeIds.isEmpty() || statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT c.*
                FROM creative c
                JOIN compass_master_db.compass_creative cc
                  ON c.creative_id = cc.creative_id
                WHERE c.creative_id IN (:creativeIds)
                  AND c.creative_status IN (:statuses)
                  AND c.soft_delete_flag = 'open'
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("creativeIds", creativeIds)
                .addValue("statuses", statuses),
            Creative::class
        )
    }
}
