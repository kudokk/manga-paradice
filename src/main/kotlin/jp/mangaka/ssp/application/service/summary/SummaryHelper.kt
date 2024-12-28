package jp.mangaka.ssp.application.service.summary

import jp.mangaka.ssp.application.service.coaccount.CoAccountGetWithCheckHelper
import jp.mangaka.ssp.application.service.country.CountryGetWithCheckHelper
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.CampaignStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignDao
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.datatime.DateTimeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relaystructspot.RelayStructSpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructDao
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.ExternalReportDailySummaryDao
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.SpotDailySummaryDao
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.condition.SpotSummaryCondition
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.SpotRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.TotalRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.SpotSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.TotalSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.merge
import jp.mangaka.ssp.presentation.common.summary.SummaryRequest
import jp.mangaka.ssp.presentation.common.summary.SummaryRequest.ListView.TermType
import jp.mangaka.ssp.util.TimeUtils
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Component
class SummaryHelper(
    private val campaignDao: CampaignDao,
    private val dateTimeDao: DateTimeDao,
    private val externalReportDailySummaryDao: ExternalReportDailySummaryDao,
    private val relayStructSpotDao: RelayStructSpotDao,
    private val spotDailySummaryDao: SpotDailySummaryDao,
    private val structDao: StructDao,
    private val coAccountGetWithCheckHelper: CoAccountGetWithCheckHelper,
    private val countryGetWithCheckHelper: CountryGetWithCheckHelper
) {
    /**
     * 広告枠の配信実績・リクエスト数の集計結果（総計）を取得する.
     *
     * @param coAccountId CoアカウントID
     * @param spotIds 広告枠IDリスト
     * @param summaryRequest 集計リクエストの内容
     * @return 広告枠の配信実績・リクエスト数の集計結果
     */
    fun getTotalSpotSummaries(
        coAccountId: CoAccountId,
        spotIds: Collection<SpotId>,
        summaryRequest: SummaryRequest.ListView
    ): Summaries<TotalSummaryCo, TotalRequestSummaryCo> {
        val condition = getSpotSummaryCondition(coAccountId, spotIds, summaryRequest)

        return if (summaryRequest.isExpectedRevenue) {
            // 想定レベニュー
            Summaries(getTotalRtbAndStructDeliveryResultSummaries(condition), getTotalRequestSummaries(condition))
        } else {
            // 実レベニュー
            Summaries(getTotalRtbDeliveryResultSummaries(condition), getTotalRequestSummaries(condition))
        }
    }

    /**
     * 広告枠の配信実績・リクエスト数の集計結果（広告枠ごと）を取得する.
     *
     * @param coAccountId CoアカウントID
     * @param spotIds 広告枠IDリスト
     * @param summaryRequest 集計リクエストの内容
     * @return 広告枠の配信実績・リクエスト数の集計結果のリスト
     */
    fun getSpotSummaries(
        coAccountId: CoAccountId,
        spotIds: Collection<SpotId>,
        summaryRequest: SummaryRequest
    ): Summaries<SpotSummaryCo, SpotRequestSummaryCo> {
        val condition = getSpotSummaryCondition(coAccountId, spotIds, summaryRequest)

        return if (summaryRequest.isExpectedRevenue) {
            // 想定レベニュー
            Summaries(getSpotRtbAndStructDeliveryResultSummaries(condition), getSpotRequestSummaries(condition))
        } else {
            // 実レベニュー
            Summaries(getSpotRtbDeliveryResultSummaries(condition), getSpotRequestSummaries(condition))
        }
    }

    /**
     * RTBの配信実績の集計結果(総計)を取得する.
     *
     * @param condition 集計条件
     * @return RTBの配信実績集計結果
     */
    @TestOnly
    fun getTotalRtbDeliveryResultSummaries(condition: SpotSummaryCondition): List<TotalSummaryCo> =
        listOf(
            spotDailySummaryDao.selectTotalRtbSpotSummaryByCondition(condition),
            externalReportDailySummaryDao.selectTotalSpotSummaryByCondition(condition)
        ).merge()

    /**
     * RTB/非RTBの配信実績の集計結果(総計)を取得する.
     *
     * @param condition 集計条件
     * @return RTBの配信実績集計結果
     */
    @TestOnly
    fun getTotalRtbAndStructDeliveryResultSummaries(condition: SpotSummaryCondition): List<TotalSummaryCo> =
        listOf(
            spotDailySummaryDao.selectTotalRtbAndStructSpotSummaryByCondition(condition),
            externalReportDailySummaryDao.selectTotalSpotSummaryByCondition(condition)
        ).merge()

    /**
     * リクエスト数の集計結果(総計)を取得する.
     *
     * @param condition 集計条件
     * @return RTBの配信実績集計結果
     */
    @TestOnly
    fun getTotalRequestSummaries(condition: SpotSummaryCondition): List<TotalRequestSummaryCo> =
        listOf(
            spotDailySummaryDao.selectTotalSpotRequestSummaryByCondition(condition),
            externalReportDailySummaryDao.selectTotalSpotRequestSummaryByCondition(condition)
        ).merge()

    /**
     * RTBの配信実績の集計結果(広告枠ごと)を取得する.
     *
     * @param condition 集計条件
     * @return RTBの配信実績集計結果のリスト
     */
    @TestOnly
    fun getSpotRtbDeliveryResultSummaries(condition: SpotSummaryCondition): List<SpotSummaryCo> =
        listOf(
            spotDailySummaryDao.selectSpotRtbSpotSummaryByCondition(condition),
            externalReportDailySummaryDao.selectSpotSummaryByCondition(condition)
        ).flatten().merge()

    /**
     * RTB/非RTBの配信実績の集計結果(広告枠ごと)を取得する.
     *
     * @param condition 集計条件
     * @return RTBの配信実績集計結果
     */
    @TestOnly
    fun getSpotRtbAndStructDeliveryResultSummaries(condition: SpotSummaryCondition): List<SpotSummaryCo> =
        listOf(
            spotDailySummaryDao.selectSpotRtbAndStructSpotSummaryByCondition(condition),
            externalReportDailySummaryDao.selectSpotSummaryByCondition(condition)
        ).flatten().merge()

    /**
     * リクエスト数の集計結果(広告枠ごと)を取得する.
     *
     * @param condition 集計条件
     * @return RTBの配信実績集計結果
     */
    @TestOnly
    fun getSpotRequestSummaries(condition: SpotSummaryCondition): List<SpotRequestSummaryCo> =
        listOf(
            spotDailySummaryDao.selectSpotRequestSummaryByCondition(condition),
            externalReportDailySummaryDao.selectSpotRequestSummaryByCondition(condition)
        ).flatten().merge()

    /**
     * 集計条件を生成する.
     *
     * @param coAccountId CoアカウントID
     * @param spotIds 広告枠IDリスト
     * @param summaryRequest 集計リクエストの内容
     * @return 生成した集計条件
     */
    @TestOnly
    fun getSpotSummaryCondition(
        coAccountId: CoAccountId,
        spotIds: Collection<SpotId>,
        summaryRequest: SummaryRequest
    ): SpotSummaryCondition {
        val campaigns = campaignDao.selectByCoAccountIdAndStatuses(coAccountId, CampaignStatus.entries)
        val structs = structDao.selectByCampaignIdsAndStatuses(campaigns.map { it.campaignId }, StructStatus.entries)
        val relaySpotStructs = relayStructSpotDao.selectBySpotIds(spotIds).let { relayStructSpots ->
            structDao.selectByIdsAndStatuses(relayStructSpots.map { it.structId }, StructStatus.entries)
        }
        val coAccount = coAccountGetWithCheckHelper.getCoAccountWithCheck(coAccountId)
        val country = countryGetWithCheckHelper.getCountryWithCheck(coAccount.countryId)
        val (startDate, endDate) = when (summaryRequest) {
            is SummaryRequest.ListView -> calcPeriod(summaryRequest, country)
            is SummaryRequest.Csv -> summaryRequest.startDate to summaryRequest.endDate
        }

        return SpotSummaryCondition(
            coAccountId,
            spotIds,
            structs.filter { it.isReseller() }.map { it.structId },
            structs.filterNot { it.isReseller() }.map { it.structId },
            relaySpotStructs.filter { it.isReseller() }.map { it.structId },
            startDate,
            endDate
        )
    }

    /**
     * 集計期間を算出する
     *
     * @param summaryRequest 集計リクエストの内容
     * @param country 国
     * @return 算出した期間の開始・終了日のペア
     */
    @TestOnly
    fun calcPeriod(summaryRequest: SummaryRequest.ListView, country: CountryMaster): Pair<LocalDate, LocalDate> {
        val currentDate = TimeUtils.fixedDate(dateTimeDao.selectCurrentDateTime(), country.timeDifference)

        return when (summaryRequest.termType) {
            TermType.today -> currentDate to currentDate
            TermType.yesterday -> currentDate.minusDays(1).let { it to it }
            TermType.thisWeek -> currentDate.with(DayOfWeek.MONDAY) to currentDate
            TermType.lastWeek -> currentDate.minusWeeks(1).let {
                it.with(DayOfWeek.MONDAY) to it.with(DayOfWeek.SUNDAY)
            }
            TermType.thisMonth -> currentDate.withDayOfMonth(1) to currentDate
            TermType.lastMonth -> currentDate.minusMonths(1).let {
                it.withDayOfMonth(1) to it.with(TemporalAdjusters.lastDayOfMonth())
            }
            TermType.last7days -> currentDate.minusDays(7) to currentDate.minusDays(1)
            TermType.last30days -> currentDate.minusDays(30) to currentDate.minusDays(1)
            // 期間指定の場合は設定があるはずなので強制キャスト
            TermType.custom -> summaryRequest.startDate!! to summaryRequest.endDate!!
        }
    }

    data class Summaries<T : SummaryCo<T>, R : RequestSummaryCo<R>>(
        val summaryCos: List<T>,
        val requestSummaryCos: List<R>
    )
}
