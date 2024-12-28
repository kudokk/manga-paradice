package jp.mangaka.ssp.application.service.spot.validation._native

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.spot.validation.CloseButtonValidation
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeVideoForm
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

@DisplayName("NativeVideoValidationのテスト")
private class NativeVideoValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator!!

    @Nested
    @DisplayName("ネイティブテンプレートIDのテスト")
    inner class NativeTemplateIdTest {
        @Test
        @DisplayName("未入力")
        fun isNull() {
            validator.validate(NativeVideoValidation(null, mock())).run {
                assertTrue(any { it.propertyPath.toString() == "nativeTemplateId" })
            }
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            validator.validate(NativeVideoValidation(NativeTemplateId(1), mock())).run {
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

            validator.validate(NativeVideoValidation(null, closeButton)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("closeButton") })
            }
        }

        @Test
        @DisplayName("未入力")
        fun isNull() {
            validator.validate(NativeVideoValidation(null, null)).run {
                assertTrue(none { it.propertyPath.toString().startsWith("closeButton") })
            }
        }
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val site: Site = mock()
        val closeButtonValidation: CloseButtonValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(NativeVideoValidation, CloseButtonValidation)
            every { NativeVideoValidation.checkMaStaffOnly(any(), any(), any()) } returns Unit
            every { NativeVideoValidation.checkIsScalable(any(), any()) } returns Unit
            every { CloseButtonValidation.of(any()) } returns closeButtonValidation
        }

        @AfterEach
        fun afterAll() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常 - 入力なし")
        fun isCorrectAndNull() {
            val form = NativeVideoForm(null, null, false)
            doReturn(PlatformId.pc).whenever(site).platformId

            val actual = NativeVideoValidation.of(form, UserType.ma_staff, site, true)

            assertEquals(NativeVideoValidation(null, null), actual)

            verify { NativeVideoValidation.checkMaStaffOnly(form, UserType.ma_staff, true) }
            verify { NativeVideoValidation.checkIsScalable(form, true) }
        }

        @Test
        @DisplayName("正常 - 入力あり")
        fun isCorrectAndNotNull() {
            val nativeTemplateId = NativeTemplateId(1)
            val closeButton: CloseButtonForm = mock()
            val form = NativeVideoForm(nativeTemplateId, closeButton, true)
            doReturn(PlatformId.smartPhone).whenever(site).platformId

            val actual = NativeVideoValidation.of(form, UserType.agency, site, false)

            assertEquals(NativeVideoValidation(nativeTemplateId, closeButtonValidation), actual)

            verify { NativeVideoValidation.checkMaStaffOnly(form, UserType.agency, false) }
            verify { NativeVideoValidation.checkIsScalable(form, false) }
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
        fun isInvalidCloseButton(
            userType: UserType,
            closeButton: CloseButtonForm?,
            isScalable: Boolean,
            isExistVideo: Boolean
        ) {
            val form = NativeVideoForm(null, closeButton, isScalable)

            assertThrows<CompassManagerException> {
                NativeVideoValidation.checkMaStaffOnly(form, userType, isExistVideo)
            }
        }

        private fun invalidParams() = listOf(
            Arguments.of(UserType.agency, closeButton, false, false),
            Arguments.of(UserType.client, null, true, false),
            Arguments.of(UserType.other, null, true, false),
        )

        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("正常")
        fun isCorrect(userType: UserType, closeButton: CloseButtonForm?, isScalable: Boolean, isExistVideo: Boolean) {
            val form = NativeVideoForm(null, closeButton, isScalable)

            assertDoesNotThrow { NativeVideoValidation.checkMaStaffOnly(form, userType, isExistVideo) }
        }

        private fun correctParams() = listOf(
            // 社員
            Arguments.of(UserType.ma_staff, null, false, false),
            Arguments.of(UserType.ma_staff, closeButton, true, false),
            Arguments.of(UserType.agency, closeButton, true, true),
            // 既存設定あり
            Arguments.of(UserType.client, closeButton, true, true),
            Arguments.of(UserType.other, closeButton, true, true),
            // 社員限定項目未設定
            Arguments.of(UserType.agency, null, false, false),
            Arguments.of(UserType.client, null, false, false),
            Arguments.of(UserType.other, null, false, false)
        )
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkIsScalableのテスト")
    inner class CheckIsScalableTest {
        val form: NativeVideoForm = mock()

        @Test
        @DisplayName("PCサイトで広告拡大あり")
        fun isInvalid() {
            doReturn(true).whenever(form).isScalable

            assertThrows<CompassManagerException> { NativeVideoValidation.checkIsScalable(form, true) }
        }

        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("正常")
        fun isCorrect(isScalable: Boolean, isPcSite: Boolean) {
            doReturn(isScalable).whenever(form).isScalable

            assertDoesNotThrow { NativeVideoValidation.checkIsScalable(form, isPcSite) }
        }

        private fun correctParams() = listOf(
            Arguments.of(false, true),
            Arguments.of(true, false),
            Arguments.of(false, false)
        )
    }
}
