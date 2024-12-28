package jp.mangaka.ssp.application.service.targeting.time

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.compassstruct.CompassStructDao
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargetingDao
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargetingInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargetingUpdate
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod.DayType
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriodDao
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriodDelete
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriodInsert
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm.DayTypePeriodDetailForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingEditForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingStatusChangeForm
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalTime
import io.mockk.verify as verifyK

@DisplayName("TimeTargetingPersistHelperのテスト")
private class TimeTargetingPersistHelperTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val timeTargetingId = TimeTargetingId(2)
    }

    val compassStructDao: CompassStructDao = mock()
    val timeTargetingDao: TimeTargetingDao = mock()
    val timeTargetingDayTypePeriodDao: TimeTargetingDayTypePeriodDao = mock()

    val sut = spy(TimeTargetingPersistHelper(compassStructDao, timeTargetingDao, timeTargetingDayTypePeriodDao))

    @Nested
    @DisplayName("createのテスト")
    inner class CreateTest {
        val structIds = listOf(1, 2, 3).map { StructId(it) }
        val basicForm: BasicSettingCreateForm = mock()
        val dayTypePeriodDetailForms: List<DayTypePeriodDetailForm> = mock()
        val dayTypePeriodsForm: DayTypePeriodsSettingForm = mock {
            on { this.dayTypePeriodDetails } doReturn dayTypePeriodDetailForms
        }
        val form: TimeTargetingCreateForm = mock {
            on { this.basic } doReturn basicForm
            on { this.dayTypePeriods } doReturn dayTypePeriodsForm
            on { this.structIds } doReturn structIds
        }
        val timeTargetingInsert: TimeTargetingInsert = mock()
        val timeTargetingDayTypePeriodInserts: List<TimeTargetingDayTypePeriodInsert> = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(TimeTargetingInsert, TimeTargetingDayTypePeriodInsert)
            every { TimeTargetingInsert.of(any(), any()) } returns timeTargetingInsert
            every { TimeTargetingDayTypePeriodInsert.of(any(), any()) } returns timeTargetingDayTypePeriodInserts

            doReturn(timeTargetingId).whenever(timeTargetingDao).insert(any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = sut.create(coAccountId, form)

            assertEquals(timeTargetingId, actual)
            verify(timeTargetingDao, times(1)).insert(timeTargetingInsert)
            verify(timeTargetingDayTypePeriodDao, times(1)).inserts(timeTargetingDayTypePeriodInserts)
            verify(compassStructDao, times(1)).updateTimeTargetingId(structIds, timeTargetingId)
            verifyK { TimeTargetingInsert.of(coAccountId, form) }
            verifyK { TimeTargetingDayTypePeriodInsert.of(timeTargetingId, dayTypePeriodDetailForms) }
        }
    }

    @Nested
    @DisplayName("editのテスト")
    inner class EditTest {
        val dayTypePeriodDetails: List<DayTypePeriodDetailForm> = mock()
        val dayTypePeriods: DayTypePeriodsSettingForm = mock {
            on { this.dayTypePeriodDetails } doReturn dayTypePeriodDetails
        }
        val structIds = listOf(1, 2, 3).map { StructId(it) }
        val form: TimeTargetingEditForm = mock {
            on { this.dayTypePeriods } doReturn dayTypePeriods
            on { this.structIds } doReturn structIds
        }
        val currentDayTypePeriods: Collection<TimeTargetingDayTypePeriod> = mock()
        val currentStructs: Collection<StructCo> = mock()
        val timeTargetingUpdate: TimeTargetingUpdate = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(TimeTargetingUpdate)
            every { TimeTargetingUpdate.of(any(), any()) } returns timeTargetingUpdate

            doNothing().whenever(sut).editTimeTargetingDayTypePeriods(any(), any(), any())
            doNothing().whenever(sut).editCompassStructs(any(), any(), any())
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            sut.edit(timeTargetingId, form, currentDayTypePeriods, currentStructs)

            verifyK { TimeTargetingUpdate.of(timeTargetingId, form) }
            verify(timeTargetingDao, times(1)).update(timeTargetingUpdate)
            verify(sut, times(1))
                .editTimeTargetingDayTypePeriods(timeTargetingId, dayTypePeriodDetails, currentDayTypePeriods)
            verify(sut, times(1)).editCompassStructs(timeTargetingId, structIds, currentStructs)
        }
    }

    @Nested
    @DisplayName("editTimeTargetingStatusのテスト")
    inner class EditTimeTargetingStatusTest {
        val form: TimeTargetingStatusChangeForm = mock {
            on { this.timeTargetingStatus } doReturn TimeTargetingStatus.archive
        }

        @Test
        fun isCorrect() {
            sut.editTimeTargetingStatus(timeTargetingId, form)

            verify(timeTargetingDao, times(1)).updateStatus(timeTargetingId, TimeTargetingStatus.archive)
        }
    }

    @Nested
    @DisplayName("editTimeTargetingDayTypePeriodsのテスト")
    inner class EditTimeTargetingDayTypePeriodsTest {
        val currentDayTypePeriods = listOf(
            timeTargetingDayTypePeriod(DayType.mon, "00:00", "23:59"),
            timeTargetingDayTypePeriod(DayType.tue, "00:00", "00:59"),
            timeTargetingDayTypePeriod(DayType.tue, "01:00", "01:59"),
            timeTargetingDayTypePeriod(DayType.wed, "00:00", "00:29"),
            timeTargetingDayTypePeriod(DayType.wed, "00:30", "00:59"),
            timeTargetingDayTypePeriod(DayType.wed, "01:00", "01:29")
        )

        @Test
        @DisplayName("追加のみ")
        fun isAddOnly() {
            val forms = listOf(
                // 既存
                dayTypePeriodDetailForm(DayType.mon, "00:00", "23:59"),
                dayTypePeriodDetailForm(DayType.tue, "00:00", "00:59"),
                dayTypePeriodDetailForm(DayType.tue, "01:00", "01:59"),
                dayTypePeriodDetailForm(DayType.wed, "00:00", "00:29"),
                dayTypePeriodDetailForm(DayType.wed, "00:30", "00:59"),
                dayTypePeriodDetailForm(DayType.wed, "01:00", "01:29"),
                // 新規
                dayTypePeriodDetailForm(DayType.wed, "01:30", "01:59"),
                dayTypePeriodDetailForm(DayType.thu, "00:00", "23:59")
            )

            sut.editTimeTargetingDayTypePeriods(timeTargetingId, forms, currentDayTypePeriods)

            verify(timeTargetingDayTypePeriodDao, times(1)).deletes(emptyList())
            verify(timeTargetingDayTypePeriodDao, times(1)).inserts(
                listOf(
                    timeTargetingDayTypePeriodInsert(DayType.wed, "01:30", "01:59"),
                    timeTargetingDayTypePeriodInsert(DayType.thu, "00:00", "23:59"),
                )
            )
        }

        @Test
        @DisplayName("削除のみ")
        fun isDeleteOnly() {
            val forms = listOf(
                // 既存（削除あり）
                dayTypePeriodDetailForm(DayType.mon, "00:00", "23:59"),
                dayTypePeriodDetailForm(DayType.tue, "00:00", "00:59"),
                dayTypePeriodDetailForm(DayType.wed, "00:30", "00:59"),
                // 新規
                dayTypePeriodDetailForm(DayType.wed, "01:30", "01:59"),
                dayTypePeriodDetailForm(DayType.thu, "00:00", "23:59")
            )

            sut.editTimeTargetingDayTypePeriods(timeTargetingId, forms, currentDayTypePeriods)

            verify(timeTargetingDayTypePeriodDao, times(1)).deletes(
                listOf(
                    timeTargetingDayTypePeriodDelete(DayType.tue, "01:00", "01:59"),
                    timeTargetingDayTypePeriodDelete(DayType.wed, "00:00", "00:29"),
                    timeTargetingDayTypePeriodDelete(DayType.wed, "01:00", "01:29"),
                )
            )
            verify(timeTargetingDayTypePeriodDao, times(1)).inserts(
                listOf(
                    timeTargetingDayTypePeriodInsert(DayType.wed, "01:30", "01:59"),
                    timeTargetingDayTypePeriodInsert(DayType.thu, "00:00", "23:59")
                )
            )
        }

        @Test
        @DisplayName("追加・削除混合")
        fun isAddAndDelete() {
            val forms = listOf(
                dayTypePeriodDetailForm(DayType.mon, "00:00", "23:59"),
                dayTypePeriodDetailForm(DayType.tue, "00:00", "00:59"),
                dayTypePeriodDetailForm(DayType.wed, "00:30", "00:59")
            )

            sut.editTimeTargetingDayTypePeriods(timeTargetingId, forms, currentDayTypePeriods)

            verify(timeTargetingDayTypePeriodDao, times(1)).deletes(
                listOf(
                    timeTargetingDayTypePeriodDelete(DayType.tue, "01:00", "01:59"),
                    timeTargetingDayTypePeriodDelete(DayType.wed, "00:00", "00:29"),
                    timeTargetingDayTypePeriodDelete(DayType.wed, "01:00", "01:29"),
                )
            )
            verify(timeTargetingDayTypePeriodDao, times(1)).inserts(emptyList())
        }
    }

    @Nested
    @DisplayName("editCompassStructsのテスト")
    inner class EditCompassStructsTest {
        val currentStructs: List<StructCo> = listOf(1, 2, 3, 4, 5, 6).map { id ->
            mock {
                on { this.structId } doReturn StructId(id)
            }
        }

        @Test
        @DisplayName("新規のみ")
        fun isAddOnly() {
            sut.editCompassStructs(
                timeTargetingId,
                listOf(1, 2, 3, 4, 5, 6, 7, 8).map { StructId(it) },
                currentStructs
            )

            verify(compassStructDao, times(1)).updateTimeTargetingId(emptyList(), null)
            verify(compassStructDao, times(1))
                .updateTimeTargetingId(listOf(7, 8).map { StructId(it) }, timeTargetingId)
        }

        @Test
        @DisplayName("削除のみ")
        fun isDeleteOnly() {
            sut.editCompassStructs(
                timeTargetingId,
                listOf(1, 4, 5).map { StructId(it) },
                currentStructs
            )

            verify(compassStructDao, times(1)).updateTimeTargetingId(listOf(2, 3, 6).map { StructId(it) }, null)
            verify(compassStructDao, times(1)).updateTimeTargetingId(emptyList(), timeTargetingId)
        }

        @Test
        @DisplayName("新規・削除混合")
        fun isAddAndDeleteOnly() {
            sut.editCompassStructs(
                timeTargetingId,
                listOf(1, 4, 5, 7, 8).map { StructId(it) },
                currentStructs
            )

            verify(compassStructDao, times(1)).updateTimeTargetingId(listOf(2, 3, 6).map { StructId(it) }, null)
            verify(compassStructDao, times(1))
                .updateTimeTargetingId(listOf(7, 8).map { StructId(it) }, timeTargetingId)
        }
    }

    private fun dayTypePeriodDetailForm(
        dayType: DayType,
        startTime: String,
        endTime: String
    ): DayTypePeriodDetailForm = mock {
        on { this.dayType } doReturn dayType
        on { this.startTime } doReturn LocalTime.parse(startTime)
        on { this.endTime } doReturn LocalTime.parse(endTime)
    }

    private fun timeTargetingDayTypePeriod(
        dayType: DayType,
        startTime: String,
        endTime: String
    ): TimeTargetingDayTypePeriod = mock {
        on { this.timeTargetingId } doReturn timeTargetingId
        on { this.dayType } doReturn dayType
        on { this.startTime } doReturn LocalTime.parse(startTime)
        on { this.endTime } doReturn LocalTime.parse(endTime)
    }

    private fun timeTargetingDayTypePeriodInsert(
        dayType: DayType,
        startTime: String,
        endTime: String
    ) = TimeTargetingDayTypePeriodInsert(
        timeTargetingId,
        dayType,
        LocalTime.parse(startTime),
        LocalTime.parse(endTime)
    )

    private fun timeTargetingDayTypePeriodDelete(
        dayType: DayType,
        startTime: String,
        endTime: String
    ) = TimeTargetingDayTypePeriodDelete(
        timeTargetingId,
        dayType,
        LocalTime.parse(startTime),
        LocalTime.parse(endTime)
    )
}
