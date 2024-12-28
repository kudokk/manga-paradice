package jp.mangaka.ssp.presentation.controller.spot

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.servlet.http.HttpSession
import jp.mangaka.ssp.application.service.spot.SpotViewService
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.SessionUtils
import jp.mangaka.ssp.presentation.common.summary.SummaryRequest
import jp.mangaka.ssp.presentation.common.summary.SummaryView
import jp.mangaka.ssp.presentation.controller.spot.csv.SpotReportCsvGenerator
import jp.mangaka.ssp.presentation.controller.spot.view.AspectRatioView
import jp.mangaka.ssp.presentation.controller.common.view.CountrySelectElementView
import jp.mangaka.ssp.presentation.controller.spot.view.CurrencyView
import jp.mangaka.ssp.presentation.controller.spot.view.DecorationView
import jp.mangaka.ssp.presentation.controller.spot.view.DspView
import jp.mangaka.ssp.presentation.controller.spot.view.FixedCpmView
import jp.mangaka.ssp.presentation.controller.spot.view.SiteView
import jp.mangaka.ssp.presentation.controller.spot.view.SizeTypeInfoView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.SpotDetailView
import jp.mangaka.ssp.presentation.controller.spot.view.nativedesign.NativeDesignPreviewView
import jp.mangaka.ssp.presentation.controller.spot.view.nativedesign.NativeDesignsView
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import io.mockk.verify as verifyK

@DisplayName("SpotFetchControllerのテスト")
private class SpotFetchControllerTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val spotId = SpotId(1)
    }

    val spotViewService: SpotViewService = mock()
    val spotReportCsvGenerator: SpotReportCsvGenerator = mock()
    val session: HttpSession = mock()

    val sut = SpotFetchController(spotViewService, spotReportCsvGenerator, session)

    @Nested
    @DisplayName("getSitesのテスト")
    inner class GetSitesTest {
        val sitesView: List<SiteView> = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(sitesView).whenever(spotViewService).getSitesView(coAccountId)

            val actual = sut.getSites(coAccountId)

            assertEquals(sitesView, actual)
            verify(spotViewService, times(1)).getSitesView(coAccountId)
        }
    }

    @Nested
    @DisplayName("getCurrenciesのテスト")
    inner class GetCurrenciesTest {
        val currenciesView: List<CurrencyView> = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(currenciesView).whenever(spotViewService).getCurrenciesView()

            val actual = sut.getCurrencies()

            assertEquals(currenciesView, actual)
            verify(spotViewService, times(1)).getCurrenciesView()
        }
    }

    @Nested
    @DisplayName("getSizeTypeInfosのテスト")
    inner class GetSizeTypeInfosTest {
        val sizeTypeInfosView: List<SizeTypeInfoView> = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(sizeTypeInfosView).whenever(spotViewService).getSizeTypeInfosView(any())

            val actual = sut.getSizeTypeInfos(coAccountId)

            assertEquals(sizeTypeInfosView, actual)
            verify(spotViewService, times(1)).getSizeTypeInfosView(coAccountId)
        }
    }

    @Nested
    @DisplayName("getDecorationsのテスト")
    inner class GetDecorationsTest {
        val decorationsView: List<DecorationView> = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(decorationsView).whenever(spotViewService).getDecorationsView(coAccountId)

            val actual = sut.getDecorations(coAccountId)

            assertEquals(decorationsView, actual)
            verify(spotViewService, times(1)).getDecorationsView(coAccountId)
        }
    }

    @Nested
    @DisplayName("getCountriesのテスト")
    inner class GetCountriesTest {
        val countriesView: List<CountrySelectElementView> = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(countriesView).whenever(spotViewService).getCountriesView()

            val actual = sut.getCountries()

            assertEquals(countriesView, actual)
            verify(spotViewService, times(1)).getCountriesView()
        }
    }

    @Nested
    @DisplayName("getDspsのテスト")
    inner class GetDspsTest {
        val dspsView: List<DspView> = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(dspsView).whenever(spotViewService).getDspsView(any())

            val actual = sut.getDsps(coAccountId)

            assertEquals(dspsView, actual)
            verify(spotViewService, times(1)).getDspsView(coAccountId)
        }
    }

    @Nested
    @DisplayName("getNativeDesignsのテスト")
    inner class GetNativeDesignsTest {
        val nativeDesignsView: NativeDesignsView = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(nativeDesignsView).whenever(spotViewService).getNativeDesignsView(any())

            val actual = sut.getNativeDesigns(coAccountId)

            assertEquals(nativeDesignsView, actual)
            verify(spotViewService, times(1)).getNativeDesignsView(coAccountId)
        }
    }

    @Nested
    @DisplayName("getNativeDesignPreviewのテスト")
    inner class GetNativeDesignPreviewTest {
        val nativeTemplateId = NativeTemplateId(1)
        val nativeDesignPreviewView: NativeDesignPreviewView = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(nativeDesignPreviewView).whenever(spotViewService).getNativeDesignPreviewView(any(), any())

            val actual = sut.getNativeDesignPreview(coAccountId, nativeTemplateId)

            assertEquals(nativeDesignPreviewView, actual)
            verify(spotViewService, times(1)).getNativeDesignPreviewView(coAccountId, nativeTemplateId)
        }
    }

    @Nested
    @DisplayName("getAspectRatiosのテスト")
    inner class GetAspectRatiosTest {
        val views: List<AspectRatioView> = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(views).whenever(spotViewService).getAspectRatiosView()

            val actual = sut.getAspectRatios()

            assertEquals(views, actual)

            verify(spotViewService, times(1)).getAspectRatiosView()
        }
    }

    @Nested
    @DisplayName("getSpotDetailのテスト")
    inner class GetSpotDetailTest {
        @BeforeEach
        fun beforeEach() {
            mockkObject(SessionUtils)
            every { SessionUtils.getUserType(any(), any()) } returns UserType.ma_staff
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val expected: SpotDetailView = mock()
            doReturn(expected).whenever(spotViewService).getSpotDetail(any(), any(), any())

            val actual = sut.getSpotDetail(coAccountId, spotId)

            assertEquals(expected, actual)
            verify(spotViewService, times(1)).getSpotDetail(coAccountId, spotId, UserType.ma_staff)
            verifyK { SessionUtils.getUserType(coAccountId, session) }
        }
    }

    @Test
    @DisplayName("getSpotFixedCpmsのテスト")
    fun testGetSpotFixedCpms() {
        val views: List<FixedCpmView> = mock()
        doReturn(views).whenever(spotViewService).getFixedCpms(any(), any())

        val actual = sut.getSpotFixedCpms(coAccountId, spotId)

        assertEquals(views, actual)
        verify(spotViewService, times(1)).getFixedCpms(coAccountId, spotId)
    }

    @Test
    @DisplayName("getCoAccountSpotsTotalSummaryのテスト")
    fun testGetCoAccountSpotsTotalSummary() {
        val summaryRequest: SummaryRequest.ListView = mock()
        val view: SummaryView = mock()
        doReturn(view).whenever(spotViewService).getCoAccountSpotsTotalSummaryView(any(), any())

        val actual = sut.getCoAccountSpotsTotalSummary(coAccountId, summaryRequest)

        assertEquals(view, actual)
        verify(spotViewService, times(1)).getCoAccountSpotsTotalSummaryView(coAccountId, summaryRequest)
    }
}
