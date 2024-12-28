package jp.mangaka.ssp.application.service.spot.helper

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
import jp.mangaka.ssp.presentation.controller.spot.view.SpotReportCsvView
import jp.mangaka.ssp.application.service.spot.util.CryptUtils
import jp.mangaka.ssp.application.service.summary.SummaryHelper
import jp.mangaka.ssp.application.service.summary.SummaryHelper.Summaries
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetype
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetypeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.site.SiteDao
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.SpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.SpotRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.SpotSummaryCo
import jp.mangaka.ssp.presentation.common.summary.SummaryRequest
import jp.mangaka.ssp.presentation.common.summary.SummaryView
import jp.mangaka.ssp.presentation.controller.spot.view.DeliveryFormatsView
import jp.mangaka.ssp.presentation.controller.spot.view.SiteView
import jp.mangaka.ssp.presentation.controller.spot.view.SizeTypeInfoView
import jp.mangaka.ssp.presentation.controller.spot.view.list.SpotListElementView
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import jp.mangaka.ssp.util.localfile.LocalFileUtils
import jp.mangaka.ssp.util.localfile.valueobject.LocalFileType
import jp.mangaka.ssp.util.localfile.valueobject.config.CommonConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import io.mockk.verify as verifyK

@DisplayName("SpotListViewHelperのテスト")
private class SpotListViewHelperTest {
    companion object {
        val coAccountId = CoAccountId(1)
    }

    val relaySpotSizetypeDao: RelaySpotSizetypeDao = mock()
    val siteDao: SiteDao = mock()
    val sizeTypeInfoDao: SizeTypeInfoDao = mock()
    val spotDao: SpotDao = mock()
    val spotBannerDisplayDao: SpotBannerDisplayDao = mock()
    val spotNativeDisplayDao: SpotNativeDisplayDao = mock()
    val spotNativeVideoDisplayDao: SpotNativeVideoDisplayDao = mock()
    val spotVideoDisplayDao: SpotVideoDisplayDao = mock()
    val summaryHelper: SummaryHelper = mock()
    val cryptUtils: CryptUtils = mock()
    val localFileUtils: LocalFileUtils = mock()
    val pageSize: Int = 50

    val sut = spy(
        SpotListViewHelper(
            relaySpotSizetypeDao,
            siteDao,
            sizeTypeInfoDao,
            spotDao,
            spotBannerDisplayDao,
            spotNativeDisplayDao,
            spotNativeVideoDisplayDao,
            spotVideoDisplayDao,
            summaryHelper,
            cryptUtils,
            localFileUtils,
            pageSize
        )
    )

    @Nested
    @DisplayName("getSpotListViewsのテスト")
    inner class GetSpotListViewsTest {
        val summaryRequest: SummaryRequest.ListView = mock()
        val siteIds = listOf(1, 2, 3).map { SiteId(it) }
        val spotIds = listOf(4, 5, 6).map { SpotId(it) }
        val sizeTypeIds = listOf(7, 8, 9).map { SizeTypeId(it) }
        val siteViewMap: Map<SiteId, SiteView> = siteIds.associateWith { mock() }
        val spots: List<Spot> = spotIds.map { id ->
            mock {
                on { this.spotId } doReturn id
            }
        }
        val spotSizeTypesMap: Map<SpotId, List<SizeTypeId>> = spotIds.associateWith { sizeTypeIds }
        val sizeTypeInfoMap: Map<SizeTypeId, SizeTypeInfoView> = sizeTypeIds.associateWith { mock() }
        val deliveryFormatsViewMap: Map<SpotId, DeliveryFormatsView> = mock()
        val summaryViewMap: Map<SpotId, SummaryView> = mock()
        val spotViews: List<SpotListElementView> = listOf(mock())

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotListElementView)
            every { SpotListElementView.of(any(), any(), any(), any(), any(), any()) } returns spotViews

            doReturn(siteViewMap).whenever(sut).getSiteViewMap(any())
            doReturn(spotSizeTypesMap).whenever(sut).getSpotSizeTypesMap(any())
            doReturn(sizeTypeInfoMap).whenever(sut).getSizeTypeInfoViewMap(any())
            doReturn(deliveryFormatsViewMap).whenever(sut).getDeliveryFormatsViewMap(any())
            doReturn(summaryViewMap).whenever(sut).getSummaryViewMap(any(), any(), any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("取得データあり")
        fun isFound() {
            doReturn(spots).whenever(spotDao).selectBySiteIdsAndStatuses(any(), any(), any(), any())

            val actual = sut.getSpotListViews(coAccountId, summaryRequest, 10)

            assertEquals(spotViews, actual)
            verify(sut, times(1)).getSiteViewMap(coAccountId)
            verify(spotDao, times(1)).selectBySiteIdsAndStatuses(siteIds.toSet(), SpotStatus.viewableStatuses, 50, 500)
            verify(sut, times(1)).getSpotSizeTypesMap(spotIds)
            verify(sut, times(1)).getSizeTypeInfoViewMap(sizeTypeIds)
            verify(sut, times(1)).getDeliveryFormatsViewMap(spotIds)
            verify(sut, times(1)).getSummaryViewMap(coAccountId, spotIds, summaryRequest)
            verifyK {
                SpotListElementView.of(
                    spots,
                    spotSizeTypesMap,
                    siteViewMap,
                    sizeTypeInfoMap,
                    deliveryFormatsViewMap,
                    summaryViewMap
                )
            }
        }

        @Test
        @DisplayName("対象データなし")
        fun isEmptyResult() {
            doReturn(emptyList<Spot>()).whenever(spotDao).selectBySiteIdsAndStatuses(any(), any(), any(), any())

            val actual = sut.getSpotListViews(coAccountId, summaryRequest, 50)

            assertEmpty(actual)
        }
    }

    @Nested
    @DisplayName("getSpotSummariesのテスト")
    inner class GetSpotSummariesTest {
        val summaryRequest: SummaryRequest.Csv = mock()
        val siteIds = listOf(1, 2, 3).map { SiteId(it) }
        val spotIds = listOf(4, 5, 6).map { SpotId(it) }
        val siteViewMap: Map<SiteId, SiteView> = siteIds.associateWith { mock() }
        val spots: List<Spot> = spotIds.map { id ->
            mock {
                on { this.spotId } doReturn id
            }
        }
        val deliveryFormatsViewMap: Map<SpotId, DeliveryFormatsView> = mock()
        val summaryViewMap: Map<SpotId, SummaryView> = mock()
        val divIdMap: Map<SpotId, String> = mock()
        val spotViews: List<SpotReportCsvView> = listOf(mock())

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotReportCsvView)
            every { SpotReportCsvView.of(any(), any(), any(), any(), any()) } returns spotViews

            doReturn(siteViewMap).whenever(sut).getSiteViewMap(any())
            doReturn(deliveryFormatsViewMap).whenever(sut).getDeliveryFormatsViewMap(any())
            doReturn(summaryViewMap).whenever(sut).getSummaryViewMap(any(), any(), any())
            doReturn(divIdMap).whenever(sut).getDivIdMap(any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("取得データあり")
        fun isFound() {
            doReturn(spots).whenever(spotDao).selectBySiteIdsAndStatuses(any(), any(), any(), any())

            val actual = sut.getSpotReportCsvViews(coAccountId, summaryRequest)

            assertEquals(spotViews, actual)
            verify(sut, times(1)).getSiteViewMap(coAccountId)
            verify(spotDao, times(1)).selectBySiteIdsAndStatuses(siteIds.toSet(), SpotStatus.viewableStatuses)
            verify(sut, times(1)).getDeliveryFormatsViewMap(spotIds)
            verify(sut, times(1)).getSummaryViewMap(coAccountId, spotIds, summaryRequest)
            verify(sut, times(1)).getDivIdMap(spotIds)
            verifyK {
                SpotReportCsvView.of(
                    spots,
                    siteViewMap,
                    deliveryFormatsViewMap,
                    summaryViewMap,
                    divIdMap
                )
            }
        }

        @Test
        @DisplayName("対象データなし")
        fun isEmptyResult() {
            doReturn(emptyList<Spot>()).whenever(spotDao).selectBySiteIdsAndStatuses(any(), any(), any(), any())

            val actual = sut.getSpotReportCsvViews(coAccountId, summaryRequest)

            assertEmpty(actual)
        }
    }

    @Nested
    @DisplayName("getSpotSizeTypesMapのテスト")
    inner class GetSpotSizeTypesMapTest {
        val spotSizeMap = mapOf(
            SpotId(1) to listOf(1, 2, 3).map { SizeTypeId(it) },
            SpotId(2) to listOf(1, 2).map { SizeTypeId(it) },
            SpotId(3) to listOf(2, 3).map { SizeTypeId(it) }
        )
        val relaySpotSizeTypes: List<RelaySpotSizetype> = spotSizeMap
            .entries
            .flatMap { es ->
                es.value.map { sizeTypeId ->
                    mock {
                        on { this.spotId } doReturn es.key
                        on { this.sizeTypeId } doReturn sizeTypeId
                    }
                }
            }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(relaySpotSizeTypes).whenever(relaySpotSizetypeDao).selectBySpotIds(any())

            val actual = sut.getSpotSizeTypesMap(spotSizeMap.keys)

            assertEquals(spotSizeMap, actual)
            verify(relaySpotSizetypeDao, times(1)).selectBySpotIds(spotSizeMap.keys)
        }
    }

    @Nested
    @DisplayName("getSiteViewMapのテスト")
    inner class GetSiteViewMapTest {
        val sites: List<Site> = listOf(mock(), mock(), mock())
        val siteViewMap: Map<SiteId, SiteView> = listOf(1, 2, 3).map { SiteId(it) }.associateWith { id ->
            mock {
                on { this.siteId } doReturn id
            }
        }

        @BeforeEach
        fun beforeEach() {
            mockkObject(SiteView)
            every { SiteView.of(any()) } returns siteViewMap.values.toList()

            doReturn(sites).whenever(siteDao).selectByCoAccountIdAndStatuses(any(), any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getSiteViewMap(coAccountId)

            assertEquals(siteViewMap, actual)
            verify(siteDao, times(1)).selectByCoAccountIdAndStatuses(coAccountId, SiteStatus.nonArchiveStatuses)
            verifyK { SiteView.of(sites) }
        }
    }

    @Nested
    @DisplayName("getSizeTypeInfoViewMapのテスト")
    inner class GetSizeTypeInfoViewMapTest {
        val sizeTypes: List<SizeTypeInfo> = listOf(mock(), mock(), mock())
        val sizeTypeMap: Map<SizeTypeId, SizeTypeInfoView> =
            listOf(1, 2, 3).map { SizeTypeId(it) }.associateWith { id ->
                mock {
                    on { this.sizeTypeId } doReturn id
                }
            }

        @BeforeEach
        fun beforeEach() {
            mockkObject(SizeTypeInfoView)
            every { SizeTypeInfoView.of(any()) } returns sizeTypeMap.values.toList()

            doReturn(sizeTypes).whenever(sizeTypeInfoDao).selectByIds(any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.getSizeTypeInfoViewMap(sizeTypeMap.keys)

            assertEquals(sizeTypeMap, actual)
            verify(sizeTypeInfoDao, times(1)).selectByIds(sizeTypeMap.keys)
            verifyK { SizeTypeInfoView.of(sizeTypes) }
        }
    }

    @Nested
    @DisplayName("getDeliveryFormatsViewMapのテスト")
    inner class GetDeliveryFormatsViewMapTest {
        val spotIds = listOf(1, 2, 3, 4).map { SpotId(it) }
        val activeBannerIds = listOf(1, 2).map { SpotId(it) }
        val activeNativeIds = listOf(1, 2).map { SpotId(it) }
        val activeVideoIds = listOf(1, 3).map { SpotId(it) }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(activeBannerIds).whenever(sut).getActiveBannerSpotIds(any())
            doReturn(activeNativeIds).whenever(sut).getActiveNativeSpotIds(any())
            doReturn(activeVideoIds).whenever(sut).getActiveVideoSpotIds(any())

            val actual = sut.getDeliveryFormatsViewMap(spotIds)

            assertEquals(
                mapOf(
                    spotIds[0] to DeliveryFormatsView(true, true, true),
                    spotIds[1] to DeliveryFormatsView(true, true, false),
                    spotIds[2] to DeliveryFormatsView(false, false, true),
                    spotIds[3] to DeliveryFormatsView(false, false, false)
                ),
                actual
            )
            verify(sut, times(1)).getActiveBannerSpotIds(spotIds)
            verify(sut, times(1)).getActiveNativeSpotIds(spotIds)
            verify(sut, times(1)).getActiveVideoSpotIds(spotIds)
        }
    }

    @Nested
    @DisplayName("getActiveBannerSpotIdsのテスト")
    inner class GetActiveBannerSpotIdsTest {
        val spotIds = listOf(1, 2, 3).map { SpotId(it) }
        val activeBannerSpotIds = listOf(1, 2).map { SpotId(it) }
        val spotBannerDisplays: List<SpotBannerDisplay> = activeBannerSpotIds.map { id ->
            mock {
                on { this.spotId } doReturn id
            }
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(spotBannerDisplays).whenever(spotBannerDisplayDao).selectByIds(any())

            val actual = sut.getActiveBannerSpotIds(spotIds)

            assertEquals(activeBannerSpotIds, actual)
            verify(spotBannerDisplayDao, times(1)).selectByIds(spotIds)
        }
    }

    @Nested
    @DisplayName("getActiveNativeSpotIdsのテスト")
    inner class GetActiveNativeSpotIdsTest {
        val spotIds = listOf(1, 2, 3, 4, 5, 6).map { SpotId(it) }
        val activeNativeSpotIds = listOf(1, 2, 3).map { SpotId(it) }
        val activeNativeVideoSpotIds = listOf(3, 4).map { SpotId(it) }
        val spotNativeDisplays: List<SpotNativeDisplay> = activeNativeSpotIds.map { id ->
            mock {
                on { this.spotId } doReturn id
            }
        }
        val spotNativeVideoDisplays: List<SpotNativeVideoDisplay> = activeNativeVideoSpotIds.map { id ->
            mock {
                on { this.spotId } doReturn id
            }
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(spotNativeDisplays).whenever(spotNativeDisplayDao).selectByIds(any())
            doReturn(spotNativeVideoDisplays).whenever(spotNativeVideoDisplayDao).selectBySpotIds(any())

            val actual = sut.getActiveNativeSpotIds(spotIds)

            assertEquals((activeNativeSpotIds + activeNativeVideoSpotIds).distinct(), actual)
            verify(spotNativeDisplayDao, times(1)).selectByIds(spotIds)
            verify(spotNativeVideoDisplayDao, times(1)).selectBySpotIds(spotIds)
        }
    }

    @Nested
    @DisplayName("getActiveVideoSpotIdsのテスト")
    inner class GetActiveVideoSpotIdsTest {
        val spotIds = listOf(1, 2, 3).map { SpotId(it) }
        val activeVideoSpotIds = listOf(1, 2).map { SpotId(it) }
        val spotVideoDisplays: List<SpotVideoDisplay> = (activeVideoSpotIds).flatMap { id ->
            List(2) {
                mock {
                    on { this.spotId } doReturn id
                }
            }
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(spotVideoDisplays).whenever(spotVideoDisplayDao).selectBySpotIds(any())

            val actual = sut.getActiveVideoSpotIds(spotIds)

            assertEquals(activeVideoSpotIds, actual)
            verify(spotVideoDisplayDao, times(1)).selectBySpotIds(spotIds)
        }
    }

    @Nested
    @DisplayName("getSummaryViewMapのテスト")
    inner class GetSummaryViewMapTest {
        val spotIds = listOf(1, 2, 3).map { SpotId(it) }
        val summaryCos: List<SpotSummaryCo> = listOf(1, 2).map { id ->
            mock {
                on { this.spotId } doReturn SpotId(id)
            }
        }
        val requestCos: List<SpotRequestSummaryCo> = listOf(1).map { id ->
            mock {
                on { this.spotId } doReturn SpotId(id)
            }
        }
        val taxRate = BigDecimal("1.1")
        val commonConfig: CommonConfig = mock {
            on { this.taxRate } doReturn taxRate
        }
        val summaryView: SummaryView = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(SummaryView)
            every {
                SummaryView.of(any<SpotSummaryCo>(), any<SpotRequestSummaryCo>(), any(), any())
            } returns summaryView

            doReturn(Summaries(summaryCos, requestCos)).whenever(summaryHelper).getSpotSummaries(any(), any(), any())
            doReturn(commonConfig).whenever(localFileUtils).loadConfig(any<LocalFileType.CommonConfig>())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        @DisplayName("正常")
        fun isCorrect(isTaxIncluded: Boolean) {
            val summaryRequest: SummaryRequest.ListView = mock {
                on { this.isTaxIncluded } doReturn isTaxIncluded
            }

            val actual = sut.getSummaryViewMap(coAccountId, spotIds, summaryRequest)

            assertEquals(
                mapOf(
                    spotIds[0] to summaryView,
                    spotIds[1] to summaryView,
                    spotIds[2] to SummaryView.zero
                ),
                actual
            )
            verify(summaryHelper, times(1)).getSpotSummaries(coAccountId, spotIds, summaryRequest)
            verify(localFileUtils, times(1)).loadConfig(LocalFileType.CommonConfig)
            verifyK(atLeast = 1) { SummaryView.of(summaryCos[0], requestCos[0], isTaxIncluded, taxRate) }
            verifyK(atLeast = 1) { SummaryView.of(summaryCos[1], null, isTaxIncluded, taxRate) }
        }
    }
}
