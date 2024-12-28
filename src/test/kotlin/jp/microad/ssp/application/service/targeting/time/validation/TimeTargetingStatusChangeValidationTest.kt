package jp.mangaka.ssp.application.service.targeting.time.validation

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jakarta.validation.Validation
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingStatusChangeForm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("TimeTargetingStatusChangeValidationのテスト")
private class TimeTargetingStatusChangeValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @DisplayName("timeTargetingStatusのテスト")
    inner class TimeTargetingStatusTest {
        val activeStructs = listOf(StructStatus.active, StructStatus.stop, StructStatus.pause).map { struct(it) }
        val inactiveStructs = List(2) { struct(StructStatus.archive) }
        val allStructs = activeStructs + inactiveStructs

        @Nested
        @DisplayName("アクティブなストラクトに紐づくとき")
        inner class RelayActiveStructTest {
            val structs = StructStatus.entries.map { struct(it) }

            @ParameterizedTest
            @CsvSource(
                value = [
                    "active,archive",
                    "archive,deleted"
                ]
            )
            @DisplayName("アーカイブ・削除済みに変更したとき")
            fun isChangeArchiveOrDeleted(currentStatus: TimeTargetingStatus, nextStatus: TimeTargetingStatus) {
                validator.validate(TimeTargetingStatusChangeValidation(currentStatus, nextStatus, structs)).run {
                    assertTrue(any { it.propertyPath.toString() == "timeTargetingStatus" })
                }
            }

            @ParameterizedTest
            @CsvSource(
                value = [
                    // 変更なし
                    "active,active",
                    "archive,archive",
                    // 変更あり
                    "archive,active"
                ]
            )
            @DisplayName("正常")
            fun isValid(currentStatus: TimeTargetingStatus, nextStatus: TimeTargetingStatus) {
                validator.validate(TimeTargetingStatusChangeValidation(currentStatus, nextStatus, structs)).run {
                    assertTrue(none { it.propertyPath.toString() == "timeTargetingStatus" })
                }
            }
        }

        @Nested
        @DisplayName("アクティブなストラクトに紐づかないとき")
        inner class RelayOnlyInactiveStructTest {
            val structs = List(3) { struct(StructStatus.archive) }

            @ParameterizedTest
            @CsvSource(
                value = [
                    // 変更なし
                    "active,active",
                    "archive,archive",
                    // 変更あり
                    "active,archive",
                    "archive,active",
                    "archive,deleted"
                ]
            )
            @DisplayName("正常")
            fun isValid(currentStatus: TimeTargetingStatus, nextStatus: TimeTargetingStatus) {
                validator.validate(TimeTargetingStatusChangeValidation(currentStatus, nextStatus, structs)).run {
                    assertTrue(none { it.propertyPath.toString() == "timeTargetingStatus" })
                }
            }
        }
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val form: TimeTargetingStatusChangeForm = mock {
                on { this.timeTargetingStatus } doReturn TimeTargetingStatus.active
            }
            val timeTargeting: TimeTargeting = mock {
                on { this.timeTargetingStatus } doReturn TimeTargetingStatus.archive
            }
            val structs: List<StructCo> = mock()

            val actual = TimeTargetingStatusChangeValidation.of(form, timeTargeting, structs)

            assertEquals(
                TimeTargetingStatusChangeValidation(
                    TimeTargetingStatus.archive,
                    TimeTargetingStatus.active,
                    structs
                ),
                actual
            )
        }
    }

    private fun struct(status: StructStatus): StructCo = mock {
        on { this.structStatus } doReturn status
    }
}
