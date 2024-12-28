package jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate.NativeTemplateStatus
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class NativeTemplateDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : NativeTemplateDao {
    override fun selectCommonsByStatuses(statuses: Collection<NativeTemplateStatus>): List<NativeTemplate> {
        if (statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM native_template
                WHERE co_account_id IS NULL
                  AND native_template_status IN (:statuses)
            """.trimIndent(),
            CustomMapSqlParameterSource("statuses", statuses),
            NativeTemplate::class
        )
    }

    override fun selectPersonalsByCoAccountIdAndStatuses(
        coAccountId: CoAccountId,
        statuses: Collection<NativeTemplateStatus>
    ): List<NativeTemplate> {
        if (statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM native_template
                WHERE co_account_id = :coAccountId
                  AND native_template_status IN (:statuses)
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("coAccountId", coAccountId)
                .addValue("statuses", statuses),
            NativeTemplate::class
        )
    }

    override fun selectByIdAndStatues(
        nativeTemplateId: NativeTemplateId,
        statuses: Collection<NativeTemplateStatus>
    ): NativeTemplate? {
        if (statuses.isEmpty()) return null

        return jdbcWrapper.queryForObject(
            """
                SELECT *
                FROM native_template
                WHERE native_template_id = :nativeTemplateId
                  AND native_template_status IN (:statuses)
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("nativeTemplateId", nativeTemplateId)
                .addValue("statuses", statuses),
            NativeTemplate::class
        )
    }
}
