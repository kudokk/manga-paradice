package jp.mangaka.ssp.infrastructure.datasource.dao.struct

import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class StructDaoImpl(
    @Autowired @Qualifier("CoreMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : StructDao {
    override fun selectByIdsAndStatuses(
        structIds: Collection<StructId>,
        statuses: Collection<StructStatus>
    ): List<StructCo> {
        if (structIds.isEmpty() || statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            makeSelectStructQuery(
                """
                    WHERE s.struct_id IN (:structIds)
                      AND s.struct_status IN (:statuses)
                      AND s.soft_delete_flag = 'open'
                """.trimIndent()
            ),
            CustomMapSqlParameterSource()
                .addValue("structIds", structIds)
                .addValue("statuses", statuses),
            StructCo::class
        )
    }

    override fun selectByCampaignIdsAndStatuses(
        campaignIds: Collection<CampaignId>,
        statuses: Collection<StructStatus>
    ): List<StructCo> {
        if (campaignIds.isEmpty() || statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            makeSelectStructQuery(
                """
                    WHERE s.soft_delete_flag = 'open' 
                      AND s.campaign_id IN (:campaignIds)
                      AND s.struct_status IN (:statuses)
                """.trimIndent()
            ),
            CustomMapSqlParameterSource()
                .addValue("campaignIds", campaignIds)
                .addValue("statuses", statuses),
            StructCo::class
        )
    }

    override fun selectByTimeTargetingIdAndStatuses(
        timeTargetingId: TimeTargetingId,
        statuses: Collection<StructStatus>
    ): List<StructCo> {
        if (statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            makeSelectStructQuery(
                """
                    WHERE cs.time_targeting_id = :timeTargetingId
                      AND s.struct_status IN (:statuses)
                      AND s.soft_delete_flag = 'open'
                """.trimIndent()
            ),
            CustomMapSqlParameterSource()
                .addValue("timeTargetingId", timeTargetingId)
                .addValue("statuses", statuses),
            StructCo::class
        )
    }

    private fun makeSelectStructQuery(condition: String): String {
        val base = """
            SELECT 
              s.*,
              cs.reseller_flag,
              cs.time_targeting_id
            FROM struct s
            JOIN compass_master_db.compass_struct cs
              ON s.struct_id = cs.struct_id
        """.trimIndent()

        return "$base\n$condition"
    }
}
