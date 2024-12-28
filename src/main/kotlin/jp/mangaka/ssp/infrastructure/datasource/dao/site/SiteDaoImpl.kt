package jp.mangaka.ssp.infrastructure.datasource.dao.site

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class SiteDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : SiteDao {
    override fun selectByIdAndStatuses(siteId: SiteId, statuses: Collection<SiteStatus>): Site? {
        if (statuses.isEmpty()) return null

        return jdbcWrapper.queryForObject(
            """
                SELECT *
                FROM site
                WHERE site_id = :siteId
                  AND site_status IN (:statuses)
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("siteId", siteId)
                .addValue("statuses", statuses),
            Site::class
        )
    }

    override fun selectByCoAccountIdAndStatuses(
        coAccountId: CoAccountId,
        statuses: Collection<SiteStatus>
    ): List<Site> {
        if (statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM site
                WHERE co_account_id = :coAccountId
                  AND site_status IN (:statuses)
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("coAccountId", coAccountId)
                .addValue("statuses", statuses),
            Site::class
        )
    }
}
