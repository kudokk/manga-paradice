package jp.mangaka.ssp.infrastructure.datasource.dao.relaystructcreative

import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class RelayStructCreativeDaoImpl(
    @Autowired @Qualifier("CoreMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : RelayStructCreativeDao {
    override fun selectByStructIds(structIds: Collection<StructId>): List<RelayStructCreative> {
        if (structIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM relay_struct_creative
                WHERE struct_id IN (:structIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("structIds", structIds),
            RelayStructCreative::class
        )
    }
}
