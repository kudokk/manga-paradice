package jp.mangaka.ssp.infrastructure.datasource.dao.struct

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDateTime

@DisplayName("StructCoのテスト")
private class StructCoTest {
    @Nested
    @DisplayName("isNotEndByのテスト")
    inner class IsNotEndByTest {
        val targetDate = LocalDateTime.of(2024, 1, 1, 0, 0, 0)!!

        @ParameterizedTest
        @ValueSource(longs = [0, 1])
        @DisplayName("対象日時が終了日時を超えていない")
        fun isNotEnded(diff: Long) {
            val sut = structCo(targetDate.plusSeconds(diff))

            assertTrue(sut.isNotEndBy(targetDate))
        }

        @Test
        @DisplayName("対象日時が終了日時より後")
        fun isEnded() {
            val sut = structCo(targetDate.plusSeconds(-1))

            assertFalse(sut.isNotEndBy(targetDate))
        }

        private fun structCo(endDate: LocalDateTime) = StructCo(mock(), "_", mock(), mock(), mock(), mock(), endDate, 0)
    }

    @Nested
    @DisplayName("isResellerのテスト")
    inner class IsResellerTest {
        @Test
        @DisplayName("リセラー対象のとき")
        fun isReseller() {
            assertTrue(structCo(1).isReseller())
        }

        @Test
        @DisplayName("リセラー対象外のとき")
        fun isNotReseller() {
            assertFalse(structCo(0).isReseller())
        }

        private fun structCo(resellerFlag: Int) = StructCo(
            mock(), "", mock(), StructStatus.active, mock(), LocalDateTime.MIN, LocalDateTime.MAX, resellerFlag
        )
    }
}
