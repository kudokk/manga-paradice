package jp.mangaka.ssp.presentation.controller.spot.csv

import jp.mangaka.ssp.presentation.controller.spot.view.SpotReportCsvView
import jp.mangaka.ssp.presentation.common.csv.AbstractReportCsvGenerator
import jp.mangaka.ssp.presentation.common.summary.SummaryRequest
import org.jetbrains.annotations.TestOnly
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.Locale

@Component
class SpotReportCsvGenerator(override val messageSource: MessageSource) : AbstractReportCsvGenerator() {
    /**
     * 広告枠配信実績レポートのCSVを生成する.
     *
     * @param csvRequest CSVダウンロードのリクエスト内容
     * @param spotReportCsvViews 配信実績のViewのリスト
     * @param locale ロケール
     * @return 広告枠配信実績レポートのCSV
     */
    fun generate(
        csvRequest: SummaryRequest.Csv,
        spotReportCsvViews: Collection<SpotReportCsvView>,
        locale: Locale
    ): String = listOf(
        convertCsvRecord(messageSource.getMessage("spot.delivery.result.report", null, locale)),
        generateCreateDateRecord(LocalDate.now(), locale),
        generateReportPeriod(csvRequest.startDate, csvRequest.endDate, locale),
        generateReportCondition(messageSource.getMessage("spot.all", null, locale), locale),
        generateTaxSelection(csvRequest.isTaxIncluded, locale),
        generateRevenueType(csvRequest.isExpectedRevenue, locale),
        convertCsvRecord(""),
        generateReportHeader(locale),
        generateReportRecords(spotReportCsvViews)
    ).joinToString("")

    /**
     * 配信実績のヘッダ行を生成する.
     *
     * @param locale ロケール
     * @return 集計結果のヘッダ行のCSVレコード
     */
    @TestOnly
    fun generateReportHeader(locale: Locale): String = convertCsvRecord(
        messageSource.getMessage("site.id", null, locale),
        messageSource.getMessage("site.name", null, locale),
        messageSource.getMessage("spot.id", null, locale),
        messageSource.getMessage("spot.name", null, locale),
        "div ID",
        "Request",
        "Impression",
        "Coverage",
        "Click",
        "CTR",
        "eCPM",
        "eCPC",
        "Revenue",
        messageSource.getMessage("banner.settings", null, locale),
        messageSource.getMessage("native.settings", null, locale),
        messageSource.getMessage("video.settings", null, locale)
    )

    /**
     * 配信実績のレコード行を生成する.
     *
     * @param spotReportCsvViews 配信実績のリスト
     * @return 集計結果のレコード行のCSVレコード
     */
    @TestOnly
    fun generateReportRecords(spotReportCsvViews: Collection<SpotReportCsvView>): String = spotReportCsvViews.map {
        listOf(
            it.siteId.toString(),
            it.siteName,
            it.spotId.toString(),
            it.spotName,
            it.divId,
            it.deliveryResult.request.toString(),
            it.deliveryResult.impression.toString(),
            it.deliveryResult.coverage,
            it.deliveryResult.click.toString(),
            it.deliveryResult.ctr,
            it.deliveryResult.ecpm,
            it.deliveryResult.ecpc,
            it.deliveryResult.revenue,
            convertIsActiveFormatText(it.deliveryFormats.isActiveBanner),
            convertIsActiveFormatText(it.deliveryFormats.isActiveNative),
            convertIsActiveFormatText(it.deliveryFormats.isActiveVideo)
        )
    }.let { convertCsvRecords(it) }

    private fun convertIsActiveFormatText(isActive: Boolean): String = if (isActive) "〇" else "-"
}
