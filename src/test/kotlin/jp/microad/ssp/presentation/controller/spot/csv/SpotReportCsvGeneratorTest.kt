package jp.mangaka.ssp.presentation.controller.spot.csv

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import jp.mangaka.ssp.presentation.controller.spot.view.SpotReportCsvView
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.common.summary.SummaryRequest
import jp.mangaka.ssp.presentation.common.summary.SummaryView
import jp.mangaka.ssp.presentation.controller.spot.view.DeliveryFormatsView
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.time.LocalDate
import java.util.Locale

@DisplayName("SpotReportCsvGeneratorのテスト")
private class SpotReportCsvGeneratorTest {
    val locale: Locale = Locale.JAPANESE
    val messageSource = ReloadableResourceBundleMessageSource().apply {
        this.setBasename("messages/messages")
        this.setDefaultEncoding("UTF-8")
        this.setFallbackToSystemLocale(false)
    }
    val sut = spy(SpotReportCsvGenerator(messageSource))

    @Nested
    @DisplayName("generateのテスト")
    inner class GenerateTest {
        val startDate: LocalDate = LocalDate.of(2024, 10, 1)
        val endDate: LocalDate = LocalDate.of(2024, 10, 30)
        val now: LocalDate = LocalDate.of(2024, 11, 1)

        @BeforeEach
        fun beforeEach() {
            mockkStatic(LocalDate::ofInstant)
            every { LocalDate.now() } returns now

            doReturn(convertRecord("作成日", "2024/11/01")).whenever(sut).generateCreateDateRecord(any(), any())
            doReturn(convertRecord("出力期間", "2024/10/01 から 2024/10/30 まで"))
                .whenever(sut)
                .generateReportPeriod(any(), any(), any())
            doReturn(convertRecord("出力条件", "すべて")).whenever(sut).generateReportCondition(any(), any())
            doReturn(convertRecord("金額表示", "税込")).whenever(sut).generateTaxSelection(any(), any())
            doReturn(convertRecord("収益区分", "想定Revenue")).whenever(sut).generateRevenueType(any(), any())
            doReturn(convertRecord("広告枠ID", "広告枠名", "Impression"))
                .whenever(sut)
                .generateReportHeader(any())
            doReturn(
                listOf(
                    convertRecord("1", "広告枠1", "100"),
                    convertRecord("2", "広告枠2", "200"),
                    convertRecord("3", "広告枠3", "300")
                ).joinToString("")
            ).whenever(sut).generateReportRecords(any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val csvRequest = csvRequest(startDate, endDate, true, false)
            val spotReportCsvViews: Collection<SpotReportCsvView> = mock()

            val actual = sut.generate(csvRequest, spotReportCsvViews, locale)

            assertEquals(
                """
                    "広告枠配信実績レポート"
                    "作成日","2024/11/01"
                    "出力期間","2024/10/01 から 2024/10/30 まで"
                    "出力条件","すべて"
                    "金額表示","税込"
                    "収益区分","想定Revenue"
                    ""
                    "広告枠ID","広告枠名","Impression"
                    "1","広告枠1","100"
                    "2","広告枠2","200"
                    "3","広告枠3","300"
                    
                """.convertRecords(),
                actual
            )
        }
    }

    @Nested
    @DisplayName("generateReportHeaderのテスト")
    inner class GenerateReportHeaderTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.generateReportHeader(locale)

            assertEquals(
                """
                    "サイトID","サイト名","広告枠ID","広告枠名","div ID","Request","Impression","Coverage","Click","CTR","eCPM","eCPC","Revenue","バナー設定","ネイティブ設定","ビデオ設定"
                    
                """.convertRecords(),
                actual
            )
        }
    }

    @Nested
    @DisplayName("generateReportRecordsのテスト")
    inner class GenerateReportRecordsTest {
        @Test
        @DisplayName("レコードあり")
        fun isNotEmpty() {
            val views = listOf(
                spotReportCsvView(
                    10, "site10", 20, "spot20", "div20", 30, 40, "50", 60, "70", "80", "90", "100", true, false, false
                ),
                spotReportCsvView(
                    10, "site10", 21, "spot21", "div21", 31, 41, "51", 61, "71", "81", "91", "101", false, true, false
                ),
                spotReportCsvView(
                    11, "site11", 22, "spot22", "div22", 32, 42, "52", 62, "72", "82", "92", "102", false, false, true
                )
            )

            val actual = sut.generateReportRecords(views)

            assertEquals(
                """
                    "10","site10","20","spot20","div20","30","40","50","60","70","80","90","100","〇","-","-"
                    "10","site10","21","spot21","div21","31","41","51","61","71","81","91","101","-","〇","-"
                    "11","site11","22","spot22","div22","32","42","52","62","72","82","92","102","-","-","〇"
                    
                """.convertRecords(),
                actual
            )
        }

        @Test
        @DisplayName("レコードなし")
        fun isEmpty() {
            val actual = sut.generateReportRecords(emptyList())

            assertEquals(
                """
                    
                """.trimIndent().convertRecords(),
                actual
            )
        }
    }

    private fun convertRecord(vararg columns: String): String = columns.joinToString("\",\"", "\"", "\"\r\n")

    private fun String.convertRecords(): String = this.trimIndent().replace("\n", "\r\n")

    private fun csvRequest(
        startDate: LocalDate,
        endDate: LocalDate,
        isTaxIncluded: Boolean,
        isExpectedRevenue: Boolean
    ): SummaryRequest.Csv = mock {
        on { this.startDate } doReturn startDate
        on { this.endDate } doReturn endDate
        on { this.isTaxIncluded } doReturn isTaxIncluded
        on { this.isExpectedRevenue } doReturn isExpectedRevenue
    }

    private fun spotReportCsvView(
        siteId: Int,
        siteName: String,
        spotId: Int,
        spotName: String,
        divId: String,
        request: Long,
        impression: Long,
        coverage: String,
        click: Long,
        ctr: String,
        ecpm: String,
        ecpc: String,
        revenue: String,
        isActiveBanner: Boolean,
        isActiveNative: Boolean,
        isActiveVideo: Boolean
    ) = SpotReportCsvView(
        SiteId(siteId), siteName, SpotId(spotId), spotName, divId,
        DeliveryFormatsView(isActiveBanner, isActiveNative, isActiveVideo),
        SummaryView(impression, click, request, revenue, coverage, ctr, ecpm, ecpc)
    )
}
