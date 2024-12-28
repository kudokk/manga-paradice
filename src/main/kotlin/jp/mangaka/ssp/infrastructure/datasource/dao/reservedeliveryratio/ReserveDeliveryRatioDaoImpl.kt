package jp.mangaka.ssp.infrastructure.datasource.dao.reservedeliveryratio

import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class ReserveDeliveryRatioDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : ReserveDeliveryRatioDao {
    override fun selectByStructIds(structIds: Collection<StructId>): List<ReserveDeliveryRatio> {
        if (structIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM reserve_delivery_ratio
                WHERE struct_id IN (:structIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("structIds", structIds),
            ReserveDeliveryRatio::class
        )
    }
}
