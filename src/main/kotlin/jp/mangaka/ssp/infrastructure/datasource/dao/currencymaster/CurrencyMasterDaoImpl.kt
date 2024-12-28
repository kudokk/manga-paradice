package jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster

import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class CurrencyMasterDaoImpl(
    @Autowired @Qualifier("CoreMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : CurrencyMasterDao {
    override fun selectById(currencyId: CurrencyId): CurrencyMaster? = jdbcWrapper.queryForObject(
        """
            SELECT *
            FROM currency_master
            WHERE currency_id = :currencyId
        """.trimIndent(),
        CustomMapSqlParameterSource("currencyId", currencyId),
        CurrencyMaster::class
    )

    override fun selectAll(): List<CurrencyMaster> = jdbcWrapper.query(
        "SELECT * FROM currency_master",
        CurrencyMaster::class
    )
}
