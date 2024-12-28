package jp.mangaka.ssp.util

import java.math.BigDecimal
import java.math.RoundingMode

object SummaryUtils {
    /**
     * レベニューを取得する.
     *
     * gross を少数第3位で四捨五入
     *
     * @param gross グロス
     * @return 算出したレベニュー
     */
    fun calcRevenue(gross: BigDecimal): BigDecimal = gross
        .setScale(2, RoundingMode.HALF_UP)

    /**
     * カバレッジを取得する.
     *
     * impression * 100 / request を少数第2位で四捨五入
     *
     * @param impression インプレッション数
     * @param request リクエスト数
     * @return 算出したカバレッジ
     */
    fun calcCoverage(impression: Long, request: Long): BigDecimal =
        (impression * 100)
            .toBigDecimal()
            .divideSafety(request.toBigDecimal(), 1, RoundingMode.HALF_UP)

    /**
     * CTRを取得する.
     *
     * click * 100 / impression を少数第4位で四捨五入
     *
     * @param click クリック数
     * @param impression インプレッション数
     * @return 算出したCTR
     */
    fun calcCtr(click: Long, impression: Long): BigDecimal =
        (click * 100)
            .toBigDecimal()
            .divideSafety(impression.toBigDecimal(), 3, RoundingMode.HALF_UP)

    /**
     * eCPMを取得する.
     *
     * gross * 1000 / impression を少数第3位で四捨五入
     *
     * @param gross グロス
     * @param impression インプレッション数
     * @return 算出したeCPM
     */
    fun calcEcpm(gross: BigDecimal, impression: Long): BigDecimal =
        gross
            .multiply(1000.toBigDecimal())
            .divideSafety(impression.toBigDecimal(), 2, RoundingMode.HALF_UP)

    /**
     * eCPCを算出
     *
     * gross / click を少数第3位で四捨五入
     *
     * @param gross グロス
     * @param click クリック数
     * @return 算出したeCPC
     */
    fun calcEcpc(gross: BigDecimal, click: Long): BigDecimal =
        gross.divideSafety(click.toBigDecimal(), 2, RoundingMode.HALF_UP)

    // 除数が 0 の場合は 0 を返却する除算の関数
    private fun BigDecimal.divideSafety(
        divisor: BigDecimal,
        scale: Int,
        roundingMode: RoundingMode
    ): BigDecimal = if (divisor.signum() == 0) {
        BigDecimal.ZERO.setScale(scale)
    } else {
        divide(divisor, scale, roundingMode)
    }
}
