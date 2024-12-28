package jp.mangaka.ssp.application.service.targeting.time

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import jp.mangaka.ssp.application.service.campaign.CampaignGetWithCheckHelper
import jp.mangaka.ssp.application.service.country.CountryGetWithCheckHelper
import jp.mangaka.ssp.application.service.struct.StructGetWithCheckHelper
import jp.mangaka.ssp.application.service.targeting.time.validation.TimeTargetingStatusChangeValidation
import jp.mangaka.ssp.application.service.targeting.time.validation.TimeTargetingValidation.TimeTargetingCreateValidation
import jp.mangaka.ssp.application.service.targeting.time.validation.TimeTargetingValidation.TimeTargetingEditValidation
import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.CampaignStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructDao
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriodDao
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingEditForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingEditForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingStatusChangeForm
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingCheckValue
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingCreateResultView
import jp.mangaka.ssp.util.exception.FormatValidationException
import jp.mangaka.ssp.util.exception.ResourceConflictException
import org.hibernate.validator.internal.engine.path.PathImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import io.mockk.verify as verifyK

@DisplayName("TimeTargetingServiceImplのテスト")
private class TimeTargetingServiceImplTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val timeTargetingId = TimeTargetingId(2)
        val countryId = CountryId(3)
    }

    val validator: Validator = mock()
    val structDao: StructDao = mock()
    val timeTargetingDayTypePeriodDao: TimeTargetingDayTypePeriodDao = mock()
    val campaignGetWithCheckHelper: CampaignGetWithCheckHelper = mock()
    val countryGetWithCheckHelper: CountryGetWithCheckHelper = mock()
    val structGetWithCheckHelper: StructGetWithCheckHelper = mock()
    val timeTargetingGetWithCheckHelper: TimeTargetingGetWithCheckHelper = mock()
    val timeTargetingPersistHelper: TimeTargetingPersistHelper = mock()

    val sut = spy(
        TimeTargetingServiceImpl(
            validator,
            structDao,
            timeTargetingDayTypePeriodDao,
            campaignGetWithCheckHelper,
            countryGetWithCheckHelper,
            structGetWithCheckHelper,
            timeTargetingGetWithCheckHelper,
            timeTargetingPersistHelper
        )
    )

    val notEmptyErrors = setOf(
        mock<ConstraintViolation<Any>> {
            on { this.propertyPath } doReturn PathImpl.createPathFromString("field")
            on { this.message } doReturn "error"
        }
    )
    val emptyErrors = emptySet<ConstraintViolation<Any>>()

    @Nested
    @DisplayName("createのテスト")
    inner class CreateTest {
        val structIds = listOf(1, 2, 3).map { StructId(it) }
        val basicForm: BasicSettingCreateForm = mock {
            on { this.countryId } doReturn countryId
        }
        val form: TimeTargetingCreateForm = mock {
            on { this.basic } doReturn basicForm
            on { this.structIds } doReturn structIds
        }
        val campaignIds = listOf(1, 2).map { CampaignId(it) }
        val structs = listOf(1, 1, 2).map { struct(it) }
        val country: CountryMaster = mock()
        val validation: TimeTargetingCreateValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(TimeTargetingCreateValidation)
            every { TimeTargetingCreateValidation.of(any(), any(), any()) } returns validation

            doReturn(mock<List<CampaignCo>>())
                .whenever(campaignGetWithCheckHelper)
                .getCampaignsWithCheck(any(), any(), any())
            doReturn(structs).whenever(structGetWithCheckHelper).getStructsWithCheck(any(), any())
            doReturn(country).whenever(countryGetWithCheckHelper).getCountryWithCheck(any())
            doReturn(timeTargetingId).whenever(timeTargetingPersistHelper).create(any(), any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(emptyErrors).whenever(validator).validate(any<TimeTargetingCreateValidation>())

            val actual = sut.create(coAccountId, form)

            assertEquals(TimeTargetingCreateResultView(timeTargetingId), actual)
            verify(structGetWithCheckHelper, times(1)).getStructsWithCheck(structIds, StructStatus.viewableStatuses)
            verify(campaignGetWithCheckHelper, times(1)).getCampaignsWithCheck(
                coAccountId,
                campaignIds,
                CampaignStatus.viewableStatuses
            )
            verify(countryGetWithCheckHelper, times(1)).getCountryWithCheck(countryId)
            verify(validator, times(1)).validate(validation)
            verify(timeTargetingPersistHelper, times(1)).create(coAccountId, form)
            verifyK { TimeTargetingCreateValidation.of(form, country, structs) }
        }

        @Test
        @DisplayName("バリデーションエラー")
        fun isValidationError() {
            doReturn(notEmptyErrors).whenever(validator).validate(any<TimeTargetingCreateValidation>())

            assertThrows<FormatValidationException> { sut.create(coAccountId, form) }

            verify(structGetWithCheckHelper, times(1)).getStructsWithCheck(structIds, StructStatus.viewableStatuses)
            verify(campaignGetWithCheckHelper, times(1)).getCampaignsWithCheck(
                coAccountId,
                campaignIds,
                CampaignStatus.viewableStatuses
            )
            verify(countryGetWithCheckHelper, times(1)).getCountryWithCheck(countryId)
            verify(validator, times(1)).validate(validation)
            verify(timeTargetingPersistHelper, never()).create(any(), any())
            verifyK { TimeTargetingCreateValidation.of(form, country, structs) }
        }
    }

    @Nested
    @DisplayName("editのテスト")
    inner class EditTest {
        val checkValue: TimeTargetingCheckValue = mock()
        val timeTargeting: TimeTargeting = mock()
        val structIds = listOf(1, 2, 3).map { StructId(it) }
        val basicForm: BasicSettingEditForm = mock {
            on { this.countryId } doReturn countryId
        }
        val form: TimeTargetingEditForm = mock {
            on { this.basic } doReturn basicForm
            on { this.structIds } doReturn structIds
            on { this.checkValue } doReturn checkValue
        }
        val campaignIds = listOf(1, 2).map { CampaignId(it) }
        val structs = listOf(1, 1, 2).map { struct(it) }
        val currentDayTypePeriods: List<TimeTargetingDayTypePeriod> = listOf(mock())
        val currentStructs: List<StructCo> = mock()
        val country: CountryMaster = mock()
        val validation: TimeTargetingEditValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(TimeTargetingEditValidation)
            every { TimeTargetingEditValidation.of(any(), any(), any(), any()) } returns validation

            doReturn(timeTargeting)
                .whenever(timeTargetingGetWithCheckHelper)
                .getTimeTargetingWithCheck(any(), any(), any())
            doReturn(mock<List<CampaignCo>>())
                .whenever(campaignGetWithCheckHelper)
                .getCampaignsWithCheck(any(), any(), any())
            doReturn(structs).whenever(structGetWithCheckHelper).getStructsWithCheck(any(), any())
            doReturn(country).whenever(countryGetWithCheckHelper).getCountryWithCheck(any())
            doReturn(currentStructs).whenever(structDao).selectByTimeTargetingIdAndStatuses(any(), any())
            doReturn(currentDayTypePeriods).whenever(timeTargetingDayTypePeriodDao).selectById(any())
            doNothing().whenever(sut).checkConflict(any(), any(), any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("バリデーションエラーのとき")
        fun isValidationError() {
            doReturn(notEmptyErrors).whenever(validator).validate(any<TimeTargetingEditValidation>())

            assertThrows<FormatValidationException> {
                sut.edit(coAccountId, timeTargetingId, form)
            }

            verify(timeTargetingGetWithCheckHelper, times(1))
                .getTimeTargetingWithCheck(coAccountId, timeTargetingId, listOf(TimeTargetingStatus.active))
            verify(structGetWithCheckHelper, times(1)).getStructsWithCheck(structIds, StructStatus.viewableStatuses)
            verify(campaignGetWithCheckHelper, times(1))
                .getCampaignsWithCheck(coAccountId, campaignIds, CampaignStatus.viewableStatuses)
            verify(countryGetWithCheckHelper, times(1)).getCountryWithCheck(countryId)
            verify(structDao, times(1)).selectByTimeTargetingIdAndStatuses(timeTargetingId, StructStatus.entries)
            verify(timeTargetingDayTypePeriodDao, times(1)).selectById(timeTargetingId)
            verify(sut, times(1)).checkConflict(checkValue, timeTargeting, currentDayTypePeriods)
            verifyK { TimeTargetingEditValidation.of(form, timeTargeting, country, structs) }
            verify(validator, times(1)).validate(validation)
            verify(timeTargetingPersistHelper, never()).edit(any(), any(), any(), any())
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(emptyErrors).whenever(validator).validate(any<TimeTargetingEditValidation>())

            assertDoesNotThrow { sut.edit(coAccountId, timeTargetingId, form) }

            verify(timeTargetingGetWithCheckHelper, times(1))
                .getTimeTargetingWithCheck(coAccountId, timeTargetingId, listOf(TimeTargetingStatus.active))
            verify(structGetWithCheckHelper, times(1)).getStructsWithCheck(structIds, StructStatus.viewableStatuses)
            verify(campaignGetWithCheckHelper, times(1))
                .getCampaignsWithCheck(coAccountId, campaignIds, CampaignStatus.viewableStatuses)
            verify(countryGetWithCheckHelper, times(1)).getCountryWithCheck(countryId)
            verify(structDao, times(1)).selectByTimeTargetingIdAndStatuses(timeTargetingId, StructStatus.entries)
            verify(timeTargetingDayTypePeriodDao, times(1)).selectById(timeTargetingId)
            verify(sut, times(1)).checkConflict(checkValue, timeTargeting, currentDayTypePeriods)
            verifyK { TimeTargetingEditValidation.of(form, timeTargeting, country, structs) }
            verify(validator, times(1)).validate(validation)
            verify(timeTargetingPersistHelper, times(1))
                .edit(timeTargetingId, form, currentDayTypePeriods, currentStructs)
        }
    }

    @Nested
    @DisplayName("editTimeTargetingStatusのテスト")
    inner class EditTimeTargetingStatusTest {
        val checkValue: TimeTargetingCheckValue = mock()
        val form: TimeTargetingStatusChangeForm = mock {
            on { this.checkValue } doReturn checkValue
        }
        val timeTargeting: TimeTargeting = mock()
        val currentDayTypePeriods: List<TimeTargetingDayTypePeriod> = mock()
        val currentStructs: List<StructCo> = mock()
        val validation: TimeTargetingStatusChangeValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(TimeTargetingStatusChangeValidation)
            every { TimeTargetingStatusChangeValidation.of(any(), any(), any()) } returns validation

            doReturn(timeTargeting)
                .whenever(timeTargetingGetWithCheckHelper)
                .getTimeTargetingWithCheck(any(), any(), any())
            doReturn(currentStructs).whenever(structDao).selectByTimeTargetingIdAndStatuses(any(), any())
            doReturn(currentDayTypePeriods).whenever(timeTargetingDayTypePeriodDao).selectById(any())
            doNothing().whenever(sut).checkConflict(any(), any(), any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("バリデーションエラーのとき")
        fun isValidationError() {
            doReturn(notEmptyErrors).whenever(validator).validate(any<TimeTargetingStatusChangeValidation>())

            assertThrows<FormatValidationException> {
                sut.editTimeTargetingStatus(coAccountId, timeTargetingId, form)
            }

            verify(timeTargetingGetWithCheckHelper, times(1))
                .getTimeTargetingWithCheck(coAccountId, timeTargetingId, TimeTargetingStatus.viewableStatuses)
            verify(structDao, times(1)).selectByTimeTargetingIdAndStatuses(timeTargetingId, StructStatus.entries)
            verify(timeTargetingDayTypePeriodDao, times(1)).selectById(timeTargetingId)
            verify(sut, times(1)).checkConflict(checkValue, timeTargeting, currentDayTypePeriods)
            verifyK { TimeTargetingStatusChangeValidation.of(form, timeTargeting, currentStructs) }
            verify(validator, times(1)).validate(validation)
            verify(timeTargetingPersistHelper, never()).editTimeTargetingStatus(any(), any())
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(emptyErrors).whenever(validator).validate(any<TimeTargetingStatusChangeValidation>())

            assertDoesNotThrow {
                sut.editTimeTargetingStatus(coAccountId, timeTargetingId, form)
            }

            verify(timeTargetingGetWithCheckHelper, times(1))
                .getTimeTargetingWithCheck(coAccountId, timeTargetingId, TimeTargetingStatus.viewableStatuses)
            verify(structDao, times(1)).selectByTimeTargetingIdAndStatuses(timeTargetingId, StructStatus.entries)
            verify(timeTargetingDayTypePeriodDao, times(1)).selectById(timeTargetingId)
            verify(sut, times(1)).checkConflict(checkValue, timeTargeting, currentDayTypePeriods)
            verifyK { TimeTargetingStatusChangeValidation.of(form, timeTargeting, currentStructs) }
            verify(validator, times(1)).validate(validation)
            verify(timeTargetingPersistHelper, times(1)).editTimeTargetingStatus(timeTargetingId, form)
        }
    }

    @Nested
    @DisplayName("checkConflictのテスト")
    inner class CheckConflictTest {
        val checkValue: TimeTargetingCheckValue = mock()
        val timeTargeting: TimeTargeting = mock()
        val timeTargetingDayTypePeriods: Collection<TimeTargetingDayTypePeriod> = listOf(mock())

        @BeforeEach
        fun beforeEach() {
            mockkObject(TimeTargetingCheckValue)
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("コンフリクトありのとき")
        fun isConflict() {
            every { TimeTargetingCheckValue.of(any(), any()) } returns mock()

            assertThrows<ResourceConflictException> {
                sut.checkConflict(checkValue, timeTargeting, timeTargetingDayTypePeriods)
            }

            verifyK { TimeTargetingCheckValue.of(timeTargeting, timeTargetingDayTypePeriods) }
        }

        @Test
        @DisplayName("コンフリクトなしのとき")
        fun isNotConflict() {
            every { TimeTargetingCheckValue.of(any(), any()) } returns checkValue

            assertDoesNotThrow {
                sut.checkConflict(checkValue, timeTargeting, timeTargetingDayTypePeriods)
            }

            verifyK { TimeTargetingCheckValue.of(timeTargeting, timeTargetingDayTypePeriods) }
        }
    }

    private fun struct(campaignId: Int): StructCo = mock {
        on { this.campaignId } doReturn CampaignId(campaignId)
    }
}
