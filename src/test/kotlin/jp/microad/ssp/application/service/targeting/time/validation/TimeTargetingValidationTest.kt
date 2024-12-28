package jp.mangaka.ssp.application.service.targeting.time.validation

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.targeting.time.validation.BasicSettingValidation.BasicSettingCreateValidation
import jp.mangaka.ssp.application.service.targeting.time.validation.BasicSettingValidation.BasicSettingEditValidation
import jp.mangaka.ssp.application.service.targeting.time.validation.TimeTargetingValidation.TimeTargetingCreateValidation
import jp.mangaka.ssp.application.service.targeting.time.validation.TimeTargetingValidation.TimeTargetingEditValidation
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod.DayType
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingEditForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm.DayTypePeriodDetailForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingEditForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalTime

@DisplayName("TimeTargetingValidationのテスト")
private class TimeTargetingValidationTest {
    companion object {
        val timeTargetingId = TimeTargetingId(1)
    }

    val validator = Validation.buildDefaultValidatorFactory().validator!!

    @Nested
    @DisplayName("TimeTargetingCreateValidationのテスト")
    inner class TimeTargetingCreateValidationTest {
        @Nested
        @DisplayName("basicのテスト")
        inner class BasicTest {
            @Test
            @DisplayName("@Validが反応しているか")
            fun isValid() {
                val basic = BasicSettingCreateValidation(null, null, mock())

                validator.validate(TimeTargetingCreateValidation(basic, mock(), emptyList())).run {
                    assertTrue(any { it.propertyPath.toString().startsWith("basic.") })
                }
            }
        }

        @Nested
        @DisplayName("dayTypePeriodsのテスト")
        inner class DayTypePeriodsTest {
            @Test
            @DisplayName("@Validが反応しているか")
            fun isValid() {
                val basic = BasicSettingCreateValidation(null, null, mock())
                val dayTypePeriods = DayTypePeriodsSettingValidation(emptyMap())

                validator.validate(TimeTargetingCreateValidation(basic, dayTypePeriods, emptyList())).run {
                    assertTrue(any { it.propertyPath.toString().startsWith("dayTypePeriods.") })
                }
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("structIdsのテスト")
        inner class StructIdsTest {
            @Test
            @DisplayName("他のタイムターゲティングに紐づくストラクトが含まれている")
            fun isContainsOtherTargetingStruct() {
                val basic = BasicSettingCreateValidation(null, null, mock())
                val structs = listOf(null, null, 1, null).map { struct(it) }

                validator.validate(TimeTargetingCreateValidation(basic, mock(), structs)).run {
                    assertTrue(any { it.propertyPath.toString() == "structIds" })
                }
            }

            @ParameterizedTest
            @MethodSource("correctParams")
            @DisplayName("正常")
            fun isCorrect(structs: List<StructCo>) {
                val basic = BasicSettingCreateValidation(null, null, mock())

                validator.validate(TimeTargetingCreateValidation(basic, mock(), structs)).run {
                    assertTrue(none { it.propertyPath.toString() == "structIds" })
                }
            }

            private fun correctParams() = listOf(
                emptyList(),
                listOf(null, null, null).map { struct(it) }
            )
        }

        @Nested
        @DisplayName("ファクトリ関数のテスト")
        inner class FactoryTest {
            val basicForm: BasicSettingCreateForm = mock {
                on { this.timeTargetingName } doReturn null
            }
            val dayTypePeriodDetailForms: List<DayTypePeriodDetailForm> = listOf(
                dayTypeDetailForm(DayType.mon, "23:59", "23:59"),
                dayTypeDetailForm(DayType.tue, "00:00", "00:59"),
                dayTypeDetailForm(DayType.tue, "01:00", "01:59")
            )
            val dayTypePeriodsForm: DayTypePeriodsSettingForm = mock {
                on { this.dayTypePeriodDetails } doReturn dayTypePeriodDetailForms
            }
            val form: TimeTargetingCreateForm = mock {
                on { this.basic } doReturn basicForm
                on { this.dayTypePeriods } doReturn dayTypePeriodsForm
            }
            val structs: List<StructCo> = listOf(null, null, 1).map { struct(it) }

            @Test
            @DisplayName("正常")
            fun isCorrect() {
                validator.validate(TimeTargetingCreateValidation.of(form, mock(), structs)).run {
                    assertTrue(any { it.propertyPath.toString() == "basic.timeTargetingName" })
                    assertTrue(any { it.propertyPath.toString() == "dayTypePeriods.dayTypeDetails[mon].periods" })
                    assertTrue(any { it.propertyPath.toString() == "structIds" })
                }
            }
        }
    }

    @Nested
    @DisplayName("TimeTargetingEditValidationのテスト")
    inner class TimeTargetingEditValidationTest {
        @Nested
        @DisplayName("basicのテスト")
        inner class BasicTest {
            @Test
            @DisplayName("@Validが反応しているか")
            fun isValid() {
                val basic = BasicSettingEditValidation(null, null, mock(), TimeTargetingStatus.active, emptyList())

                validator.validate(TimeTargetingEditValidation(basic, mock(), emptyList(), timeTargetingId)).run {
                    assertTrue(any { it.propertyPath.toString().startsWith("basic.") })
                }
            }
        }

        @Nested
        @DisplayName("dayTypePeriodsのテスト")
        inner class DayTypePeriodsTest {
            @Test
            @DisplayName("@Validが反応しているか")
            fun isValid() {
                val basic = BasicSettingEditValidation(null, null, mock(), TimeTargetingStatus.active, emptyList())
                val dayTypePeriods = DayTypePeriodsSettingValidation(emptyMap())

                validator
                    .validate(TimeTargetingEditValidation(basic, dayTypePeriods, emptyList(), timeTargetingId))
                    .run { assertTrue(any { it.propertyPath.toString().startsWith("dayTypePeriods.") }) }
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("structIdsのテスト")
        inner class StructIdsTest {
            @Test
            @DisplayName("他のタイムターゲティングに紐づくストラクトが含まれている")
            fun isContainsOtherTargetingStruct() {
                val basic = BasicSettingEditValidation(null, null, mock(), TimeTargetingStatus.active, emptyList())
                val structs = listOf(null, null, 1, 1, 2).map { struct(it) }

                validator.validate(TimeTargetingEditValidation(basic, mock(), structs, timeTargetingId)).run {
                    assertTrue(any { it.propertyPath.toString() == "structIds" })
                }
            }

            @ParameterizedTest
            @MethodSource("correctParams")
            @DisplayName("正常")
            fun isCorrect(structs: List<StructCo>) {
                val basic = BasicSettingEditValidation(null, null, mock(), TimeTargetingStatus.active, emptyList())

                validator.validate(TimeTargetingEditValidation(basic, mock(), structs, timeTargetingId)).run {
                    assertTrue(none { it.propertyPath.toString() == "structIds" })
                }
            }

            private fun correctParams() = listOf(
                emptyList(),
                listOf(null, null, 1, 1).map { struct(it) }
            )
        }

        @Nested
        @DisplayName("ファクトリ関数のテスト")
        inner class FactoryTest {
            val basicForm: BasicSettingEditForm = mock {
                on { this.timeTargetingName } doReturn null
            }
            val dayTypePeriodDetailForms = listOf(
                dayTypeDetailForm(DayType.mon, "23:59", "23:59"),
                dayTypeDetailForm(DayType.tue, "00:00", "00:59"),
                dayTypeDetailForm(DayType.tue, "01:00", "01:59")
            )
            val dayTypePeriodsForm: DayTypePeriodsSettingForm = mock {
                on { this.dayTypePeriodDetails } doReturn dayTypePeriodDetailForms
            }
            val form: TimeTargetingEditForm = mock {
                on { this.basic } doReturn basicForm
                on { this.dayTypePeriods } doReturn dayTypePeriodsForm
            }
            val timeTargeting: TimeTargeting = mock {
                on { this.timeTargetingId } doReturn timeTargetingId
                on { this.timeTargetingStatus } doReturn TimeTargetingStatus.active
            }
            val structs: List<StructCo> = listOf(null, null, 1, 2).map { struct(it) }

            @Test
            @DisplayName("遷移できないステータス変更のとき")
            fun isInvalidStatusChange() {
                doReturn(TimeTargetingStatus.deleted).whenever(basicForm).timeTargetingStatus

                assertThrows<CompassManagerException> {
                    TimeTargetingEditValidation.of(form, timeTargeting, mock(), structs)
                }
            }

            @Test
            @DisplayName("正常")
            fun isCorrect() {
                doReturn(TimeTargetingStatus.archive).whenever(basicForm).timeTargetingStatus

                validator.validate(TimeTargetingEditValidation.of(form, timeTargeting, mock(), structs)).run {
                    assertTrue(any { it.propertyPath.toString() == "basic.timeTargetingName" })
                    assertTrue(any { it.propertyPath.toString() == "dayTypePeriods.dayTypeDetails[mon].periods" })
                    assertTrue(any { it.propertyPath.toString() == "structIds" })
                }
            }
        }
    }

    private fun struct(timeTargetingId: Int?): StructCo = mock {
        on { this.timeTargetingId } doReturn timeTargetingId?.let { TimeTargetingId(it) }
    }

    private fun dayTypeDetailForm(dayType: DayType, startTime: String, endTime: String): DayTypePeriodDetailForm =
        mock {
            on { this.dayType } doReturn dayType
            on { this.startTime } doReturn LocalTime.parse(startTime)
            on { this.endTime } doReturn LocalTime.parse(endTime)
        }
}
