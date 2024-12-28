package jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod

import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class TimeTargetingDayTypePeriodDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : TimeTargetingDayTypePeriodDao {
    override fun inserts(timeTargetingDayTypePeriods: Collection<TimeTargetingDayTypePeriodInsert>) {
        if (timeTargetingDayTypePeriods.isEmpty()) return

        jdbcWrapper.insertExecuteBatch(
            { it.withTableName("time_targeting_day_type_period") },
            timeTargetingDayTypePeriods
        )
    }

    override fun selectById(timeTargetingId: TimeTargetingId): List<TimeTargetingDayTypePeriod> =
        jdbcWrapper.query(
            """
                SELECT *
                FROM time_targeting_day_type_period
                WHERE time_targeting_id = :timeTargetingId
            """.trimIndent(),
            CustomMapSqlParameterSource("timeTargetingId", timeTargetingId),
            TimeTargetingDayTypePeriod::class
        )

    override fun selectByIds(timeTargetingIds: Collection<TimeTargetingId>): List<TimeTargetingDayTypePeriod> {
        if (timeTargetingIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM time_targeting_day_type_period
                WHERE time_targeting_id IN (:timeTargetingIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("timeTargetingIds", timeTargetingIds),
            TimeTargetingDayTypePeriod::class
        )
    }

    override fun deletes(conditions: Collection<TimeTargetingDayTypePeriodDelete>) {
        if (conditions.isEmpty()) return

        jdbcWrapper.bachUpdate(
            """
                DELETE FROM time_targeting_day_type_period
                WHERE time_targeting_id = :timeTargetingId
                  AND day_type = :dayType
                  AND start_time = :startTime
                  AND end_time = :endTime
            """.trimIndent(),
            conditions
        )
    }
}
