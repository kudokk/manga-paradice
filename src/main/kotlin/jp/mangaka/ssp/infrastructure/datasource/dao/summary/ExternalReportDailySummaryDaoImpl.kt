package jp.mangaka.ssp.infrastructure.datasource.dao.summary

import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.SummaryUtils.makeGroupByCondition
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.SummaryUtils.makeSelectCondition
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.condition.SpotSummaryCondition
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.SpotRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.TotalRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.SpotSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.TotalSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomBeanPropertySqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import kotlin.reflect.KClass

@Repository
class ExternalReportDailySummaryDaoImpl(
    @Autowired @Qualifier("CompassSummaryJdbc") private val jdbcWrapper: JdbcWrapper
) : ExternalReportDailySummaryDao {
    override fun selectTotalSpotSummaryByCondition(
        condition: SpotSummaryCondition
    ): TotalSummaryCo = selectSummaryByCondition(condition, TotalSummaryCo::class)
        .getOrNull(0) ?: TotalSummaryCo.zero

    override fun selectSpotSummaryByCondition(
        condition: SpotSummaryCondition
    ): List<SpotSummaryCo> = selectSummaryByCondition(condition, SpotSummaryCo::class)

    override fun selectTotalSpotRequestSummaryByCondition(
        condition: SpotSummaryCondition
    ): TotalRequestSummaryCo = selectRequestSummaryByCondition(condition, TotalRequestSummaryCo::class)
        .getOrNull(0) ?: TotalRequestSummaryCo.zero

    override fun selectSpotRequestSummaryByCondition(
        condition: SpotSummaryCondition
    ): List<SpotRequestSummaryCo> = selectRequestSummaryByCondition(condition, SpotRequestSummaryCo::class)

    private fun <T : SummaryCo<T>> selectSummaryByCondition(
        condition: SpotSummaryCondition,
        resultType: KClass<T>
    ): List<T> {
        if (condition.spotIds.isEmpty() || condition.resellerStructIds.isEmpty()) return emptyList()

        val query = mutableListOf<String>().apply {
            add("SELECT")
            addAll(makeSelectCondition(resultType))
            add("  IFNULL(SUM(impression), 0) AS impression,")
            add("  IFNULL(SUM(click), 0) AS click,")
            add("  IFNULL(SUM(gross), 0) AS gross")
            add("FROM external_report_daily_summary")
            add("WHERE co_account_id = :coAccountId")
            add("  AND spot_id IN (:spotIds)")
            add("  AND struct_id IN (:resellerStructIds)")
            add("  AND (target_date >= :startDate AND target_date <= :endDate)")
            makeGroupByCondition(resultType)?.let { add(it) }
        }.joinToString("\n")

        return jdbcWrapper.query(query, CustomBeanPropertySqlParameterSource(condition), resultType)
    }

    private fun <T : RequestSummaryCo<T>> selectRequestSummaryByCondition(
        condition: SpotSummaryCondition,
        resultType: KClass<T>
    ): List<T> {
        if (condition.spotIds.isEmpty()) return emptyList()

        val query = mutableListOf<String>().apply {
            add("SELECT")
            addAll(makeSelectCondition(resultType))
            add("  IFNULL(SUM(impression), 0) AS request")
            add("FROM external_report_daily_summary")
            add("WHERE co_account_id = :coAccountId")
            add("  AND spot_id IN (:spotIds)")
            add("  AND (target_date >= :startDate AND target_date <= :endDate)")
            makeGroupByCondition(resultType)?.let { add(it) }
        }.joinToString("\n")

        return jdbcWrapper.query(query, CustomBeanPropertySqlParameterSource(condition), resultType)
    }
}
