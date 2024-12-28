package jp.mangaka.ssp.application.service.targeting.time

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jp.mangaka.ssp.application.service.coaccount.CoAccountGetWithCheckHelper
import jp.mangaka.ssp.application.service.country.CountryGetWithCheckHelper
import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.CampaignStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignDao
import jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster.CoAccountMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructDao
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargetingDao
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriodDao
import jp.mangaka.ssp.presentation.controller.common.view.CountrySelectElementView
import jp.mangaka.ssp.presentation.controller.common.view.StructSelectElementView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.CountriesView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingDetailView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingListElementView
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import io.mockk.verify as verifyK

@DisplayName("TimeTargetingViewServiceImplのテスト")
private class TimeTargetingViewServiceImplTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val timeTargetingId = TimeTargetingId(2)
    }

    val campaignDao: CampaignDao = mock()
    val countryMasterDao: CountryMasterDao = mock()
    val structDao: StructDao = mock()
    val timeTargetingDao: TimeTargetingDao = mock()
    val timeTargetingDayTypePeriodDao: TimeTargetingDayTypePeriodDao = mock()
    val coAccountGetWithCheckHelper: CoAccountGetWithCheckHelper = mock()
    val countryGetWithCheckHelper: CountryGetWithCheckHelper = mock()
    val timeTargetingGetWithCheckHelper: TimeTargetingGetWithCheckHelper = mock()
    val timeTargetingsPageSize = 100

    val sut = spy(
        TimeTargetingViewServiceImpl(
            campaignDao,
            countryMasterDao,
            structDao,
            timeTargetingDao,
            timeTargetingDayTypePeriodDao,
            coAccountGetWithCheckHelper,
            countryGetWithCheckHelper,
            timeTargetingGetWithCheckHelper,
            timeTargetingsPageSize
        )
    )

    @Nested
    @DisplayName("getTimeTargetingViewsのテスト")
    inner class GetTimeTargetingViewsTest {
        val timeTargetingDayTypePeriods: List<TimeTargetingDayTypePeriod> = mock()
        val timeTargetingListElementViews: List<TimeTargetingListElementView> = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(TimeTargetingListElementView)
            every { TimeTargetingListElementView.of(any(), any()) } returns timeTargetingListElementViews

            doReturn(timeTargetingDayTypePeriods).whenever(timeTargetingDayTypePeriodDao).selectByIds(any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            // Setup
            val timeTargetingIds = listOf(1, 2, 3).map { TimeTargetingId(it) }
            val timeTargetings: List<TimeTargeting> = timeTargetingIds.map { id ->
                mock {
                    on { this.timeTargetingId } doReturn id
                }
            }
            doReturn(timeTargetings)
                .whenever(timeTargetingDao)
                .selectByCoAccountIdAndStatuses(any(), any(), any(), any())

            // Exercise
            val actual = sut.getTimeTargetingViews(coAccountId, 0)

            // Verify
            assertEquals(timeTargetingListElementViews, actual)
            verify(timeTargetingDao, times(1)).selectByCoAccountIdAndStatuses(
                coAccountId, TimeTargetingStatus.viewableStatuses, 100, 0
            )
            verify(timeTargetingDayTypePeriodDao, times(1)).selectByIds(timeTargetingIds)
            verifyK { TimeTargetingListElementView.of(timeTargetings, timeTargetingDayTypePeriods) }
        }
    }

    @Nested
    @DisplayName("getTimeTargetingViewのテスト")
    inner class GetTimeTargetingViewTest {
        val timeTargeting: TimeTargeting = mock {
            on { this.countryId } doReturn CountryId(1)
        }
        val timeTargetingDayTypePeriods: List<TimeTargetingDayTypePeriod> = mock()
        val country: CountryMaster = mock()
        val structs: List<StructCo> = listOf(1, 2, 3, 1, 2).map { campaignId ->
            mock {
                on { this.campaignId } doReturn CampaignId(campaignId)
            }
        }
        val campaigns: List<CampaignCo> = mock()
        val view: TimeTargetingDetailView = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(TimeTargetingDetailView)
            every { TimeTargetingDetailView.of(any(), any(), any(), any(), any()) } returns view

            doReturn(timeTargeting)
                .whenever(timeTargetingGetWithCheckHelper)
                .getTimeTargetingWithCheck(any(), any(), any())
            doReturn(timeTargetingDayTypePeriods).whenever(timeTargetingDayTypePeriodDao).selectById(any())
            doReturn(country).whenever(countryGetWithCheckHelper).getCountryWithCheck(any())
            doReturn(structs).whenever(structDao).selectByTimeTargetingIdAndStatuses(any(), any())
            doReturn(campaigns).whenever(campaignDao).selectByIdsAndStatuses(any(), any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getTimeTargetingView(coAccountId, timeTargetingId)

            assertEquals(view, actual)
            verify(timeTargetingGetWithCheckHelper, times(1))
                .getTimeTargetingWithCheck(coAccountId, timeTargetingId, TimeTargetingStatus.viewableStatuses)
            verify(timeTargetingDayTypePeriodDao, times(1)).selectById(timeTargetingId)
            verify(countryGetWithCheckHelper, times(1)).getCountryWithCheck(timeTargeting.countryId)
            verify(structDao, times(1))
                .selectByTimeTargetingIdAndStatuses(timeTargetingId, StructStatus.entries)
            verify(campaignDao, times(1))
                .selectByIdsAndStatuses(listOf(1, 2, 3).map { CampaignId(it) }, CampaignStatus.entries)
            verifyK {
                TimeTargetingDetailView.of(timeTargeting, timeTargetingDayTypePeriods, country, structs, campaigns)
            }
        }
    }

    @Nested
    @DisplayName("getCountriesViewのテスト")
    inner class GetCountriesViewTest {
        val coAccount: CoAccountMaster = mock {
            on { this.countryId } doReturn CountryId(1)
        }
        val countries = listOf(
            country(1, "ja1", "en1", "kr1", true),
            country(2, "ja2", "en2", "kr2", true),
            country(3, "ja3", "en3", "kr3", false),
            country(4, "ja4", "en4", "kr4", false),
            country(5, "ja5", "en5", "kr5", true)
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(coAccount).whenever(coAccountGetWithCheckHelper).getCoAccountWithCheck(any())
            doReturn(countries).whenever(countryMasterDao).selectAll()

            val actual = sut.getCountriesView(coAccountId)

            assertEquals(
                CountriesView(
                    coAccount.countryId,
                    listOf(
                        countryView(1, "ja1", "en1", "kr1"),
                        countryView(2, "ja2", "en2", "kr2"),
                        countryView(5, "ja5", "en5", "kr5")
                    )
                ),
                actual
            )
            verify(coAccountGetWithCheckHelper, times(1)).getCoAccountWithCheck(coAccountId)
            verify(countryMasterDao, times(1)).selectAll()
        }

        private fun country(
            countryId: Int,
            countryNameJa: String,
            countryNameEn: String,
            countryNameKr: String,
            isAvailableAtCompass: Boolean
        ): CountryMaster = mock {
            on { this.countryId } doReturn CountryId(countryId)
            on { this.name } doReturn countryNameJa
            on { this.nameEn } doReturn countryNameEn
            on { this.nameKr } doReturn countryNameKr
            on { this.isAvailableAtCompass() } doReturn isAvailableAtCompass
        }

        private fun countryView(countryId: Int, countryNameJa: String, countryNameEn: String, countryNameKr: String) =
            CountrySelectElementView(CountryId(countryId), countryNameJa, countryNameEn, countryNameKr)
    }

    @Nested
    @DisplayName("getStructViewsのテスト")
    inner class GetStructViewsTest {
        val campaigns: List<CampaignCo> = listOf(1, 2, 3).map { campaign(it) }
        val noRelateStructs = listOf(1, 2, 3).map { struct(it, null) }
        val relateStructs1 = listOf(4, 5).map { struct(it, timeTargetingId.value) }
        val relateStructs2 = listOf(6, 7).map { struct(it, 99) }
        val allStructs = noRelateStructs + relateStructs1 + relateStructs2
        val views: List<StructSelectElementView> = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(StructSelectElementView)
            every { StructSelectElementView.of(any(), any()) } returns views

            doReturn(campaigns).whenever(campaignDao).selectByCoAccountIdAndStatuses(any(), any())
            doReturn(allStructs).whenever(structDao).selectByCampaignIdsAndStatuses(any(), any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常 - タイムターゲティングID指定なし")
        fun isCorrectWithoutTimeTargetingId() {
            val actual = sut.getStructViews(coAccountId, null)

            assertEquals(views, actual)
            verify(campaignDao, times(1)).selectByCoAccountIdAndStatuses(coAccountId, CampaignStatus.viewableStatuses)
            verify(structDao, times(1))
                .selectByCampaignIdsAndStatuses(listOf(1, 2, 3).map { CampaignId(it) }, StructStatus.viewableStatuses)
            verifyK { StructSelectElementView.of(noRelateStructs, campaigns) }
        }

        @Test
        @DisplayName("正常 - タイムターゲティングID指定あり")
        fun isCorrectWithTimeTargetingId() {
            val actual = sut.getStructViews(coAccountId, timeTargetingId)

            assertEquals(views, actual)
            verify(campaignDao, times(1)).selectByCoAccountIdAndStatuses(coAccountId, CampaignStatus.viewableStatuses)
            verify(structDao, times(1))
                .selectByCampaignIdsAndStatuses(listOf(1, 2, 3).map { CampaignId(it) }, StructStatus.viewableStatuses)
            verifyK { StructSelectElementView.of(noRelateStructs + relateStructs1, campaigns) }
        }

        private fun campaign(campaignId: Int): CampaignCo = mock {
            on { this.campaignId } doReturn CampaignId(campaignId)
        }

        private fun struct(structId: Int, timeTargetingId: Int?): StructCo = mock {
            on { this.structId } doReturn StructId(structId)
            on { this.timeTargetingId } doReturn timeTargetingId?.let { TimeTargetingId(it) }
        }
    }
}
