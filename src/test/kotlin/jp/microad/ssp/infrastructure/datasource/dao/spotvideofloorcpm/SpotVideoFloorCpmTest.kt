package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm

import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate

@DisplayName("SpotVideoFloorCpmのテスト")
private class SpotVideoFloorCpmTest {
    @Nested
    @DisplayName("containsPeriodのテスト")
    inner class ContainsPeriodTest {
        val startDate = LocalDate.of(2024, 1, 15)

        @Nested
        @DisplayName("終了日時の設定なし")
        inner class HasNotEndDateTest {
            val sut = SpotVideoFloorCpm(mock(), mock(), startDate, null, mock())

            @ParameterizedTest
            @ValueSource(strings = ["2024-01-15", "2024-01-16", "2024-12-31"])
            @DisplayName("期間内")
            fun isInPeriod(targetDate: LocalDate) {
                assertTrue(sut.containsPeriod(targetDate))
            }

            @ParameterizedTest
            @ValueSource(strings = ["2024-01-01", "2024-01-14"])
            @DisplayName("期間外")
            fun isNotInPeriod(targetDate: LocalDate) {
                assertFalse(sut.containsPeriod(targetDate))
            }
        }

        @Nested
        @DisplayName("終了日時の設定あり")
        inner class HasEndDateTest {
            val sut = SpotVideoFloorCpm(mock(), mock(), startDate, LocalDate.of(2024, 1, 25), mock())

            @ParameterizedTest
            @ValueSource(strings = ["2024-01-15", "2024-01-16", "2024-01-24", "2024-01-25"])
            @DisplayName("期間内")
            fun isInPeriod(targetDate: LocalDate) {
                assertTrue(sut.containsPeriod(targetDate))
            }

            @ParameterizedTest
            @ValueSource(strings = ["2024-01-01", "2024-01-14", "2024-01-26", "2024-12-31"])
            @DisplayName("期間外")
            fun isNotInPeriod(targetDate: LocalDate) {
                assertFalse(sut.containsPeriod(targetDate))
            }
        }
    }
}
