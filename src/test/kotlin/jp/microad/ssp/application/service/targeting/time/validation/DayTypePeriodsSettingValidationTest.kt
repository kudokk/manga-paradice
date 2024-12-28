package jp.mangaka.ssp.application.service.targeting.time.validation

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.targeting.time.validation.DayTypePeriodsSettingValidation.DayTypeDetailValidation
import jp.mangaka.ssp.application.service.targeting.time.validation.DayTypePeriodsSettingValidation.DayTypeDetailValidation.Period
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod.DayType
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm.DayTypePeriodDetailForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalTime

@DisplayName("DayTypePeriodsSettingValidationのテスト")
private class DayTypePeriodsSettingValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator!!

    @Nested
    @DisplayName("dayTypeDetailsのテスト")
    inner class DayTypeDetailsTest {
        @Test
        @DisplayName("空のとき")
        fun isEmpty() {
            validator.validate(DayTypePeriodsSettingValidation(emptyMap())).run {
                assertTrue(any { it.propertyPath.toString() == "dayTypeDetails" })
            }
        }

        @Test
        @DisplayName("@Validは機能しているか")
        fun isValid() {
            val detail = DayTypeDetailValidation(listOf(period("01:00", "00:00")))
            val validation = DayTypePeriodsSettingValidation(mapOf(DayType.mon to detail))

            validator.validate(validation).run {
                assertTrue(none { it.propertyPath.toString() == "dayTypeDetails" })
                assertTrue(any { it.propertyPath.toString() == "dayTypeDetails[mon].periods" })
            }
        }
    }

    @Nested
    @DisplayName("DayTypeDetailValidationのテスト")
    inner class DayTypeDetailValidationTest {
        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("Periodsのテスト")
        inner class PeriodsTest {
            @Test
            @DisplayName("逆転した期間が存在するとき")
            fun isContainsInvalidPeriod() {
                validator.validate(DayTypeDetailValidation(listOf(period("01:00", "00:59")))).run {
                    assertTrue(any { it.propertyPath.toString() == "periods" })
                }
            }

            @ParameterizedTest
            @MethodSource("duplicateParams")
            @DisplayName("重複する期間が存在するとき")
            fun isDuplicate(periods: List<Period>) {
                validator.validate(DayTypeDetailValidation(periods)).run {
                    assertTrue(any { it.propertyPath.toString() == "periods" })
                }
            }

            private fun duplicateParams() = listOf(
                // ある期間の開始時刻と別の期間の終了時刻が同じ
                listOf(
                    period("01:59", "02:59"),
                    period("01:00", "01:59"),
                    period("03:00", "03:59"),
                    period("00:00", "00:59")
                ),
                // ある期間内に別の期間が含まれる
                listOf(
                    period("01:30", "02:59"),
                    period("01:00", "01:59"),
                    period("03:00", "03:59"),
                    period("00:00", "00:59")
                )
            )

            @ParameterizedTest
            @MethodSource("correctParams")
            @DisplayName("正常")
            fun isCorrect(periods: List<Period>) {
                validator.validate(DayTypeDetailValidation(periods)).run {
                    assertTrue(none { it.propertyPath.toString() == "periods" })
                }
            }

            private fun correctParams() = listOf(
                listOf(
                    period("00:00", "23:59")
                ),
                listOf(
                    period("00:00", "00:59"),
                    period("01:00", "01:59"),
                    period("02:00", "02:59")
                )
            )
        }

        @Nested
        @DisplayName("ファクトリ関数のテスト")
        inner class FactoryTest {
            @BeforeEach
            fun beforeEach() {
                mockkObject(DayTypePeriodsSettingValidation)
                every { DayTypePeriodsSettingValidation.checkHoliday(any()) } returns Unit
            }

            @AfterEach
            fun afterEach() {
                unmockkAll()
            }

            @Test
            @DisplayName("正常")
            fun isCorrect() {
                val form = DayTypePeriodsSettingForm(
                    true,
                    listOf(
                        dayTypeDetailForm(DayType.mon, "00:00", "01:59"),
                        dayTypeDetailForm(DayType.mon, "02:00", "03:59"),
                        dayTypeDetailForm(DayType.mon, "04:00", "05:59"),
                        dayTypeDetailForm(DayType.tue, "18:30", "18:29"),
                        dayTypeDetailForm(DayType.tue, "19:30", "20:29"),
                        dayTypeDetailForm(DayType.wed, "00:00", "23:59")
                    )
                )

                validator.validate(DayTypePeriodsSettingValidation.of(form)).run {
                    assertTrue(any { it.propertyPath.toString() == "dayTypeDetails[tue].periods" })
                }
            }
        }

        @Nested
        @DisplayName("checkHolidayのテスト")
        inner class CheckHolidayTest {
            val notHolidayDetailForms = listOf(
                dayTypeDetailForm(DayType.mon, "00:00", "23:59"),
                dayTypeDetailForm(DayType.tue, "00:00", "23:59")
            )
            val holidayDetailForms = listOf(
                dayTypeDetailForm(DayType.hol, "00:00", "00:59"),
                dayTypeDetailForm(DayType.hol, "01:00", "01:59")
            )
            val form: DayTypePeriodsSettingForm = mock()

            @Nested
            @DisplayName("祝日判断有効のとき")
            inner class ActiveHolidayTest {
                @BeforeEach
                fun beforeEach() {
                    doReturn(true).whenever(form).isActiveHoliday
                }

                @Test
                @DisplayName("正常")
                fun isCorrect() {
                    assertDoesNotThrow { DayTypePeriodsSettingValidation.checkHoliday(form) }
                }
            }

            @Nested
            @DisplayName("祝日判断無効のとき")
            inner class InactiveHolidayTest {
                @BeforeEach
                fun beforeEach() {
                    doReturn(false).whenever(form).isActiveHoliday
                }

                @Test
                @DisplayName("祝日の時間帯が設定されていないとき")
                fun isValid() {
                    doReturn(notHolidayDetailForms).whenever(form).dayTypePeriodDetails

                    assertDoesNotThrow { DayTypePeriodsSettingValidation.checkHoliday(form) }
                }

                @Test
                @DisplayName("祝日の時間帯が設定されているとき")
                fun isInvalid() {
                    doReturn(notHolidayDetailForms + holidayDetailForms).whenever(form).dayTypePeriodDetails

                    assertThrows<CompassManagerException> { DayTypePeriodsSettingValidation.checkHoliday(form) }
                }
            }
        }
    }

    private fun dayTypeDetailForm(dayType: DayType, startTime: String, endTime: String) =
        DayTypePeriodDetailForm(dayType, LocalTime.parse(startTime), LocalTime.parse(endTime))

    private fun period(startTime: String, endTime: String) =
        Period(LocalTime.parse(startTime), LocalTime.parse(endTime))
}
