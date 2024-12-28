package jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster

import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class CountryMasterDaoImpl(
    @Autowired @Qualifier("CoreMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : CountryMasterDao {
    override fun selectAll(): List<CountryMaster> = jdbcWrapper.query(
        """
            SELECT *
            FROM country_master
        """.trimIndent(),
        CountryMaster::class
    )

    override fun selectById(countryId: CountryId): CountryMaster? = jdbcWrapper.queryForObject(
        """
            SELECT *
            FROM country_master
            WHERE country_id = :countryId
        """.trimIndent(),
        CustomMapSqlParameterSource("countryId", countryId),
        CountryMaster::class
    )

    override fun selectByIds(countryIds: Collection<CountryId>): List<CountryMaster> {
        if (countryIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM country_master
                WHERE country_id IN (:countryIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("countryIds", countryIds),
            CountryMaster::class
        )
    }
}
