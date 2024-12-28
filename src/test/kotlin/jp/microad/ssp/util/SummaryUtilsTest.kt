package jp.mangaka.ssp.util

import jp.mangaka.ssp.util.SummaryUtils.calcCoverage
import jp.mangaka.ssp.util.SummaryUtils.calcCtr
import jp.mangaka.ssp.util.SummaryUtils.calcEcpc
import jp.mangaka.ssp.util.SummaryUtils.calcEcpm
import jp.mangaka.ssp.util.SummaryUtils.calcRevenue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("SummaryUtilsのテスト")
private class SummaryUtilsTest {
    @Nested
    @DisplayName("calcRevenueのテスト")
    inner class CalcRevenueTest {
        @Test
        @DisplayName("四捨五入で切り上げ")
        fun isRoundUp() {
            assertEquals(calcRevenue(BigDecimal("12.345")), BigDecimal("12.35"))
        }

        @Test
        @DisplayName("四捨五入で切り捨て")
        fun isRoundDown() {
            assertEquals(calcRevenue(BigDecimal("12.344")), BigDecimal("12.34"))
        }
    }

    @Nested
    @DisplayName("calcCoverageのテスト")
    inner class CalcCoverageTest {
        @Test
        @DisplayName("四捨五入で切り上げ")
        fun isRoundUp() {
            // 200 * 100 / 230 = 86.956521739130434782608695652174
            assertEquals(calcCoverage(200, 230), BigDecimal("87.0"))
        }

        @Test
        @DisplayName("四捨五入で切り捨て")
        fun isRoundDown() {
            // 150 * 100 / 230 = 65.21739130434782608695652173913
            assertEquals(calcCoverage(150, 230), BigDecimal("65.2"))
        }

        @Test
        @DisplayName("ゼロ除算")
        fun isZeroDivide() {
            assertEquals(calcCoverage(100, 0), BigDecimal("0.0"))
        }
    }

    @Nested
    @DisplayName("calcCtrのテスト")
    inner class CalcCtrTest {
        @Test
        @DisplayName("四捨五入で切り上げ")
        fun isRoundUp() {
            // 200 * 100 / 230 = 86.956521739130434782608695652174
            assertEquals(calcCtr(200, 230), BigDecimal("86.957"))
        }

        @Test
        @DisplayName("四捨五入で切り捨て")
        fun isRoundDown() {
            // 150 * 100 / 230 = 65.21739130434782608695652173913
            assertEquals(calcCtr(150, 230), BigDecimal("65.217"))
        }

        @Test
        @DisplayName("ゼロ除算")
        fun isZeroDivide() {
            assertEquals(calcCtr(100, 0), BigDecimal("0.000"))
        }
    }

    @Nested
    @DisplayName("calcEcpmのテスト")
    inner class CalcEcpmTest {
        @Test
        @DisplayName("四捨五入で切り上げ")
        fun isRoundUp() {
            // 200 * 1000 / 175 = 1,142.8571428571428571428571428571
            assertEquals(calcEcpm(BigDecimal("200"), 175), BigDecimal("1142.86"))
        }

        @Test
        @DisplayName("四捨五入で切り捨て")
        fun isRoundDown() {
            // 200 * 1000 / 155 = 1,290.3225806451612903225806451613
            assertEquals(calcEcpm(BigDecimal("200"), 155), BigDecimal("1290.32"))
        }

        @Test
        @DisplayName("ゼロ除算")
        fun isZeroDivide() {
            assertEquals(calcEcpm(BigDecimal("200"), 0), BigDecimal("0.00"))
        }
    }

    @Nested
    @DisplayName("calcEcpcのテスト")
    inner class CalcEcpcTest {
        @Test
        @DisplayName("四捨五入で切り上げ")
        fun isRoundUp() {
            // 200 / 95 = 2.1052631578947368421052631578947
            assertEquals(calcEcpc(BigDecimal("200"), 95), BigDecimal("2.11"))
        }

        @Test
        @DisplayName("四捨五入で切り捨て")
        fun isRoundDown() {
            // 200 / 175 = 1.1428571428571428571428571428571
            assertEquals(calcEcpc(BigDecimal("200"), 175), BigDecimal("1.14"))
        }

        @Test
        @DisplayName("ゼロ除算")
        fun isZeroDivide() {
            assertEquals(calcEcpc(BigDecimal("200"), 0), BigDecimal("0.00"))
        }
    }
}
