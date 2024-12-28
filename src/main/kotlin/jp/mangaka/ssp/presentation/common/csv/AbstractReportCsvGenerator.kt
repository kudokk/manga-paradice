package jp.mangaka.ssp.presentation.common.csv

import jp.mangaka.ssp.util.exception.CompassManagerException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.QuoteMode
import org.jetbrains.annotations.TestOnly
import org.springframework.context.MessageSource
import java.io.IOException
import java.io.StringWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

abstract class AbstractReportCsvGenerator {
    abstract val messageSource: MessageSource

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    private val csvFormat = CSVFormat.EXCEL
        .builder()
        .setQuoteMode(QuoteMode.ALL_NON_NULL)
        .build()

    /**
     * データをCSV形式に変換する.
     *
     * @param records CSVレコード（複数カラム）のリスト
     * @return CSV形式に変換した文字列
     * @throws CompassManagerException CSV形式への変換が失敗したとき.
     */
    @TestOnly
    fun convertCsvRecords(records: List<List<String>>): String {
        val writer = StringWriter()

        try {
            csvFormat
                .print(writer)
                .use { it.printRecords(records) }
        } catch (e: IOException) {
            throw CompassManagerException("CSVダウンロード中にエラーが発生しました。", e)
        }

        return writer.toString()
    }

    /**
     * データをCSV形式に変換する.
     *
     * @param columns CSVレコード（複数カラム）
     * @return CSV形式に変換した文字列
     * @throws CompassManagerException CSV形式への変換が失敗したとき.
     */
    @TestOnly
    fun convertCsvRecord(vararg columns: String): String = convertCsvRecords(listOf(columns.toList()))

    /**
     * 作成日のCSVレコードを生成する.
     *
     * @param date 作成日
     * @param locale ロケール
     * @return 作成日のCSVレコード
     */
    @TestOnly
    fun generateCreateDateRecord(date: LocalDate, locale: Locale): String = convertCsvRecord(
        messageSource.getMessage("create.date", null, locale),
        date.format(dateFormatter)
    )

    /**
     * 出力期間のCSVレコードを生成する.
     *
     * @param startDate 開始日
     * @param endDate 終了日
     * @param locale ロケール
     * @return 出力期間のCSVレコード
     */
    @TestOnly
    fun generateReportPeriod(startDate: LocalDate, endDate: LocalDate, locale: Locale): String =
        convertCsvRecord(
            messageSource.getMessage("report.period", null, locale),
            messageSource.getMessage(
                "report.period.value",
                arrayOf(startDate.format(dateFormatter), endDate.format(dateFormatter)),
                locale
            )
        )

    /**
     * 出力条件のCSVレコードを生成する.
     *
     * @param condition 出力条件
     * @param locale ロケール
     * @return 出力条件のCSVレコード
     */
    @TestOnly
    fun generateReportCondition(condition: String, locale: Locale): String = convertCsvRecord(
        messageSource.getMessage("report.condition", null, locale),
        condition
    )

    /**
     * 金額表示のCSVレコードを生成する.
     *
     * @param isTaxIncluded 税込みかどうか
     * @param locale ロケール
     * @return 金額表示のCSVレコード
     */
    @TestOnly
    fun generateTaxSelection(isTaxIncluded: Boolean, locale: Locale): String = convertCsvRecord(
        messageSource.getMessage("amount.declared", null, locale),
        messageSource.getMessage(if (isTaxIncluded) "tax.included" else "tax.excluded", null, locale)
    )

    /**
     * 収益区分のCSVレコードを生成する.
     *
     * @param isExpectedRevenue 想定レベニューかどうか
     * @param locale ロケール
     * @return 収益区分のCSVレコード
     */
    @TestOnly
    fun generateRevenueType(isExpectedRevenue: Boolean, locale: Locale): String = convertCsvRecord(
        messageSource.getMessage("revenue.type", null, locale),
        messageSource.getMessage(if (isExpectedRevenue) "revenue.expect" else "revenue.actual", null, locale)
    )
}
