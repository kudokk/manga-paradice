package jp.mangaka.ssp.infrastructure.datasource.dao.reservetotallimitimpression

import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate

@DisplayName("ReserveTotalLimitImpressionのテスト")
private class ReserveTotalLimitImpressionTest {
    @Nested
    @DisplayName("isNotEndByのテスト")
    inner class IsNotEndByTest {
        val targetDate = LocalDate.of(2024, 1, 1)

        @Test
        @DisplayName("終了日時が未設定")
        fun isNoEndDate() {
            val sut = sut(null)

            assertTrue(sut.isNotEndBy(targetDate))
        }

        @ParameterizedTest
        @ValueSource(longs = [0, 1])
        @DisplayName("対象日時が終了日時を超えていない")
        fun isNotEnded(diff: Long) {
            val sut = sut(targetDate.plusDays(diff))

            assertTrue(sut.isNotEndBy(targetDate))
        }

        @Test
        @DisplayName("対象日時が終了日時を超えている")
        fun isEnded() {
            val sut = sut(targetDate.plusDays(-1))

            assertFalse(sut.isNotEndBy(targetDate))
        }

        private fun sut(date: LocalDate?) = ReserveTotalLimitImpression(1, mock(), date, 1, mock())
    }
}
