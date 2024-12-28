package jp.mangaka.ssp.application.service.summary

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jp.mangaka.ssp.application.service.coaccount.CoAccountGetWithCheckHelper
import jp.mangaka.ssp.application.service.country.CountryGetWithCheckHelper
import jp.mangaka.ssp.application.service.summary.SummaryHelper.Summaries
import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.CampaignStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignDao
import jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster.CoAccountMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.datatime.DateTimeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relaystructspot.RelayStructSpot
import jp.mangaka.ssp.infrastructure.datasource.dao.relaystructspot.RelayStructSpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructDao
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.ExternalReportDailySummaryDao
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.SpotDailySummaryDao
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.condition.SpotSummaryCondition
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.SpotRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.TotalRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.SpotSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.TotalSummaryCo
import jp.mangaka.ssp.presentation.common.summary.SummaryRequest
import jp.mangaka.ssp.presentation.common.summary.SummaryRequest.ListView.TermType
import jp.mangaka.ssp.util.TimeUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import io.mockk.verify as verifyK

@DisplayName("SummaryHelperのテスト")
private class SummaryHelperTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val countryId = CountryId(1)
    }

    val campaignDao: CampaignDao = mock()
    val dateTimeDao: DateTimeDao = mock()
    val externalReportDailySummaryDao: ExternalReportDailySummaryDao = mock()
    val relayStructSpotDao: RelayStructSpotDao = mock()
    val spotDailySummaryDao: SpotDailySummaryDao = mock()
    val structDao: StructDao = mock()
    val coAccountGetWithCheckHelper: CoAccountGetWithCheckHelper = mock()
    val countryGetWithCheckHelper: CountryGetWithCheckHelper = mock()

    val sut = spy(
        SummaryHelper(
            campaignDao, dateTimeDao, externalReportDailySummaryDao, relayStructSpotDao, spotDailySummaryDao,
            structDao, coAccountGetWithCheckHelper, countryGetWithCheckHelper
        )
    )

    @Nested
    @DisplayName("getTotalSpotSummariesのテスト")
    inner class GetTotalSpotSummariesTest {
        val spotIds: List<SpotId> = mock()
        val summaryRequest: SummaryRequest.ListView = mock()
        val condition: SpotSummaryCondition = mock()
        val deliveryResults: List<TotalSummaryCo> = mock()
        val requests: List<TotalRequestSummaryCo> = mock()
        val summaries = Summaries(deliveryResults, requests)

        @BeforeEach
        fun beforeEach() {
            doReturn(condition).whenever(sut).getSpotSummaryCondition(any(), any(), any())
            doReturn(deliveryResults).whenever(sut).getTotalRtbDeliveryResultSummaries(any())
            doReturn(deliveryResults).whenever(sut).getTotalRtbAndStructDeliveryResultSummaries(any())
            doReturn(requests).whenever(sut).getTotalRequestSummaries(any())
        }

        @Test
        @DisplayName("想定レベニュー")
        fun isExpectedRevenue() {
            doReturn(true).whenever(summaryRequest).isExpectedRevenue

            val actual = sut.getTotalSpotSummaries(coAccountId, spotIds, summaryRequest)

            assertEquals(summaries, actual)
            verify(sut, times(1)).getSpotSummaryCondition(coAccountId, spotIds, summaryRequest)
            verify(sut, times(1)).getTotalRtbAndStructDeliveryResultSummaries(condition)
            verify(sut, times(1)).getTotalRequestSummaries(condition)
        }

        @Test
        @DisplayName("実レベニュー")
        fun isActualRevenue() {
            doReturn(false).whenever(summaryRequest).isExpectedRevenue

            val actual = sut.getTotalSpotSummaries(coAccountId, spotIds, summaryRequest)

            assertEquals(summaries, actual)
            verify(sut, times(1)).getSpotSummaryCondition(coAccountId, spotIds, summaryRequest)
            verify(sut, times(1)).getTotalRtbDeliveryResultSummaries(condition)
            verify(sut, times(1)).getTotalRequestSummaries(condition)
        }
    }

    @Nested
    @DisplayName("getSpotSummariesのテスト")
    inner class GetSpotSummariesTest {
        val spotIds: List<SpotId> = mock()
        val summaryRequest: SummaryRequest.ListView = mock()
        val condition: SpotSummaryCondition = mock()
        val deliveryResults: List<TotalSummaryCo> = mock()
        val requests: List<TotalRequestSummaryCo> = mock()
        val summaries = Summaries(deliveryResults, requests)

        @BeforeEach
        fun beforeEach() {
            doReturn(condition).whenever(sut).getSpotSummaryCondition(any(), any(), any())
            doReturn(deliveryResults).whenever(sut).getSpotRtbDeliveryResultSummaries(any())
            doReturn(deliveryResults).whenever(sut).getSpotRtbAndStructDeliveryResultSummaries(any())
            doReturn(requests).whenever(sut).getSpotRequestSummaries(any())
        }

        @Test
        @DisplayName("想定レベニュー")
        fun isExpectedRevenue() {
            doReturn(true).whenever(summaryRequest).isExpectedRevenue

            val actual = sut.getSpotSummaries(coAccountId, spotIds, summaryRequest)

            assertEquals(summaries, actual)
            verify(sut, times(1)).getSpotSummaryCondition(coAccountId, spotIds, summaryRequest)
            verify(sut, times(1)).getSpotRtbAndStructDeliveryResultSummaries(condition)
            verify(sut, times(1)).getSpotRequestSummaries(condition)
        }

        @Test
        @DisplayName("実レベニュー")
        fun isActualRevenue() {
            doReturn(false).whenever(summaryRequest).isExpectedRevenue

            val actual = sut.getSpotSummaries(coAccountId, spotIds, summaryRequest)

            assertEquals(summaries, actual)
            verify(sut, times(1)).getSpotSummaryCondition(coAccountId, spotIds, summaryRequest)
            verify(sut, times(1)).getSpotRtbDeliveryResultSummaries(condition)
            verify(sut, times(1)).getSpotRequestSummaries(condition)
        }
    }

    @Nested
    @DisplayName("getTotalRtbDeliveryResultSummariesのテスト")
    inner class GetTotalRtbDeliveryResultSummariesTest {
        val condition: SpotSummaryCondition = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(TotalSummaryCo(100, 200, BigDecimal("300")))
                .whenever(spotDailySummaryDao)
                .selectTotalRtbSpotSummaryByCondition(any())
            doReturn(TotalSummaryCo(200, 300, BigDecimal("400")))
                .whenever(externalReportDailySummaryDao)
                .selectTotalSpotSummaryByCondition(any())

            val actual = sut.getTotalRtbDeliveryResultSummaries(condition)

            assertEquals(listOf(TotalSummaryCo(300, 500, BigDecimal("700"))), actual)
            verify(spotDailySummaryDao, times(1)).selectTotalRtbSpotSummaryByCondition(condition)
            verify(externalReportDailySummaryDao, times(1)).selectTotalSpotSummaryByCondition(condition)
        }
    }

    @Nested
    @DisplayName("getTotalRtbAndStructDeliveryResultSummariesのテスト")
    inner class GetTotalRtbAndStructDeliveryResultSummariesTest {
        val condition: SpotSummaryCondition = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(TotalSummaryCo(100, 200, BigDecimal("300")))
                .whenever(spotDailySummaryDao)
                .selectTotalRtbAndStructSpotSummaryByCondition(any())
            doReturn(TotalSummaryCo(200, 300, BigDecimal("400")))
                .whenever(externalReportDailySummaryDao)
                .selectTotalSpotSummaryByCondition(any())

            val actual = sut.getTotalRtbAndStructDeliveryResultSummaries(condition)

            assertEquals(listOf(TotalSummaryCo(300, 500, BigDecimal("700"))), actual)
            verify(spotDailySummaryDao, times(1)).selectTotalRtbAndStructSpotSummaryByCondition(condition)
            verify(externalReportDailySummaryDao, times(1)).selectTotalSpotSummaryByCondition(condition)
        }
    }

    @Nested
    @DisplayName("getTotalRequestSummariesのテスト")
    inner class GetTotalRequestSummariesTest {
        val condition: SpotSummaryCondition = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(TotalRequestSummaryCo(100))
                .whenever(spotDailySummaryDao)
                .selectTotalSpotRequestSummaryByCondition(any())
            doReturn(TotalRequestSummaryCo(200))
                .whenever(externalReportDailySummaryDao)
                .selectTotalSpotRequestSummaryByCondition(any())

            val actual = sut.getTotalRequestSummaries(condition)

            assertEquals(listOf(TotalRequestSummaryCo(300)), actual)
            verify(spotDailySummaryDao, times(1)).selectTotalSpotRequestSummaryByCondition(condition)
            verify(externalReportDailySummaryDao, times(1)).selectTotalSpotRequestSummaryByCondition(condition)
        }
    }

    @Nested
    @DisplayName("getSpotRtbDeliveryResultSummariesのテスト")
    inner class GetSpotRtbDeliveryResultSummariesTest {
        val condition: SpotSummaryCondition = mock()
        val spotDailies = listOf(
            spotSummaryCo(1, 100, 200, 300.0),
            spotSummaryCo(2, 200, 300, 400.0),
            spotSummaryCo(3, 300, 400, 500.0)
        )
        val externalReports = listOf(
            spotSummaryCo(1, 110, 210, 310.0),
            spotSummaryCo(2, 210, 310, 410.0),
            spotSummaryCo(4, 410, 510, 610.0)
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(spotDailies)
                .whenever(spotDailySummaryDao)
                .selectSpotRtbSpotSummaryByCondition(any())
            doReturn(externalReports)
                .whenever(externalReportDailySummaryDao)
                .selectSpotSummaryByCondition(any())

            val actual = sut.getSpotRtbDeliveryResultSummaries(condition)

            assertEquals(
                listOf(
                    spotSummaryCo(1, 210, 410, 610.0),
                    spotSummaryCo(2, 410, 610, 810.0),
                    spotSummaryCo(3, 300, 400, 500.0),
                    spotSummaryCo(4, 410, 510, 610.0)
                ),
                actual
            )
            verify(spotDailySummaryDao, times(1)).selectSpotRtbSpotSummaryByCondition(condition)
            verify(externalReportDailySummaryDao, times(1)).selectSpotSummaryByCondition(condition)
        }
    }

    @Nested
    @DisplayName("getSpotRtbAndStructDeliveryResultSummariesのテスト")
    inner class GetSpotRtbAndStructDeliveryResultSummariesTest {
        val condition: SpotSummaryCondition = mock()
        val spotDailies = listOf(
            spotSummaryCo(1, 100, 200, 300.0),
            spotSummaryCo(2, 200, 300, 400.0),
            spotSummaryCo(3, 300, 400, 500.0)
        )
        val externalReports = listOf(
            spotSummaryCo(1, 110, 210, 310.0),
            spotSummaryCo(2, 210, 310, 410.0),
            spotSummaryCo(4, 410, 510, 610.0)
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(spotDailies)
                .whenever(spotDailySummaryDao)
                .selectSpotRtbAndStructSpotSummaryByCondition(any())
            doReturn(externalReports)
                .whenever(externalReportDailySummaryDao)
                .selectSpotSummaryByCondition(any())

            val actual = sut.getSpotRtbAndStructDeliveryResultSummaries(condition)

            assertEquals(
                listOf(
                    spotSummaryCo(1, 210, 410, 610.0),
                    spotSummaryCo(2, 410, 610, 810.0),
                    spotSummaryCo(3, 300, 400, 500.0),
                    spotSummaryCo(4, 410, 510, 610.0)
                ),
                actual
            )
            verify(spotDailySummaryDao, times(1)).selectSpotRtbAndStructSpotSummaryByCondition(condition)
            verify(externalReportDailySummaryDao, times(1)).selectSpotSummaryByCondition(condition)
        }
    }

    @Nested
    @DisplayName("getSpotRequestSummariesのテスト")
    inner class GetSpotRequestSummariesTest {
        val condition: SpotSummaryCondition = mock()
        val spotDailies = listOf(
            spotRequestSummaryCo(1, 100),
            spotRequestSummaryCo(2, 200),
            spotRequestSummaryCo(3, 300)
        )
        val externalReports = listOf(
            spotRequestSummaryCo(1, 110),
            spotRequestSummaryCo(2, 210),
            spotRequestSummaryCo(4, 410)
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(spotDailies)
                .whenever(spotDailySummaryDao)
                .selectSpotRequestSummaryByCondition(any())
            doReturn(externalReports)
                .whenever(externalReportDailySummaryDao)
                .selectSpotRequestSummaryByCondition(any())

            val actual = sut.getSpotRequestSummaries(condition)

            assertEquals(
                listOf(
                    spotRequestSummaryCo(1, 210),
                    spotRequestSummaryCo(2, 410),
                    spotRequestSummaryCo(3, 300),
                    spotRequestSummaryCo(4, 410)
                ),
                actual
            )
            verify(spotDailySummaryDao, times(1)).selectSpotRequestSummaryByCondition(condition)
            verify(externalReportDailySummaryDao, times(1)).selectSpotRequestSummaryByCondition(condition)
        }
    }

    @Nested
    @DisplayName("getSpotSummaryConditionのテスト")
    inner class GetSpotSummaryConditionTest {
        val spotIds = listOf(1, 2, 3).map { SpotId(it) }
        val campaignIds = listOf(1, 2).map { CampaignId(it) }
        val campaigns = campaignIds.map { campaign(it) }
        val resellerStructIds = listOf(10, 11, 12).map { StructId(it) }
        val notResellerStructIds = listOf(20, 21, 22).map { StructId(it) }
        val coAccountStructs =
            resellerStructIds.map { struct(it, true) } + notResellerStructIds.map { struct(it, false) }
        val resellerRelaySpotStructIds = listOf(10, 11, 12, 13).map { StructId(it) }
        val notResellerRelaySpotStructIds = listOf(20, 21, 22, 23).map { StructId(it) }
        val relaySpotStructIds = resellerStructIds + notResellerRelaySpotStructIds
        val relayStructSpots = relaySpotStructIds.map { relayStructSpot(it) }
        val relaySpotStructs = resellerRelaySpotStructIds.map { struct(it, true) } +
            notResellerRelaySpotStructIds.map { struct(it, false) }
        val coAccount: CoAccountMaster = mock {
            on { this.countryId } doReturn countryId
        }
        val country: CountryMaster = mock()
        val startDate = LocalDate.of(2024, 1, 1)!!
        val endDate = LocalDate.of(2024, 1, 15)!!

        @BeforeEach
        fun beforeEach() {
            doReturn(campaigns).whenever(campaignDao).selectByCoAccountIdAndStatuses(any(), any())
            doReturn(coAccountStructs)
                .whenever(structDao)
                .selectByCampaignIdsAndStatuses(campaignIds, StructStatus.entries)
            doReturn(relayStructSpots).whenever(relayStructSpotDao).selectBySpotIds(spotIds)
            doReturn(relaySpotStructs)
                .whenever(structDao)
                .selectByIdsAndStatuses(relaySpotStructIds, StructStatus.entries)
            doReturn(coAccount).whenever(coAccountGetWithCheckHelper).getCoAccountWithCheck(any())
            doReturn(country).whenever(countryGetWithCheckHelper).getCountryWithCheck(any())
            doReturn(startDate to endDate).whenever(sut).calcPeriod(any(), any())
        }

        @Test
        @DisplayName("正常 - リクエストがListViewのとき")
        fun isCorrectAndListView() {
            val summaryRequest: SummaryRequest.ListView = mock()

            val actual = sut.getSpotSummaryCondition(coAccountId, spotIds, summaryRequest)

            assertEquals(
                SpotSummaryCondition(
                    coAccountId,
                    spotIds,
                    resellerStructIds,
                    notResellerStructIds,
                    resellerRelaySpotStructIds,
                    startDate,
                    endDate
                ),
                actual
            )
            verify(campaignDao, times(1)).selectByCoAccountIdAndStatuses(coAccountId, CampaignStatus.entries)
            verify(structDao, times(1)).selectByCampaignIdsAndStatuses(campaignIds, StructStatus.entries)
            verify(relayStructSpotDao, times(1)).selectBySpotIds(spotIds)
            verify(structDao, times(1)).selectByIdsAndStatuses(relaySpotStructIds, StructStatus.entries)
            verify(coAccountGetWithCheckHelper, times(1)).getCoAccountWithCheck(coAccountId)
            verify(countryGetWithCheckHelper, times(1)).getCountryWithCheck(countryId)
            verify(sut, times(1)).calcPeriod(summaryRequest, country)
        }

        @Test
        @DisplayName("正常 - リクエストがCSVのとき")
        fun isCorrectAndCsv() {
            val summaryRequest: SummaryRequest.Csv = mock {
                on { this.startDate } doReturn startDate
                on { this.endDate } doReturn endDate
            }

            val actual = sut.getSpotSummaryCondition(coAccountId, spotIds, summaryRequest)

            assertEquals(
                SpotSummaryCondition(
                    coAccountId,
                    spotIds,
                    resellerStructIds,
                    notResellerStructIds,
                    resellerRelaySpotStructIds,
                    startDate,
                    endDate
                ),
                actual
            )
            verify(campaignDao, times(1)).selectByCoAccountIdAndStatuses(coAccountId, CampaignStatus.entries)
            verify(structDao, times(1)).selectByCampaignIdsAndStatuses(campaignIds, StructStatus.entries)
            verify(relayStructSpotDao, times(1)).selectBySpotIds(spotIds)
            verify(structDao, times(1)).selectByIdsAndStatuses(relaySpotStructIds, StructStatus.entries)
            verify(coAccountGetWithCheckHelper, times(1)).getCoAccountWithCheck(coAccountId)
            verify(countryGetWithCheckHelper, times(1)).getCountryWithCheck(countryId)
            verify(sut, never()).calcPeriod(any(), any())
        }

        private fun campaign(campaignId: CampaignId): CampaignCo = mock {
            on { this.campaignId } doReturn campaignId
        }

        private fun struct(structId: StructId, isReseller: Boolean): StructCo = mock {
            on { this.structId } doReturn structId
            on { this.isReseller() } doReturn isReseller
        }

        private fun relayStructSpot(structId: StructId): RelayStructSpot = mock {
            on { this.structId } doReturn structId
        }
    }

    @Nested
    @DisplayName("calcPeriodのテスト")
    inner class CalcPeriodTest {
        val currentDateTime: LocalDateTime = mock()
        val today = LocalDate.of(2024, 10, 15)!!
        val country: CountryMaster = mock {
            on { this.timeDifference } doReturn BigDecimal(1)
        }

        @BeforeEach
        fun beforeEach() {
            doReturn(currentDateTime).whenever(dateTimeDao).selectCurrentDateTime()

            mockkObject(TimeUtils)
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("当日")
        fun isToday() {
            every { TimeUtils.fixedDate(any(), any()) } returns today

            val actual = sut.calcPeriod(summaryRequest(TermType.today), country)

            assertEquals(LocalDate.of(2024, 10, 15) to LocalDate.of(2024, 10, 15), actual)
            // 共通部分の確認なので以降はチェックしない
            verifyK { TimeUtils.fixedDate(currentDateTime, country.timeDifference) }
        }

        @Test
        @DisplayName("前日")
        fun isYesterday() {
            every { TimeUtils.fixedDate(any(), any()) } returns today

            val actual = sut.calcPeriod(summaryRequest(TermType.yesterday), country)

            assertEquals(LocalDate.of(2024, 10, 14) to LocalDate.of(2024, 10, 14), actual)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                // 当日が週頭
                "2024-10-14,2024-10-14,2024-10-14",
                // 当日が週半ば
                "2024-10-18,2024-10-14,2024-10-18",
                // 当日が週末
                "2024-10-20,2024-10-14,2024-10-20"
            ]
        )
        @DisplayName("今週")
        fun isThisWeek(today: LocalDate, startDate: LocalDate, endDate: LocalDate) {
            every { TimeUtils.fixedDate(any(), any()) } returns today

            val actual = sut.calcPeriod(summaryRequest(TermType.thisWeek), country)

            assertEquals(startDate to endDate, actual)
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                // 当日が週頭
                "2024-10-14",
                // 当日が週半ば
                "2024-10-18",
                // 当日が週末
                "2024-10-20"
            ]
        )
        @DisplayName("先週")
        fun isLastWeek(today: LocalDate) {
            every { TimeUtils.fixedDate(any(), any()) } returns today

            val actual = sut.calcPeriod(summaryRequest(TermType.lastWeek), country)

            assertEquals(LocalDate.of(2024, 10, 7) to LocalDate.of(2024, 10, 13), actual)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                // 当日が月頭
                "2024-10-01,2024-10-01,2024-10-01",
                // 当日が月半ば
                "2024-10-18,2024-10-01,2024-10-18",
                // 当日が月末
                "2024-10-31,2024-10-01,2024-10-31"
            ]
        )
        @DisplayName("当月")
        fun isThisMonth(today: LocalDate, startDate: LocalDate, endDate: LocalDate) {
            every { TimeUtils.fixedDate(any(), any()) } returns today

            val actual = sut.calcPeriod(summaryRequest(TermType.thisMonth), country)

            assertEquals(startDate to endDate, actual)
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                // 当日が月初
                "2024-10-01",
                // 当日が月半ば
                "2024-10-18",
                // 当日が月末
                "2024-10-31"
            ]
        )
        @DisplayName("前月")
        fun isLastMonth(today: LocalDate) {
            every { TimeUtils.fixedDate(any(), any()) } returns today

            val actual = sut.calcPeriod(summaryRequest(TermType.lastMonth), country)

            assertEquals(LocalDate.of(2024, 9, 1) to LocalDate.of(2024, 9, 30), actual)
        }

        @Test
        @DisplayName("過去7日間")
        fun isLast7days() {
            every { TimeUtils.fixedDate(any(), any()) } returns today

            val actual = sut.calcPeriod(summaryRequest(TermType.last7days), country)

            assertEquals(LocalDate.of(2024, 10, 8) to LocalDate.of(2024, 10, 14), actual)
        }

        @Test
        @DisplayName("過去30日間")
        fun isLast30days() {
            every { TimeUtils.fixedDate(any(), any()) } returns today

            val actual = sut.calcPeriod(summaryRequest(TermType.last30days), country)

            assertEquals(LocalDate.of(2024, 9, 15) to LocalDate.of(2024, 10, 14), actual)
        }

        @Test
        @DisplayName("期間指定")
        fun isCustom() {
            every { TimeUtils.fixedDate(any(), any()) } returns today

            val startDate = LocalDate.of(2023, 5, 20)
            val endDate = LocalDate.of(2024, 3, 15)
            val actual = sut.calcPeriod(summaryRequest(TermType.custom, startDate, endDate), country)

            assertEquals(startDate to endDate, actual)
        }

        private fun summaryRequest(termType: TermType, startDate: LocalDate? = null, endDate: LocalDate? = null) =
            SummaryRequest.ListView(true, true, termType, startDate, endDate)
    }

    private fun spotSummaryCo(
        spotId: Int,
        impression: Long,
        click: Long,
        gross: Double
    ) = SpotSummaryCo(SpotId(spotId), impression, click, gross.toBigDecimal())

    private fun spotRequestSummaryCo(spotId: Int, request: Long) = SpotRequestSummaryCo(SpotId(spotId), request)
}
