package jp.mangaka.ssp.application.service.spot.validation.video

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio16to5
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio16to9
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio32to5
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm
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
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import io.mockk.verify as verifyK

@DisplayName("VideoSettingValidationのテスト")
private class VideoSettingValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @DisplayName("最大ローテーション回数のテスト")
    inner class RotationMaxTest {
        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(VideoSettingValidation(null, emptyList())).run {
                assertTrue(any { it.propertyPath.toString() == "rotationMax" })
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, 0, 101])
        @DisplayName("範囲外の値")
        fun isOutOfRange(value: Int) {
            validator.validate(VideoSettingValidation(value, emptyList())).run {
                assertTrue(any { it.propertyPath.toString() == "rotationMax" })
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 2, 99, 100])
        @DisplayName("正常")
        fun isValid(value: Int) {
            validator.validate(VideoSettingValidation(value, emptyList())).run {
                assertTrue(none { it.propertyPath.toString() == "rotationMax" })
            }
        }
    }

    @Nested
    @DisplayName("detailsのテスト")
    inner class DetailsTest {
        @Test
        @DisplayName("@NotEmptyが機能している")
        fun isNotEmpty() {
            val details = emptyList<VideoDetailValidation>()

            validator.validate(VideoSettingValidation(null, details)).run {
                assertTrue(any { it.propertyPath.toString() == "details" })
            }
        }

        @Test
        @DisplayName("@Validが機能している")
        fun isValid() {
            val details = listOf(VideoDetailValidation(null, null, null, null, null, false))

            validator.validate(VideoSettingValidation(null, details)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("details[0].") })
            }
        }
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val detail: VideoDetailValidation = mock()
        val detailForms: List<VideoDetailForm> =
            listOf(aspectRatio16to9, aspectRatio16to5, aspectRatio32to5).map { id ->
                mock {
                    on { this.aspectRatioId } doReturn id
                }
            }
        val site: Site = mock()
        val existingAspectRatioIds = listOf(aspectRatio16to9, aspectRatio16to5)

        @BeforeEach
        fun beforeEach() {
            mockkObject(VideoSettingValidation, VideoDetailValidation)

            every { VideoSettingValidation.checkNotDuplicate(any()) } returns Unit
            every { VideoSettingValidation.checkPrLabelType(any()) } returns Unit
            every { VideoDetailValidation.of(any(), any(), any(), any(), any(), any()) } returns detail
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常 - 入力あり")
        fun isCorrectAndNotEmpty() {
            val form = VideoSettingForm(10, true, 20, detailForms)

            val actual = VideoSettingValidation.of(
                form, UserType.ma_staff, site, DisplayType.overlay, false, existingAspectRatioIds
            )

            assertEquals(
                VideoSettingValidation(10, listOf(detail, detail, detail)),
                actual
            )

            verifyK { VideoSettingValidation.checkNotDuplicate(form) }
            verifyK { VideoSettingValidation.checkPrLabelType(form) }
            verifyK(exactly = 1) {
                VideoDetailValidation.of(
                    detailForms[0], UserType.ma_staff, site, DisplayType.overlay, false, true
                )
            }
            verifyK(exactly = 1) {
                VideoDetailValidation.of(
                    detailForms[1], UserType.ma_staff, site, DisplayType.overlay, false, true
                )
            }
            verifyK(exactly = 1) {
                VideoDetailValidation.of(
                    detailForms[2], UserType.ma_staff, site, DisplayType.overlay, false, false
                )
            }
        }

        @Test
        @DisplayName("正常 - 入力なし")
        fun isCorrectAndEmpty() {
            // detailsは空になることがないので入力あり
            val form = VideoSettingForm(null, false, null, detailForms)

            val actual = VideoSettingValidation.of(
                form, UserType.ma_staff, site, DisplayType.inline, false, existingAspectRatioIds
            )

            assertEquals(
                VideoSettingValidation(null, listOf(detail, detail, detail)),
                actual
            )

            verifyK { VideoSettingValidation.checkNotDuplicate(form) }
            verifyK { VideoSettingValidation.checkPrLabelType(form) }
            verifyK(exactly = 1) {
                VideoDetailValidation.of(detailForms[0], UserType.ma_staff, site, DisplayType.inline, false, true)
            }
            verifyK(exactly = 1) {
                VideoDetailValidation.of(detailForms[1], UserType.ma_staff, site, DisplayType.inline, false, true)
            }
            verifyK(exactly = 1) {
                VideoDetailValidation.of(detailForms[2], UserType.ma_staff, site, DisplayType.inline, false, false)
            }
        }
    }

    @Nested
    @DisplayName("checkNotDuplicateのテスト")
    inner class CheckNotDuplicateTest {
        val nonDuplicates = listOf(null, aspectRatio16to9, null, aspectRatio16to5, aspectRatio32to5).map { form(it) }

        @Test
        @DisplayName("重複あり")
        fun isDuplicate() {
            assertThrows<CompassManagerException> {
                VideoSettingValidation.checkNotDuplicate(
                    VideoSettingForm(null, true, null, nonDuplicates + form(aspectRatio16to9))
                )
            }
        }

        @Test
        @DisplayName("重複なし")
        fun isNotDuplicate() {
            assertDoesNotThrow {
                VideoSettingValidation.checkNotDuplicate(VideoSettingForm(null, true, null, nonDuplicates))
            }
        }

        private fun form(aspectRatioId: AspectRatioId?): VideoDetailForm = mock {
            on { this.aspectRatioId } doReturn aspectRatioId
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkPrLabelTypeのテスト")
    inner class CheckPrLabelTypeTest {
        val form: VideoSettingForm = mock()

        @ParameterizedTest
        @ValueSource(ints = [-1, 0, 7])
        @DisplayName("不正な値")
        fun isInvalid(value: Int) {
            doReturn(value).whenever(form).prLabelType

            assertThrows<CompassManagerException> { VideoSettingValidation.checkPrLabelType(form) }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(value: Int?) {
            doReturn(value).whenever(form).prLabelType

            assertDoesNotThrow { VideoSettingValidation.checkPrLabelType(form) }
        }

        private fun validParams() = listOf(null, 1, 2, 3, 4, 5, 6)
    }
}
