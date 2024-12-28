package jp.mangaka.ssp.application.service.spot.validation

import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.spot.validation.dsp.DspValidation
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.DspForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotDspEditForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal
import io.mockk.verify as verifyK

@DisplayName("SpotDspEditValidationのテスト")
private class SpotDspEditValidationTest {
    companion object {
        val dspId1 = DspId(1)
        val dspId2 = DspId(2)
    }

    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @DisplayName("DSPリストのテスト")
    inner class DspsTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            val dsp = DspValidation(null, null, null)

            validator.validate(SpotDspEditValidation(listOf(dsp))).run {
                assertTrue(any { it.propertyPath.toString().startsWith("dsps[0].") })
            }
        }
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val dspForms = listOf(
            DspForm(null, null, null),
            DspForm(dspId1, BigDecimal("123.456"), BigDecimal("234.567")),
            DspForm(dspId2, BigDecimal("345.678"), BigDecimal("567.890"))
        )
        val form = SpotDspEditForm(dspForms, mock())
        val dspValidations = listOf(
            DspValidation(null, null, null),
            DspValidation(dspId1, BigDecimal("123.456"), BigDecimal("234.567")),
            DspValidation(dspId2, BigDecimal("345.678"), BigDecimal("567.890"))
        )

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotDspEditValidation)
            every { SpotDspEditValidation.checkMaStaffOnly(any()) } returns Unit
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = SpotDspEditValidation.of(form, UserType.ma_staff)

            assertEquals(SpotDspEditValidation(dspValidations), actual)

            verifyK { SpotDspEditValidation.checkMaStaffOnly(UserType.ma_staff) }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkMaStaffOnlyのテスト")
    inner class CheckMaStaffOnlyTest {
        @ParameterizedTest
        @EnumSource(value = UserType::class, names = ["ma_staff"], mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("不正入力")
        fun isInvalid(userType: UserType) {
            assertThrows<CompassManagerException> {
                SpotDspEditValidation.checkMaStaffOnly(userType)
            }
        }

        @Test
        @DisplayName("正常")
        fun isValid() {
            assertDoesNotThrow { SpotDspEditValidation.checkMaStaffOnly(UserType.ma_staff) }
        }
    }
}
