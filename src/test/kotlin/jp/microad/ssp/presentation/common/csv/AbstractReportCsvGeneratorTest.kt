package jp.mangaka.ssp.presentation.common.csv

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.time.LocalDate
import java.util.Locale

// CSV変換関数でIOExceptionを発生させるのは難しそうなので、テストの実装を省略しています.
@DisplayName("AbstractReportCsvGeneratorのテスト")
private class AbstractReportCsvGeneratorTest {
    val locale: Locale = Locale.JAPANESE
    val sut = object : AbstractReportCsvGenerator() {
        override val messageSource = ReloadableResourceBundleMessageSource().apply {
            this.setBasename("messages/messages")
            this.setDefaultEncoding("UTF-8")
            this.setFallbackToSystemLocale(false)
        }
    }

    @Nested
    @DisplayName("convertCsvRecordsのテスト")
    inner class ConvertCsvRecordsTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.convertCsvRecords(
                listOf(
                    listOf("1", "test1", "100"),
                    listOf("2", "test2", "200"),
                    listOf("3", "test3", "300")
                )
            )

            assertEquals(
                """
                    "1","test1","100"
                    "2","test2","200"
                    "3","test3","300"
                    
                """.toCsvRecord(),
                actual
            )
        }
    }

    @Nested
    @DisplayName("convertCsvRecordのテスト")
    inner class ConvertCsvRecordTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.convertCsvRecord("1", "test1", "100")

            assertEquals(
                """
                    "1","test1","100"
                    
                """.toCsvRecord(),
                actual
            )
        }
    }

    @Nested
    @DisplayName("generateCreateDateRecordのテスト")
    inner class GenerateCreateDateRecordTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.generateCreateDateRecord(LocalDate.of(2024, 1, 2), locale)

            assertEquals(
                """
                    "作成日","2024/01/02"
                    
                """.toCsvRecord(),
                actual
            )
        }
    }

    @Nested
    @DisplayName("generateReportPeriodのテスト")
    inner class GenerateReportPeriodTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.generateReportPeriod(LocalDate.of(2024, 1, 2), LocalDate.of(2024, 2, 3), locale)

            assertEquals(
                """
                    "出力期間","2024/01/02 から 2024/02/03 まで"
                    
                """.toCsvRecord(),
                actual
            )
        }
    }

    @Nested
    @DisplayName("generateReportConditionのテスト")
    inner class GenerateReportConditionTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.generateReportCondition("全てのテスト", locale)

            assertEquals(
                """
                    "出力条件","全てのテスト"
                    
                """.toCsvRecord(),
                actual
            )
        }
    }

    @Nested
    @DisplayName("generateTaxSelectionのテスト")
    inner class GenerateTaxSelectionTest {
        @ParameterizedTest
        @CsvSource(value = ["true,税込", "false,税抜"])
        @DisplayName("正常")
        fun isCorrect(isTaxIncluded: Boolean, value: String) {
            val actual = sut.generateTaxSelection(isTaxIncluded, locale)

            assertEquals(
                """
                    "金額表示","$value"
                    
                """.toCsvRecord(),
                actual
            )
        }
    }

    @Nested
    @DisplayName("generateRevenueTypeのテスト")
    inner class GenerateRevenueTypeTest {
        @ParameterizedTest
        @CsvSource(value = ["true,想定Revenue", "false,実Revenue"])
        @DisplayName("正常")
        fun isCorrect(isExpectedRevenue: Boolean, value: String) {
            val actual = sut.generateRevenueType(isExpectedRevenue, locale)

            assertEquals(
                """
                    "収益区分","$value"
                    
                """.toCsvRecord(),
                actual
            )
        }
    }

    private fun String.toCsvRecord() = trimIndent().replace("\n", "\r\n")
}
