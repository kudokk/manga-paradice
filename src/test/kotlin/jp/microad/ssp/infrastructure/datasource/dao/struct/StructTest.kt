package jp.mangaka.ssp.infrastructure.datasource.dao.struct

import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDateTime

@DisplayName("Structのテスト")
private class StructTest {
    @Nested
    @DisplayName("isNotEndByのテスト")
    inner class IsNotEndByTest {
        val targetDate = LocalDateTime.of(2024, 1, 1, 0, 0, 0)

        @ParameterizedTest
        @ValueSource(longs = [0, 1])
        @DisplayName("対象日時が終了日時を超えていない")
        fun isNotEnded(diff: Long) {
            val sut = sut(targetDate.plusSeconds(diff))

            assertTrue(sut.isNotEndBy(targetDate))
        }

        @Test
        @DisplayName("対象日時が終了日時より後")
        fun isEnded() {
            val sut = sut(targetDate.plusSeconds(-1))

            assertFalse(sut.isNotEndBy(targetDate))
        }

        private fun sut(endDate: LocalDateTime) = StructCo(mock(), "_", mock(), mock(), mock(), mock(), endDate, 0)
    }
}
