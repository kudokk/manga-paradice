package jp.mangaka.ssp.infrastructure.datasource.dao.compassstruct

import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class CompassStructDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : CompassStructDao {
    override fun updateTimeTargetingId(structIds: Collection<StructId>, timeTargetingId: TimeTargetingId?) {
        if (structIds.isEmpty()) return

        jdbcWrapper.update(
            """
                UPDATE compass_struct
                SET time_targeting_id = :timeTargetingId
                WHERE struct_id IN (:structIds)
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("timeTargetingId", timeTargetingId)
                .addValue("structIds", structIds)
        )
    }
}
