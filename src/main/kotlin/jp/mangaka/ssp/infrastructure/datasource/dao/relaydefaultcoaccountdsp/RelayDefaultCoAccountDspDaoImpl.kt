package jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

@Repository
class RelayDefaultCoAccountDspDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : RelayDefaultCoAccountDspDao {
    override fun selectByCoAccountId(coAccountId: CoAccountId): List<RelayDefaultCoAccountDsp> =
        jdbcWrapper.query(
            """
                SELECT *
                FROM relay_default_co_account_dsp
                WHERE co_account_id = :coAccountId
            """.trimIndent(),
            MapSqlParameterSource("coAccountId", coAccountId.value),
            RelayDefaultCoAccountDsp::class
        )
}
