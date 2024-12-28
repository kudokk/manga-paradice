package jp.mangaka.ssp.presentation.common.summary

import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.TotalRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.TotalSummaryCo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("SummaryViewのテスト")
private class SummaryViewTest {
    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val taxRate = 1.1.toBigDecimal()

        @Test
        @DisplayName("税込みオフ、リクエスト実績なし")
        fun isTaxOffAndRequestNotExist() {
            val actual = SummaryView.of(
                TotalSummaryCo(1000, 800, BigDecimal("2000.00000000")),
                null,
                false,
                taxRate
            )

            assertEquals(SummaryView(1000, 800, 0, "2000.00", "0.0", "80.000", "2000.00", "2.50"), actual)
        }

        @Test
        @DisplayName("税込みオン、リクエスト実績あり")
        fun isTaxOnAndRequestExist() {
            val actual = SummaryView.of(
                TotalSummaryCo(1000, 800, BigDecimal("2000.00000000")),
                TotalRequestSummaryCo(500),
                true,
                taxRate
            )

            assertEquals(SummaryView(1000, 800, 500, "2200.00", "200.0", "80.000", "2200.00", "2.75"), actual)
        }
    }
}
