package jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement

import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class NativeTemplateElementDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : NativeTemplateElementDao {
    override fun selectByNativeTemplateIds(
        nativeTemplateIds: Collection<NativeTemplateId>
    ): List<NativeTemplateElement> {
        if (nativeTemplateIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM native_template_element
                WHERE native_template_id IN (:nativeTemplateIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("nativeTemplateIds", nativeTemplateIds),
            NativeTemplateElement::class
        )
    }
}
