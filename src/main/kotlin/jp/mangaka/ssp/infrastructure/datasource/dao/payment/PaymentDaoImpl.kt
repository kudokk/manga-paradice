package jp.mangaka.ssp.infrastructure.datasource.dao.payment

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class PaymentDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : PaymentDao {
    override fun selectBySpotIds(spotIds: Collection<SpotId>): List<Payment> {
        if (spotIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM payment
                WHERE spot_id IN (:spotIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("spotIds", spotIds),
            Payment::class
        )
    }
}
