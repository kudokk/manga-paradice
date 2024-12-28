package jp.mangaka.ssp.infrastructure.datasource.dao.datatime

import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class DateTimeDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : DateTimeDao {
    override fun selectCurrentDateTime(): LocalDateTime =
        jdbcWrapper.queryForObject("SELECT CURRENT_TIMESTAMP", MapSqlParameterSource(), LocalDateTime::class)!!
}
