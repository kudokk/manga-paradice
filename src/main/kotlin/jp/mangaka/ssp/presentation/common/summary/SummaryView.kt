package jp.mangaka.ssp.presentation.common.summary

import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo
import jp.mangaka.ssp.util.SummaryUtils.calcCoverage
import jp.mangaka.ssp.util.SummaryUtils.calcCtr
import jp.mangaka.ssp.util.SummaryUtils.calcEcpc
import jp.mangaka.ssp.util.SummaryUtils.calcEcpm
import jp.mangaka.ssp.util.SummaryUtils.calcRevenue
import java.math.BigDecimal

data class SummaryView(
    val impression: Long,
    val click: Long,
    val request: Long,
    val revenue: String,
    val coverage: String,
    val ctr: String,
    val ecpm: String,
    val ecpc: String
) {
    companion object {
        /**
         * 配信実績なし
         */
        val zero = SummaryView(0, 0, 0, "0.00", "0.0", "0.000", "0.00", "0.00")

        /**
         * 配信実績集計結果のViewを生成する
         *
         * @param summaryCo impression、click、grossの実績
         * @param requestSummaryCo requestの実績
         * @param isTaxIncluded 税込みの有無
         * @param taxRate 税率の乗数（税率が 10％ なら 1.1 が設定される）
         * @return 生成した配信実績集計結果のView
         */
        fun <T : SummaryCo<T>, R : RequestSummaryCo<R>> of(
            summaryCo: T,
            requestSummaryCo: R?,
            isTaxIncluded: Boolean,
            taxRate: BigDecimal
        ): SummaryView {
            val gross = if (isTaxIncluded) summaryCo.gross.multiply(taxRate) else summaryCo.gross
            val request = requestSummaryCo?.request ?: 0

            return SummaryView(
                summaryCo.impression,
                summaryCo.click,
                request,
                calcRevenue(gross).toPlainString(),
                calcCoverage(summaryCo.impression, request).toPlainString(),
                calcCtr(summaryCo.click, summaryCo.impression).toPlainString(),
                calcEcpm(gross, summaryCo.impression).toPlainString(),
                calcEcpc(gross, summaryCo.click).toPlainString()
            )
        }
    }
}
