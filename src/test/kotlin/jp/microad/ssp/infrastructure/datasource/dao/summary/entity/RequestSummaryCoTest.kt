package jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.SpotRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.TotalRequestSummaryCo
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("RequestSummaryCoのテスト")
private class RequestSummaryCoTest {
    companion object {
        val spotId10 = SpotId(10)
        val spotId11 = SpotId(11)
        val spotId12 = SpotId(12)
    }

    @Nested
    @DisplayName("TotalRequestSummaryCoのテスト")
    inner class TotalRequestSummaryCoTest {
        @Nested
        @DisplayName("addのテスト")
        inner class AddTest {
            @Test
            @DisplayName("正常")
            fun isCorrect() {
                val actual = TotalRequestSummaryCo(100).add(TotalRequestSummaryCo(200))

                assertEquals(TotalRequestSummaryCo(300), actual)
            }
        }
    }

    @Nested
    @DisplayName("SpotRequestSummaryCoのテスト")
    inner class SpotRequestSummaryCoTest {
        @Nested
        @DisplayName("addのテスト")
        inner class AddTest {
            @Test
            @DisplayName("正常")
            fun isCorrect() {
                val actual = SpotRequestSummaryCo(spotId10, 100)
                    .add(SpotRequestSummaryCo(spotId10, 200))

                assertEquals(SpotRequestSummaryCo(spotId10, 300), actual)
            }

            @Test
            @DisplayName("異なる集計対象")
            fun isOtherSummaryTarget() {
                assertThrows<CompassManagerException> {
                    SpotRequestSummaryCo(spotId10, 100)
                        .add(SpotRequestSummaryCo(spotId11, 200))
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
                TotalRequestSummaryCo(100),
                TotalRequestSummaryCo(200)
            ).merge()

            assertEquals(listOf(TotalRequestSummaryCo(300)), actual)
        }

        @Test
        @DisplayName("広告枠単位のテスト")
        fun isSpot() {
            val actual = listOf(
                SpotRequestSummaryCo(spotId10, 100),
                SpotRequestSummaryCo(spotId10, 200),
                SpotRequestSummaryCo(spotId11, 300),
                SpotRequestSummaryCo(spotId11, 400),
                SpotRequestSummaryCo(spotId12, 500),
                SpotRequestSummaryCo(spotId12, 600),
            ).merge()

            assertEquals(
                listOf(
                    SpotRequestSummaryCo(spotId10, 300),
                    SpotRequestSummaryCo(spotId11, 700),
                    SpotRequestSummaryCo(spotId12, 1100)
                ),
                actual
            )
        }

        @Test
        @DisplayName("集計単位に集計結果が１つのとき")
        fun isSingleRequestSummaryCo() {
            val actual = listOf(TotalRequestSummaryCo(100))
                .merge()

            assertEquals(listOf(TotalRequestSummaryCo(100)), actual)
        }

        @Test
        @DisplayName("リストが空のとき")
        fun isEmptyList() {
            val actual = emptyList<TotalRequestSummaryCo>().merge()

            assertEquals(emptyList<TotalRequestSummaryCo>(), actual)
        }
    }
}
