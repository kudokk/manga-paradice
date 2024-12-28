package jp.mangaka.ssp.application.service.spot

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
import jp.mangaka.ssp.application.service.site.SiteGetWithCheckHelper
import jp.mangaka.ssp.application.service.spot.helper.SpotDetailViewHelper
import jp.mangaka.ssp.application.service.spot.helper.SpotGetWithCheckHelper
import jp.mangaka.ssp.application.service.spot.helper.SpotListViewHelper
import jp.mangaka.ssp.application.service.summary.SummaryHelper
import jp.mangaka.ssp.application.service.summary.SummaryHelper.Summaries
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.deal.DealId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeElementId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio.AspectRatioStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatioDao
import jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster.CoAccountMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster.CurrencyMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster.CurrencyMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.datatime.DateTimeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.decoration.Decoration
import jp.mangaka.ssp.infrastructure.datasource.dao.decoration.DecorationDao
import jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster.DspMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster.DspMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.fixedfloorcpm.FixedFloorCpm
import jp.mangaka.ssp.infrastructure.datasource.dao.fixedfloorcpm.FixedFloorCpmDao
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate.NativeTemplateStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplateDao
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElement
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElementDao
import jp.mangaka.ssp.infrastructure.datasource.dao.payment.Payment
import jp.mangaka.ssp.infrastructure.datasource.dao.payment.Payment.PaymentType
import jp.mangaka.ssp.infrastructure.datasource.dao.payment.PaymentDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp.RelayDefaultCoAccountDsp
import jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp.RelayDefaultCoAccountDspDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relayfixedfloorcpmspot.RelayFixedFloorCpmSpot
import jp.mangaka.ssp.infrastructure.datasource.dao.relayfixedfloorcpmspot.RelayFixedFloorCpmSpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.Reqgroup.ReqgroupStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.ReqgroupDao
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.ReqgroupDspCountryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.site.SiteDao
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.SpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.TotalRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.TotalSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.common.summary.SummaryRequest
import jp.mangaka.ssp.presentation.common.summary.SummaryView
import jp.mangaka.ssp.presentation.controller.spot.view.AspectRatioView
import jp.mangaka.ssp.presentation.controller.common.view.CountrySelectElementView
import jp.mangaka.ssp.presentation.controller.spot.view.CurrencyView
import jp.mangaka.ssp.presentation.controller.spot.view.DecorationView
import jp.mangaka.ssp.presentation.controller.spot.view.DspView
import jp.mangaka.ssp.presentation.controller.spot.view.FixedCpmView
import jp.mangaka.ssp.presentation.controller.spot.view.SiteView
import jp.mangaka.ssp.presentation.controller.spot.view.SizeTypeInfoView
import jp.mangaka.ssp.presentation.controller.spot.view.SpotReportCsvView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.SpotDetailView
import jp.mangaka.ssp.presentation.controller.spot.view.list.SpotListElementView
import jp.mangaka.ssp.presentation.controller.spot.view.nativedesign.NativeDesignPreviewView
import jp.mangaka.ssp.presentation.controller.spot.view.nativedesign.NativeDesignsView
import jp.mangaka.ssp.presentation.controller.spot.view.nativedesign.NativeDesignsView.NativeDesignView
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import jp.mangaka.ssp.util.TimeUtils
import jp.mangaka.ssp.util.localfile.LocalFileUtils
import jp.mangaka.ssp.util.localfile.valueobject.LocalFileType
import jp.mangaka.ssp.util.localfile.valueobject.config.CommonConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import io.mockk.verify as verifyK

@DisplayName("SpotViewServiceImplのテスト")
private class SpotViewServiceImplTest {
    companion object {
        val coAccountId1 = CoAccountId(1)
        val spotId1 = SpotId(1)
        val countryId1 = CountryId(1)
    }

    val aspectRatioDao: AspectRatioDao = mock()
    val countryMasterDao: CountryMasterDao = mock()
    val currencyMasterDao: CurrencyMasterDao = mock()
    val dateTimeDao: DateTimeDao = mock()
    val decorationDao: DecorationDao = mock()
    val dspMasterDao: DspMasterDao = mock()
    val fixedFloorCpmDao: FixedFloorCpmDao = mock()
    val nativeTemplateDao: NativeTemplateDao = mock()
    val nativeTemplateElementDao: NativeTemplateElementDao = mock()
    val paymentDao: PaymentDao = mock()
    val relayFixedFloorCpmSpotDao: RelayFixedFloorCpmSpotDao = mock()
    val relayDefaultCoAccountDspDao: RelayDefaultCoAccountDspDao = mock()
    val reqgroupDao: ReqgroupDao = mock()
    val siteDao: SiteDao = mock()
    val sizeTypeInfoDao: SizeTypeInfoDao = mock()
    val spotDao: SpotDao = mock()
    val coAccountGetWithCheckHelper: CoAccountGetWithCheckHelper = mock()
    val siteGetWithCheckHelper: SiteGetWithCheckHelper = mock()
    val spotDetailViewHelper: SpotDetailViewHelper = mock()
    val spotGetWithCheckHelper: SpotGetWithCheckHelper = mock()
    val spotListViewHelper: SpotListViewHelper = mock()
    val summaryHelper: SummaryHelper = mock()
    val localFileUtils: LocalFileUtils = mock()

    val sut = spy(
        SpotViewServiceImpl(
            aspectRatioDao, countryMasterDao, currencyMasterDao, dateTimeDao, decorationDao, dspMasterDao,
            fixedFloorCpmDao, nativeTemplateDao, nativeTemplateElementDao, paymentDao, relayDefaultCoAccountDspDao,
            relayFixedFloorCpmSpotDao, reqgroupDao, siteDao, spotDao, sizeTypeInfoDao, coAccountGetWithCheckHelper,
            siteGetWithCheckHelper, spotDetailViewHelper, spotGetWithCheckHelper, spotListViewHelper, summaryHelper,
            localFileUtils
        )
    )

    @AfterEach
    fun after() {
        unmockkAll()
    }

    @Nested
    @DisplayName("getSitesViewのテスト")
    inner class GetSitesViewTest {
        val entities: List<Site> = mock()
        val sitesView: List<SiteView> = mock()

        init {
            mockkObject(SiteView)
            every { SiteView.of(any()) } returns sitesView

            doReturn(entities).whenever(siteDao).selectByCoAccountIdAndStatuses(any(), any())
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getSitesView(coAccountId1)

            assertEquals(sitesView, actual)
            verify(siteDao, times(1)).selectByCoAccountIdAndStatuses(
                coAccountId1,
                listOf(SiteStatus.active, SiteStatus.requested, SiteStatus.ng)
            )
            verifyK { SiteView.of(entities) }
        }
    }

    @Nested
    @DisplayName("getCurrenciesViewのテスト")
    inner class GetCurrenciesViewTest {
        val entities: List<CurrencyMaster> = mock()
        val currenciesView: List<CurrencyView> = mock()

        init {
            mockkObject(CurrencyView)
            every { CurrencyView.of(any()) } returns currenciesView

            doReturn(entities).whenever(currencyMasterDao).selectAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getCurrenciesView()

            assertEquals(currenciesView, actual)
            verify(currencyMasterDao, times(1)).selectAll()
            verifyK { CurrencyView.of(entities) }
        }
    }

    @Nested
    @DisplayName("getSizeTypeInfosViewのテスト")
    inner class GetSizeTypeInfosViewTest {
        val bannerStandardEntities: List<SizeTypeInfo> = listOf(1, 2, 3).map {
            mockSizeTypeInfo(SizeTypeId(it))
        }
        val nativeStandardEntities = listOf(SizeTypeId.nativePc, SizeTypeId.nativeSp).map {
            mockSizeTypeInfo(it)
        }
        val userDefineds: List<SizeTypeInfo> = listOf(4, 5, 6).map {
            mockSizeTypeInfo(SizeTypeId(it))
        }
        val sizeTypeInfosView: List<SizeTypeInfoView> = mock()

        init {
            mockkObject(SizeTypeInfoView)
            every { SizeTypeInfoView.of(any()) } returns sizeTypeInfosView

            doReturn(bannerStandardEntities + nativeStandardEntities).whenever(sizeTypeInfoDao).selectStandards()
            doReturn(userDefineds).whenever(sizeTypeInfoDao).selectUserDefinedsByCoAccountId(any())
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getSizeTypeInfosView(coAccountId1)

            assertEquals(sizeTypeInfosView, actual)
            verify(sizeTypeInfoDao, times(1)).selectStandards()
            verify(sizeTypeInfoDao, times(1)).selectUserDefinedsByCoAccountId(coAccountId1)
            verifyK { SizeTypeInfoView.of(bannerStandardEntities + userDefineds) }
        }

        private fun mockSizeTypeInfo(sizeTypeId: SizeTypeId): SizeTypeInfo = mock {
            on { this.sizeTypeId } doReturn sizeTypeId
        }
    }

    @Nested
    @DisplayName("getDecorationsViewのテスト")
    inner class GetDecorationsViewTest {
        val entities: List<Decoration> = mock()
        val decorationsView: List<DecorationView> = mock()

        init {
            mockkObject(DecorationView)
            every { DecorationView.of(any<Collection<Decoration>>()) } returns decorationsView

            doReturn(entities).whenever(decorationDao).selectByCoAccountIds(any())
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getDecorationsView(coAccountId1)

            assertEquals(decorationsView, actual)
            verify(decorationDao, times(1)).selectByCoAccountIds(listOf(CoAccountId.zero, coAccountId1))
            verifyK { DecorationView.of(entities) }
        }
    }

    @Nested
    @DisplayName("getCountriesViewのテスト")
    inner class GetCountriesViewTest {
        val entities: List<CountryMaster> = mock()
        val countriesView: List<CountrySelectElementView> = mock()

        init {
            mockkObject(CountrySelectElementView)
            every { CountrySelectElementView.of(any<List<CountryMaster>>()) } returns countriesView

            doReturn(entities).whenever(countryMasterDao).selectAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getCountriesView()

            assertEquals(countriesView, actual)
            verify(countryMasterDao, times(1)).selectAll()
            verifyK { CountrySelectElementView.of(entities) }
        }
    }

    @Nested
    @DisplayName("getDspsViewのテスト")
    inner class GetDspsViewTest {
        val dspIds = listOf(1, 2, 3).map { DspId(it) }
        val dspCountryIds: Map<DspId, List<CountryId>> = mock()
        val dsps = dspIds.map { dspMaster(it) }
        val relayDefaultCoAccountDsps = listOf(
            RelayDefaultCoAccountDsp(coAccountId1, dspIds[0], 1.toBigDecimal(), 1),
            RelayDefaultCoAccountDsp(coAccountId1, dspIds[1], 2.toBigDecimal(), 2)
        )
        val defaultDsps = mapOf(
            dspIds[0] to RelayDefaultCoAccountDsp(coAccountId1, dspIds[0], 1.toBigDecimal(), 1),
            dspIds[1] to RelayDefaultCoAccountDsp(coAccountId1, dspIds[1], 2.toBigDecimal(), 2)
        )
        val dspsView: List<DspView> = mock()

        init {
            mockkObject(DspView)
            every { DspView.of(any(), any(), any()) } returns dspsView

            doReturn(dsps).whenever(dspMasterDao).selectAll()
            doReturn(dspCountryIds).whenever(sut).getDspCountryIds(any())
            doReturn(relayDefaultCoAccountDsps).whenever(relayDefaultCoAccountDspDao).selectByCoAccountId(any())
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getDspsView(coAccountId1)

            assertEquals(dspsView, actual)
            verify(dspMasterDao, times(1)).selectAll()
            verify(sut, times(1)).getDspCountryIds(dspIds)
            verify(relayDefaultCoAccountDspDao, times(1)).selectByCoAccountId(coAccountId1)
            verifyK { DspView.of(dsps, dspCountryIds, defaultDsps) }
        }

        private fun dspMaster(dspId: DspId): DspMaster = mock {
            on { this.dspId } doReturn dspId
        }

        private fun relayDefaultCoAccountDsp(dspId: DspId): RelayDefaultCoAccountDsp = mock {
            on { this.dspId } doReturn dspId
        }
    }

    @Nested
    @DisplayName("getDspCountryIdsのテスト")
    inner class GetDspCountryIdsTest {
        val dspIds = listOf(1, 2, 3, 4).map { DspId(it) }
        val countryIds = listOf(1, 2, 3).map { CountryId(it) }
        val entities = listOf(
            reqgroupDspCountryCo(dspIds[0], countryIds[0]),
            reqgroupDspCountryCo(dspIds[1], countryIds[0]),
            reqgroupDspCountryCo(dspIds[1], countryIds[1]),
            reqgroupDspCountryCo(dspIds[1], countryIds[2]),
            reqgroupDspCountryCo(dspIds[2], CountryId.zero),
            reqgroupDspCountryCo(dspIds[2], countryIds[0]),
            reqgroupDspCountryCo(dspIds[2], countryIds[1])
        )

        init {
            doReturn(entities).whenever(reqgroupDao).selectReqgroupDspCountryCosByDspIdsAndStatuses(any(), any())
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getDspCountryIds(dspIds)

            assertEquals(
                mapOf(
                    dspIds[0] to listOf(countryIds[0]),
                    dspIds[1] to listOf(countryIds[0], countryIds[1], countryIds[2]),
                    dspIds[2] to listOf(CountryId.zero),
                    dspIds[3] to listOf(CountryId.zero)
                ),
                actual
            )
            verify(reqgroupDao, times(1)).selectReqgroupDspCountryCosByDspIdsAndStatuses(
                dspIds, listOf(ReqgroupStatus.active)
            )
        }

        private fun reqgroupDspCountryCo(dspId: DspId, countryId: CountryId) = ReqgroupDspCountryCo(
            mock(), dspId, countryId
        )
    }

    @Nested
    @DisplayName("getNativeDesignsViewのテスト")
    inner class GetNativeDesignsViewTest {
        val commonTemplates = listOf(
            mockNativeTemplate(10, null, "共通10", 1),
            mockNativeTemplate(11, null, "共通11", 1),
            mockNativeTemplate(12, null, "共通12", 2),
            mockNativeTemplate(13, null, "共通13", 2),
        )
        val personalTemplates = listOf(
            mockNativeTemplate(20, 1, "ユーザー定義20", 1),
            mockNativeTemplate(21, 1, "ユーザー定義21", 1),
            mockNativeTemplate(22, 1, "ユーザー定義22", 2),
            mockNativeTemplate(23, 1, "ユーザー定義23", 2),
        )
        val videoTemplateIds = listOf(11, 12, 20, 23).map { NativeTemplateId(it) }
        val nativeTemplateStatuses = listOf(NativeTemplateStatus.active)

        init {
            doReturn(commonTemplates).whenever(nativeTemplateDao).selectCommonsByStatuses(any())
            doReturn(personalTemplates)
                .whenever(nativeTemplateDao)
                .selectPersonalsByCoAccountIdAndStatuses(any(), any())
            doReturn(videoTemplateIds).whenever(sut).getVideoNativeTemplateIds(any())
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getNativeDesignsView(coAccountId1)

            assertEquals(
                NativeDesignsView(
                    listOf(
                        nativeDesignView(21, "ユーザー定義21", 1),
                        nativeDesignView(22, "ユーザー定義22", 2),
                    ),
                    listOf(
                        nativeDesignView(11, "共通11", 1),
                        nativeDesignView(12, "共通12", 2),
                    )
                ),
                actual
            )
            verify(nativeTemplateDao, times(1)).selectCommonsByStatuses(nativeTemplateStatuses)
            verify(nativeTemplateDao, times(1)).selectPersonalsByCoAccountIdAndStatuses(
                coAccountId1, nativeTemplateStatuses
            )
            verify(sut, times(1)).getVideoNativeTemplateIds(
                listOf(10, 11, 12, 13, 20, 21, 22, 23).map { NativeTemplateId(it) }
            )
        }

        private fun mockNativeTemplate(
            nativeTemplateId: Int, coAccountId: Int?, nativeTemplateName: String, platformId: Int
        ): NativeTemplate = mock {
            on { this.nativeTemplateId } doReturn NativeTemplateId(nativeTemplateId)
            on { this.coAccountId } doReturn coAccountId?.let { CoAccountId(it) }
            on { this.nativeTemplateName } doReturn nativeTemplateName
            on { this.platformId } doReturn PlatformId(platformId)
        }

        private fun nativeDesignView(
            nativeTemplateId: Int, nativeTemplateName: String, platformId: Int
        ) = NativeDesignView(NativeTemplateId(nativeTemplateId), nativeTemplateName, PlatformId(platformId))
    }

    @Nested
    @DisplayName("getNativeVideoTemplateIdsのテスト")
    inner class GetNativeVideoTemplateIdsTest {
        val nativeTemplateElements = listOf(
            // 通常デザイン
            listOf(1).map { mockNativeTemplateElement(1, it) },
            listOf(1, 2).map { mockNativeTemplateElement(2, it) },
            listOf(1, 2, 3).map { mockNativeTemplateElement(3, it) },
            listOf(NativeElementId.video.value).map { mockNativeTemplateElement(4, it) },
            listOf(1, 2, NativeElementId.video.value).map { mockNativeTemplateElement(5, it) },
            listOf(1, 2, 3, NativeElementId.video.value).map { mockNativeTemplateElement(6, it) },
        ).flatten()

        init {
            doReturn(nativeTemplateElements).whenever(nativeTemplateElementDao).selectByNativeTemplateIds(any())
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val nativeTemplateIds = listOf(1, 2, 3, 4, 5, 6).map { NativeTemplateId(it) }

            val actual = sut.getVideoNativeTemplateIds(nativeTemplateIds)

            assertEquals(listOf(4, 5, 6).map { NativeTemplateId(it) }, actual)
            verify(nativeTemplateElementDao, times(1)).selectByNativeTemplateIds(nativeTemplateIds)
        }

        fun mockNativeTemplateElement(
            nativeTemplateId: Int, nativeElementId: Int
        ): NativeTemplateElement = mock {
            on { this.nativeTemplateId } doReturn NativeTemplateId(nativeTemplateId)
            on { this.nativeElementId } doReturn NativeElementId(nativeElementId)
        }
    }

    @Nested
    @DisplayName("getNativeDesignPreviewViewのテスト")
    inner class GetNativeDesignPreviewViewTest {
        val nativeTemplateId = NativeTemplateId(1)
        val nativeTemplate: NativeTemplate = mock()
        val nativeTemplateElements: List<NativeTemplateElement> = mock()
        val nativeDesignPreviewView: NativeDesignPreviewView = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(NativeDesignPreviewView)
            every { NativeDesignPreviewView.of(any(), any()) } returns nativeDesignPreviewView

            doReturn(nativeTemplate).whenever(spotGetWithCheckHelper).getNativeTemplateWithCheck(any(), any(), any())
            doReturn(nativeTemplateElements).whenever(nativeTemplateElementDao).selectByNativeTemplateIds(any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getNativeDesignPreviewView(coAccountId1, nativeTemplateId)

            assertEquals(nativeDesignPreviewView, actual)

            verify(spotGetWithCheckHelper, times(1)).getNativeTemplateWithCheck(
                coAccountId1, nativeTemplateId, listOf(NativeTemplateStatus.active)
            )
            verify(nativeTemplateElementDao, times(1)).selectByNativeTemplateIds(listOf(nativeTemplateId))
            verifyK { NativeDesignPreviewView.of(nativeTemplate, nativeTemplateElements) }
        }
    }

    @Nested
    @DisplayName("getAspectRatiosViewのテスト")
    inner class GetAspectRatiosViewTest {
        val entities: List<AspectRatio> = mock()
        val views: List<AspectRatioView> = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(AspectRatioView)

            every { AspectRatioView.of(any()) } returns views
            doReturn(entities).whenever(aspectRatioDao).selectByStatuses(any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getAspectRatiosView()

            assertEquals(views, actual)

            verify(aspectRatioDao, times(1)).selectByStatuses(listOf(AspectRatioStatus.active))
            verifyK { AspectRatioView.of(entities) }
        }
    }

    @Nested
    @DisplayName("getDetailのテスト")
    inner class GetDetailTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val expected: SpotDetailView = mock()
            doReturn(expected).whenever(spotDetailViewHelper).getSpotDetail(any(), any(), any())

            val actual = sut.getSpotDetail(coAccountId1, spotId1, UserType.ma_staff)

            assertEquals(expected, actual)
            verify(spotDetailViewHelper, times(1)).getSpotDetail(coAccountId1, spotId1, UserType.ma_staff)
        }
    }

    @Nested
    @DisplayName("getSpotsのテスト")
    inner class GetSpotsTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val summaryRequest: SummaryRequest.ListView = mock()
            val expected: List<SpotListElementView> = mock()
            doReturn(expected).whenever(spotListViewHelper).getSpotListViews(any(), any(), any())

            val actual = sut.getSpots(coAccountId1, summaryRequest, 10)

            assertEquals(expected, actual)
            verify(spotListViewHelper, times(1)).getSpotListViews(coAccountId1, summaryRequest, 10)
        }
    }

    @Nested
    @DisplayName("getFixedCpmのテスト")
    inner class GetFixedCpmTest {
        val spot: Spot = mock()
        val coAccount: CoAccountMaster = mock {
            on { this.countryId } doReturn countryId1
        }
        val timeDifference: BigDecimal = BigDecimal.ONE
        val country: CountryMaster = mock {
            on { this.timeDifference } doReturn timeDifference
        }
        val currentDateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0)
        val currentDate = LocalDate.of(2024, 1, 1)
        val nonFixedCpmPayments = listOf(PaymentType.fixed_cpm, PaymentType.revenue_share).map { mockPayment(it) }
        val fixedCpmPayments = List(3) { mockPayment(PaymentType.fixed_cpm_all) }
        val dealIds = listOf(1, 2, 3).map { DealId(it) }
        val relayFixedFloorCpmSpots = dealIds.map { mockRelayFixedFloorCpmSpot(it) }
        val pastFixedFloorCpms = listOf(currentDate.minusDays(1), currentDate.minusDays(2)).map {
            mockFixedFloorCpm(it)
        }
        val futureFixedFloorCpms = listOf(null, currentDate, currentDate.plusDays(1), currentDate.plusDays(2)).map {
            mockFixedFloorCpm(it)
        }
        val fixedCpmViews: List<FixedCpmView> = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(TimeUtils, FixedCpmView)
            every { TimeUtils.fixedDate(any(), any()) } returns currentDate
            every { FixedCpmView.of(any(), any(), any()) } returns fixedCpmViews

            doReturn(spot).whenever(spotGetWithCheckHelper).getSpotWithCheck(any(), any())
            doReturn(coAccount).whenever(coAccountGetWithCheckHelper).getCoAccountWithCheck(any())
            doReturn(country).whenever(spotGetWithCheckHelper).getCountryWithCheck(any())
            doReturn(currentDateTime).whenever(dateTimeDao).selectCurrentDateTime()
            doReturn(relayFixedFloorCpmSpots).whenever(relayFixedFloorCpmSpotDao).selectBySpotId(any())
            doReturn(pastFixedFloorCpms + futureFixedFloorCpms).whenever(fixedFloorCpmDao).selectByIds(any())
        }

        @Test
        @DisplayName("固定単価支払いがないとき")
        fun isNotFixedCpm() {
            doReturn(nonFixedCpmPayments).whenever(paymentDao).selectBySpotId(spotId1)

            val actual = sut.getFixedCpms(coAccountId1, spotId1)

            assertEmpty(actual)
            verify(spotGetWithCheckHelper, times(1)).getSpotWithCheck(spotId1, SpotStatus.entries)
            verify(coAccountGetWithCheckHelper, times(1)).getCoAccountWithCheck(coAccountId1)
            verify(spotGetWithCheckHelper, times(1)).getCountryWithCheck(countryId1)
            verify(paymentDao, times(1)).selectBySpotId(spotId1)
        }

        @DisplayName("固定単価支払いがあるとき")
        fun isFixedCpm() {
            doReturn(nonFixedCpmPayments + fixedCpmPayments).whenever(paymentDao).selectBySpotId(spotId1)

            val actual = sut.getFixedCpms(coAccountId1, spotId1)

            assertEquals(fixedCpmViews, actual)
            verify(spotGetWithCheckHelper, times(1)).getSpotWithCheck(spotId1, SpotStatus.entries)
            verify(coAccountGetWithCheckHelper, times(1)).getCoAccountWithCheck(coAccountId1)
            verify(spotGetWithCheckHelper, times(1)).getCountryWithCheck(countryId1)
            verify(paymentDao, times(1)).selectBySpotId(spotId1)
            verify(dateTimeDao, times(1)).selectCurrentDateTime()
            verifyK { TimeUtils.fixedDate(currentDateTime, timeDifference) }
            verify(relayFixedFloorCpmSpotDao, times(1)).selectBySpotId(spotId1)
            verify(fixedFloorCpmDao, times(1)).selectByIds(dealIds)
            verifyK { FixedCpmView.of(spot, fixedCpmPayments, futureFixedFloorCpms) }
        }

        private fun mockPayment(paymentType: PaymentType): Payment = mock {
            on { this.paymentType } doReturn paymentType
        }

        private fun mockRelayFixedFloorCpmSpot(dealId: DealId): RelayFixedFloorCpmSpot = mock {
            on { this.dealId } doReturn dealId
        }

        private fun mockFixedFloorCpm(endDate: LocalDate?): FixedFloorCpm = mock {
            on { this.endDate } doReturn endDate
        }
    }

    @Nested
    @DisplayName("getCoAccountSpotsTotalSummaryViewのテスト")
    inner class GetCoAccountSpotsTotalSummaryViewTest {
        val spotIds = listOf(1, 2, 3).map { SpotId(it) }
        val summaryRequest: SummaryRequest.ListView = mock {
            on { this.isTaxIncluded } doReturn true
        }
        val siteIds = listOf(10, 11, 12).map { SiteId(it) }
        val sites: List<Site> = siteIds.map { siteId ->
            mock {
                on { this.siteId } doReturn siteId
            }
        }
        val spots: List<Spot> = spotIds.map { spotId ->
            mock {
                on { this.spotId } doReturn spotId
            }
        }
        val deliveryResult = TotalSummaryCo(100, 200, BigDecimal("300.00000000"))
        val request = TotalRequestSummaryCo(400)
        val summaries = Summaries(listOf(deliveryResult), listOf(request))
        val commonConfig: CommonConfig = mock {
            on { this.taxRate } doReturn 1.1.toBigDecimal()
        }

        @BeforeEach
        fun beforeEach() {
            doReturn(sites).whenever(siteDao).selectByCoAccountIdAndStatuses(any(), any())
            doReturn(spots).whenever(spotDao).selectBySiteIdsAndStatuses(any(), any(), any(), any())
            doReturn(summaries).whenever(summaryHelper).getTotalSpotSummaries(any(), any(), any())
            doReturn(commonConfig).whenever(localFileUtils).loadConfig(LocalFileType.CommonConfig)
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getCoAccountSpotsTotalSummaryView(coAccountId1, summaryRequest)

            assertEquals(SummaryView.of(deliveryResult, request, true, commonConfig.taxRate), actual)
            verify(siteDao, times(1)).selectByCoAccountIdAndStatuses(coAccountId1, SiteStatus.nonArchiveStatuses)
            verify(spotDao, times(1))
                .selectBySiteIdsAndStatuses(siteIds, SpotStatus.viewableStatuses, Int.MAX_VALUE, 0)
            verify(summaryHelper, times(1)).getTotalSpotSummaries(coAccountId1, spotIds, summaryRequest)
            verify(localFileUtils, times(1)).loadConfig(LocalFileType.CommonConfig)
        }
    }

    @Nested
    @DisplayName("getSpotTotalSummaryViewのテスト")
    inner class GetSpotTotalSummaryViewTest {
        val spotId = SpotId(1)
        val siteId = SiteId(2)
        val spot: Spot = mock {
            on { this.spotId } doReturn spotId
            on { this.siteId } doReturn siteId
        }
        val summaryRequest: SummaryRequest.ListView = mock {
            on { this.isTaxIncluded } doReturn true
        }
        val deliveryResult = TotalSummaryCo(100, 200, BigDecimal("300.00000000"))
        val request = TotalRequestSummaryCo(400)
        val summaries = Summaries(listOf(deliveryResult), listOf(request))
        val commonConfig: CommonConfig = mock {
            on { this.taxRate } doReturn 1.1.toBigDecimal()
        }

        @BeforeEach
        fun beforeEach() {
            doReturn(spot).whenever(spotGetWithCheckHelper).getSpotWithCheck(any(), any())
            doReturn(mock<Site>()).whenever(siteGetWithCheckHelper).getSiteWithCheck(any(), any(), any())
            doReturn(summaries).whenever(summaryHelper).getTotalSpotSummaries(any(), any(), any())
            doReturn(commonConfig).whenever(localFileUtils).loadConfig(LocalFileType.CommonConfig)
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getSpotTotalSummaryView(coAccountId1, spotId, summaryRequest)

            assertEquals(SummaryView.of(deliveryResult, request, true, commonConfig.taxRate), actual)
            verify(spotGetWithCheckHelper, times(1)).getSpotWithCheck(spotId, SpotStatus.viewableStatuses)
            verify(siteGetWithCheckHelper, times(1))
                .getSiteWithCheck(coAccountId1, siteId, SiteStatus.nonArchiveStatuses)
            verify(summaryHelper, times(1)).getTotalSpotSummaries(coAccountId1, listOf(spotId), summaryRequest)
            verify(localFileUtils, times(1)).loadConfig(LocalFileType.CommonConfig)
        }
    }

    @Nested
    @DisplayName("getSpotReportCsvViewsのテスト")
    inner class GetSpotReportCsvViewsTest {
        val summaryRequest: SummaryRequest.Csv = mock()
        val views: List<SpotReportCsvView> = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(views).whenever(spotListViewHelper).getSpotReportCsvViews(any(), any())

            val actual = sut.getSpotReportCsvViews(coAccountId1, summaryRequest)

            assertEquals(views, actual)
            verify(spotListViewHelper, times(1)).getSpotReportCsvViews(coAccountId1, summaryRequest)
        }
    }
}
