package jp.mangaka.ssp.infrastructure.datasource.dao.campaign

import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.CampaignStatus
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class CampaignDaoImpl(
    @Autowired @Qualifier("CoreMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : CampaignDao {
    override fun selectByIdsAndStatuses(
        campaignIds: Collection<CampaignId>,
        statuses: Collection<CampaignStatus>
    ): List<CampaignCo> {
        if (campaignIds.isEmpty() || statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            makeSelectStructQuery(
                """
                    WHERE c.campaign_id IN (:campaignIds)
                      AND c.campaign_status IN (:statuses)
                      AND c.soft_delete_flag = 'open'
                """.trimIndent()
            ),
            CustomMapSqlParameterSource()
                .addValue("campaignIds", campaignIds)
                .addValue("statuses", statuses),
            CampaignCo::class
        )
    }

    override fun selectByCoAccountIdAndStatuses(
        coAccountId: CoAccountId,
        statuses: Collection<CampaignStatus>
    ): List<CampaignCo> {
        if (statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT
                  c.*,
                  cc.pureads_type
                FROM campaign c
                JOIN compass_master_db.compass_campaign cc
                  ON c.campaign_id = cc.campaign_id
                WHERE c.co_account_id = :coAccountId
                  AND c.campaign_status IN (:statuses)
                  AND c.soft_delete_flag = 'open'
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("coAccountId", coAccountId)
                .addValue("statuses", statuses),
            CampaignCo::class
        )
    }

    private fun makeSelectStructQuery(condition: String): String {
        val base = """
            SELECT
              c.*,
              cc.pureads_type
            FROM campaign c
            JOIN compass_master_db.compass_campaign cc
              ON c.campaign_id = cc.campaign_id
        """.trimIndent()

        return "$base\n$condition"
    }
}
