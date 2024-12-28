package jp.mangaka.ssp.infrastructure.datasource.dao.operationlog

import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class CompassUserOperationLogDaoImpl(
    @Autowired @Qualifier("CompassLogJdbc") private val jdbcWrapper: JdbcWrapper
) : CompassUserOperationLogDao {
    override fun insert(userOperationLog: CompassUserOperationLogInsert) {
        jdbcWrapper.update(
            """
                INSERT INTO compass_user_operation_log
                  (ip_address, user_id, co_account_id, exec_query, exec_parameters, soft_delete_flag, create_time)
                VALUES
                  (:ipAddress, :userId, :coAccountId, :execQuery, :execParameters, 'open', NOW())
            """.trimIndent(),
            userOperationLog
        )
    }

    data class UserOperationLog(val execQuery: String, val execParameters: String)
}
