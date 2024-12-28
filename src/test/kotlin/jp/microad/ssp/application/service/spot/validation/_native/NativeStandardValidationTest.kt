package jp.mangaka.ssp.application.service.spot.validation._native

import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.spot.validation.CloseButtonValidation
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeStandardForm
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
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import io.mockk.verify as verifyK

@DisplayName("NativeStandardValidationのテスト")
private class NativeStandardValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator!!

    @Nested
    @DisplayName("ネイティブテンプレートIDのテスト")
    inner class NativeTemplateIdTest {
        @Test
        @DisplayName("未入力")
        fun isNull() {
            validator.validate(NativeStandardValidation(null, mock())).run {
                assertTrue(any { it.propertyPath.toString() == "nativeTemplateId" })
            }
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            validator.validate(NativeStandardValidation(NativeTemplateId(1), mock())).run {
                assertTrue(none { it.propertyPath.toString() == "nativeTemplateId" })
            }
        }
    }

    @Nested
    @DisplayName("閉じるボタンのテスト")
    inner class CloseButtonTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            val closeButton = CloseButtonValidation(1, -1, null, null, null)

            validator.validate(NativeStandardValidation(null, closeButton)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("closeButton") })
            }
        }

        @Test
        @DisplayName("未入力")
        fun isNull() {
            validator.validate(NativeStandardValidation(null, null)).run {
                assertTrue(none { it.propertyPath.toString().startsWith("closeButton") })
            }
        }
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val closeButtonValidation: CloseButtonValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(NativeStandardValidation, CloseButtonValidation)
            every { NativeStandardValidation.checkMaStaffOnly(any(), any(), any()) } returns Unit
            every { CloseButtonValidation.of(any()) } returns closeButtonValidation
        }

        @AfterEach
        fun afterAll() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常 - 入力なし")
        fun isCorrectAndNull() {
            val form = NativeStandardForm(null, null)

            val actual = NativeStandardValidation.of(form, UserType.ma_staff, true)

            assertEquals(NativeStandardValidation(null, null), actual)

            verifyK { NativeStandardValidation.checkMaStaffOnly(form, UserType.ma_staff, true) }
        }

        @Test
        @DisplayName("正常 - 入力あり")
        fun isCorrectAndNotNull() {
            val nativeTemplateId = NativeTemplateId(1)
            val closeButton: CloseButtonForm = mock()
            val form = NativeStandardForm(nativeTemplateId, closeButton)

            val actual = NativeStandardValidation.of(form, UserType.agency, false)

            assertEquals(NativeStandardValidation(nativeTemplateId, closeButtonValidation), actual)

            verifyK { NativeStandardValidation.checkMaStaffOnly(form, UserType.agency, false) }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkMaStaffOnlyのテスト")
    inner class CheckMaStaffOnlyTest {
        val closeButton: CloseButtonForm = mock()

        @ParameterizedTest
        @MethodSource("invalidParams")
        @DisplayName("不正入力")
        fun isInvalidCloseButton(userType: UserType, closeButton: CloseButtonForm?, isExistStandard: Boolean) {
            val form = NativeStandardForm(null, closeButton)

            assertThrows<CompassManagerException> {
                NativeStandardValidation.checkMaStaffOnly(form, userType, isExistStandard)
            }
        }

        private fun invalidParams() = listOf(
            Arguments.of(UserType.agency, closeButton, false),
            Arguments.of(UserType.client, closeButton, false),
            Arguments.of(UserType.other, closeButton, false)
        )

        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("正常")
        fun isCorrect(userType: UserType, closeButton: CloseButtonForm?, isExistStandard: Boolean) {
            val form = NativeStandardForm(null, closeButton)

            assertDoesNotThrow { NativeStandardValidation.checkMaStaffOnly(form, userType, isExistStandard) }
        }

        private fun correctParams() = listOf(
            // 社員
            Arguments.of(UserType.ma_staff, null, false),
            Arguments.of(UserType.ma_staff, closeButton, false),
            // 既存あり
            Arguments.of(UserType.agency, closeButton, true),
            Arguments.of(UserType.client, closeButton, true),
            Arguments.of(UserType.other, closeButton, true),
            // 社員限定項目未設定
            Arguments.of(UserType.agency, null, false),
            Arguments.of(UserType.client, null, false),
            Arguments.of(UserType.other, null, false)
        )
    }
}
