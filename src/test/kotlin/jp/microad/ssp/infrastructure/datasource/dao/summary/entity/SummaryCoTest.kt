package jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.SpotSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.TotalSummaryCo
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

@DisplayName("SummaryCoのテスト")
private class SummaryCoTest {
    companion object {
        val spotId10 = SpotId(10)
        val spotId11 = SpotId(11)
        val spotId12 = SpotId(12)
    }

    @Nested
    @DisplayName("TotalSummaryCoのテスト")
    inner class TotalSummaryCoTest {
        @Nested
        @DisplayName("addのテスト")
        inner class AddTest {
            @Test
            @DisplayName("正常")
            fun isCorrect() {
                val actual = TotalSummaryCo(100, 200, BigDecimal("300"))
                    .add(TotalSummaryCo(200, 300, BigDecimal("400")))

                assertEquals(TotalSummaryCo(300, 500, BigDecimal("700")), actual)
            }
        }
    }

    @Nested
    @DisplayName("SpotSummaryCoのテスト")
    inner class SpotSummaryCoTest {
        @Nested
        @DisplayName("addのテスト")
        inner class AddTest {
            @Test
            @DisplayName("正常")
            fun isCorrect() {
                val actual = SpotSummaryCo(spotId10, 100, 200, BigDecimal("300"))
                    .add(SpotSummaryCo(spotId10, 200, 300, BigDecimal("400")))

                assertEquals(SpotSummaryCo(spotId10, 300, 500, BigDecimal("700")), actual)
            }

            @Test
            @DisplayName("異なる集計対象")
            fun isOtherSummaryTarget() {
                assertThrows<CompassManagerException> {
                    SpotSummaryCo(spotId10, 100, 200, BigDecimal("300"))
                        .add(SpotSummaryCo(spotId11, 200, 300, BigDecimal("400")))
                }
            }
        }
    }

    @Nested
    @DisplayName("mergeのテスト")
    inner class MergeTest {
        @Test
        @DisplayName("総計のテスト")
        fun isTotal() {
            val actual = listOf(
                TotalSummaryCo(100, 200, BigDecimal("300")),
                TotalSummaryCo(200, 300, BigDecimal("400"))
            ).merge()

            assertEquals(listOf(TotalSummaryCo(300, 500, BigDecimal("700"))), actual)
        }

        @Test
        @DisplayName("広告枠単位のテスト")
        fun isSpot() {
            val actual = listOf(
                SpotSummaryCo(spotId10, 100, 200, BigDecimal("300")),
                SpotSummaryCo(spotId10, 200, 300, BigDecimal("400")),
                SpotSummaryCo(spotId11, 300, 400, BigDecimal("500")),
                SpotSummaryCo(spotId11, 400, 500, BigDecimal("600")),
                SpotSummaryCo(spotId12, 500, 600, BigDecimal("700")),
                SpotSummaryCo(spotId12, 600, 700, BigDecimal("800"))
            ).merge()

            assertEquals(
                listOf(
                    SpotSummaryCo(spotId10, 300, 500, BigDecimal("700")),
                    SpotSummaryCo(spotId11, 700, 900, BigDecimal("1100")),
                    SpotSummaryCo(spotId12, 1100, 1300, BigDecimal("1500"))
                ),
                actual
            )
        }

        @Test
        @DisplayName("集計単位に集計結果が１つのとき")
        fun isSingleSummaryCo() {
            val actual = listOf(TotalSummaryCo(100, 200, BigDecimal("300")))
                .merge()

            assertEquals(listOf(TotalSummaryCo(100, 200, BigDecimal("300"))), actual)
        }

        @Test
        @DisplayName("リストが空のとき")
        fun isEmptyList() {
            val actual = emptyList<TotalSummaryCo>().merge()

            assertEquals(emptyList<TotalSummaryCo>(), actual)
        }
    }
}
