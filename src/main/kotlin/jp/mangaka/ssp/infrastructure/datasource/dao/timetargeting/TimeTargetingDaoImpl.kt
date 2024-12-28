package jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class TimeTargetingDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : TimeTargetingDao {
    override fun insert(timeTargeting: TimeTargetingInsert): TimeTargetingId = jdbcWrapper.insertExecuteAndReturnId(
        { it.withTableName("time_targeting").usingGeneratedKeyColumns("time_targeting_id") },
        timeTargeting
    ).let { TimeTargetingId(it.toInt()) }

    override fun selectByIdAndStatuses(
        timeTargetingId: TimeTargetingId,
        statuses: Collection<TimeTargetingStatus>
    ): TimeTargeting? {
        if (statuses.isEmpty()) return null

        return jdbcWrapper.queryForObject(
            """
                SELECT *
                FROM time_targeting
                WHERE time_targeting_id = :timeTargetingId
                  AND time_targeting_status IN (:statuses)
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("timeTargetingId", timeTargetingId)
                .addValue("statuses", statuses),
            TimeTargeting::class
        )
    }

    override fun selectByCoAccountIdAndStatuses(
        coAccountId: CoAccountId,
        statuses: Collection<TimeTargetingStatus>,
        limit: Int,
        offset: Int
    ): List<TimeTargeting> {
        if (statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM time_targeting
                WHERE co_account_id = :coAccountId
                  AND time_targeting_status IN (:statuses)
                ORDER BY time_targeting_id 
                LIMIT :limit OFFSET :offset
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("coAccountId", coAccountId)
                .addValue("statuses", statuses)
                .addValue("limit", limit)
                .addValue("offset", offset),
            TimeTargeting::class
        )
    }

    override fun update(timeTargeting: TimeTargetingUpdate) {
        jdbcWrapper.update(
            """
                UPDATE time_targeting
                SET time_targeting_name = :timeTargetingName
                   ,time_targeting_status = :timeTargetingStatus
                   ,country_id = :countryId
                   ,is_active_holiday = :isActiveHoliday
                   ,description = :description
                WHERE time_targeting_id = :timeTargetingId
            """.trimIndent(),
            timeTargeting
        )
    }

    override fun updateStatus(timeTargetingId: TimeTargetingId, status: TimeTargetingStatus) {
        jdbcWrapper.update(
            """
                UPDATE time_targeting
                SET time_targeting_status = :status
                WHERE time_targeting_id = :timeTargetingId
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("timeTargetingId", timeTargetingId)
                .addValue("status", status)
        )
    }
}
