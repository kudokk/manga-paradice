package jp.mangaka.ssp.infrastructure.datasource.dao.spotupstreamcurrency

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class SpotUpstreamCurrencyDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : SpotUpstreamCurrencyDao {
    override fun insert(spotUpstreamCurrency: SpotUpstreamCurrencyInsert) {
        jdbcWrapper.insertExecute({ it.withTableName("spot_upstream_currency") }, spotUpstreamCurrency)
    }

    override fun selectById(spotId: SpotId): SpotUpstreamCurrency? = jdbcWrapper.queryForObject(
        """
            SELECT *
            FROM spot_upstream_currency
            WHERE spot_id = :spotId
        """.trimIndent(),
        CustomMapSqlParameterSource("spotId", spotId),
        SpotUpstreamCurrency::class
    )
}
