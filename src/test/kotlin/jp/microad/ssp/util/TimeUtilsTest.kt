package jp.mangaka.ssp.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

private class TimeUtilsTest {
    @Nested
    @DisplayName("fixedDateTimeのテスト")
    inner class FixedDateTimeTest {
        val dateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0)

        @ParameterizedTest
        @CsvSource(
            value = [
                "0.0,2024-01-01T00:00:00",
                "3.5,2024-01-01T03:30:00",
                "-48.0,2023-12-30T00:00:00"
            ]
        )
        @DisplayName("正常")
        fun isCorrect(difference: BigDecimal, expected: LocalDateTime) {
            assertEquals(
                expected,
                TimeUtils.fixedDateTime(dateTime, difference)
            )
        }
    }

    @Test
    @DisplayName("fixedDateのテスト")
    fun testFixedDate() {
        assertEquals(
            LocalDate.of(2024, 1, 2),
            TimeUtils.fixedDate(LocalDateTime.of(2024, 1, 1, 0, 0, 0), BigDecimal("24.0"))
        )
    }
}
