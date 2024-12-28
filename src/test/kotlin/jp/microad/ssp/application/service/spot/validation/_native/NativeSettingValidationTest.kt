package jp.mangaka.ssp.application.service.spot.validation._native

import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeStandardForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeVideoForm
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import io.mockk.verify as verifyK

@DisplayName("NativeSettingValidationのテスト")
private class NativeSettingValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @DisplayName("通常デザインのテスト")
    inner class StandardTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            val standard = NativeStandardValidation(null, null)

            validator.validate(NativeSettingValidation(standard, null)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("standard") })
            }
        }

        @Test
        @DisplayName("未入力")
        fun isNull() {
            validator.validate(NativeSettingValidation(null, null)).run {
                assertTrue(none { it.propertyPath.toString().startsWith("standard") })
            }
        }
    }

    @Nested
    @DisplayName("動画デザインのテスト")
    inner class VideoTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            val video = NativeVideoValidation(null, null)

            validator.validate(NativeSettingValidation(null, video)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("video") })
            }
        }

        @Test
        @DisplayName("未入力")
        fun isNull() {
            validator.validate(NativeSettingValidation(null, null)).run {
                assertTrue(none { it.propertyPath.toString().startsWith("video") })
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("フォーマットリストのテスト")
    inner class FormatsTest {
        val standardValidation: NativeStandardValidation = mock()
        val videoValidation: NativeVideoValidation = mock()

        @Test
        @DisplayName("全フォーマットが未入力")
        fun isAllNull() {
            validator.validate(NativeSettingValidation(null, null)).run {
                forEach { println(it.propertyPath.toString()) }
                assertTrue(any { it.propertyPath.toString() == "formats" })
            }
        }

        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("正常")
        fun isCorrect(standard: NativeStandardValidation?, video: NativeVideoValidation?) {
            validator.validate(NativeSettingValidation(standard, video)).run {
                assertTrue(none { it.propertyPath.toString() == "formats" })
            }
        }

        private fun correctParams() = listOf(
            Arguments.of(standardValidation, videoValidation),
            Arguments.of(standardValidation, null),
            Arguments.of(null, videoValidation)
        )
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val standardValidation: NativeStandardValidation = mock()
        val videoValidation: NativeVideoValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(NativeSettingValidation, NativeStandardValidation, NativeVideoValidation)
            every { NativeSettingValidation.checkNativeStandard(any(), any()) } returns Unit
            every { NativeStandardValidation.of(any(), any(), any()) } returns standardValidation
            every { NativeVideoValidation.of(any(), any(), any(), any()) } returns videoValidation
        }

        @AfterEach
        fun afterAll() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常 - 入力なし")
        fun isCorrectAndNull() {
            val form = NativeSettingForm(null, null)

            val actual = NativeSettingValidation.of(form, UserType.ma_staff, null, true, false, true)

            assertEquals(NativeSettingValidation(null, null), actual)

            verifyK { NativeSettingValidation.checkNativeStandard(form, true) }
            verifyK(exactly = 0) { NativeStandardValidation.of(any(), any(), any()) }
            verifyK(exactly = 0) { NativeVideoValidation.of(any(), any(), any(), any()) }
        }

        @Test
        @DisplayName("正常 - 入力あり")
        fun isCorrectAndNotNull() {
            val standardForm: NativeStandardForm = mock()
            val videoForm: NativeVideoForm = mock()
            val site: Site = mock()
            val form = NativeSettingForm(standardForm, videoForm)

            val actual = NativeSettingValidation.of(form, UserType.agency, site, false, true, false)

            assertEquals(NativeSettingValidation(standardValidation, videoValidation), actual)

            verifyK { NativeSettingValidation.checkNativeStandard(form, false) }
            verifyK { NativeStandardValidation.of(standardForm, UserType.agency, true) }
            verifyK { NativeVideoValidation.of(videoForm, UserType.agency, site, false) }
        }
    }
}
