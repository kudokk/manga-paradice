package jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import java.time.LocalDateTime

@DisplayName("TimeTargetingのテスト")
private class TimeTargetingTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val timeTargetingId = TimeTargetingId(2)
        val countryId = CountryId(3)
    }

    @Nested
    @DisplayName("コンストラクタのテスト")
    inner class ConstructorTest {
        @Test
        @DisplayName("国IDがnullのとき")
        fun isNullCountryId() {
            assertThrows<CompassManagerException> {
                TimeTargeting(
                    timeTargetingId,
                    coAccountId,
                    "time1",
                    TimeTargetingStatus.active,
                    null,
                    true,
                    null,
                    LocalDateTime.MIN
                )
            }
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            assertDoesNotThrow {
                TimeTargeting(
                    timeTargetingId,
                    coAccountId,
                    "time1",
                    TimeTargetingStatus.active,
                    countryId,
                    true,
                    null,
                    LocalDateTime.MIN
                )
            }
        }
    }

    @Nested
    @DisplayName("TimeTargetingStatusのテスト")
    inner class TimeTargetingStatusTest {
        @Nested
        @DisplayName("isAllowedChangeのテスト")
        inner class IsAllowedChangeTest {
            @ParameterizedTest
            @EnumSource(value = TimeTargetingStatus::class)
            @DisplayName("未変更のとき")
            fun isNoChange(status: TimeTargetingStatus) {
                assertTrue(status.isAllowedChange(status))
            }

            @ParameterizedTest
            @CsvSource(
                value = [
                    "active,deleted",
                    "deleted,active",
                    "deleted,archive",
                ]
            )
            @DisplayName("許可されていないステータス遷移のとき")
            fun isInvalid(current: TimeTargetingStatus, next: TimeTargetingStatus) {
                assertFalse(current.isAllowedChange(next))
            }

            @ParameterizedTest
            @CsvSource(
                value = [
                    "active,archive",
                    "archive,active",
                    "archive,deleted",
                ]
            )
            @DisplayName("許可されているステータス遷移のとき")
            fun isValid(current: TimeTargetingStatus, next: TimeTargetingStatus) {
                assertTrue(current.isAllowedChange(next))
            }
        }

        @Nested
        @DisplayName("checkAllowedChangeのテスト")
        inner class CheckAllowedChangeTest {
            @Test
            @DisplayName("許可されていないステータス遷移のとき")
            fun isInvalid() {
                assertThrows<CompassManagerException> {
                    TimeTargetingStatus.active.checkAllowedChange(TimeTargetingStatus.deleted)
                }
            }

            @Test
            @DisplayName("許可されているステータス遷移のとき")
            fun isValid() {
                assertDoesNotThrow {
                    TimeTargetingStatus.active.checkAllowedChange(TimeTargetingStatus.archive)
                }
            }
        }
    }
}
