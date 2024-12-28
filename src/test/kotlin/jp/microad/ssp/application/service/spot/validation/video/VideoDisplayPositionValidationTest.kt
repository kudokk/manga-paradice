package jp.mangaka.ssp.application.service.spot.validation.video

import com.nhaarman.mockito_kotlin.mock
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.spot.validation.video.VideoDisplayPositionValidation.VideoDisplayPositionElementValidation
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm.VideoDisplayPositionElementForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm.VideoDisplayPositionElementForm.DirectionType.Horizontal
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm.VideoDisplayPositionElementForm.DirectionType.Vertical
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("VideoDisplayPositionValidationのテスト")
private class VideoDisplayPositionValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("isNotEmptyのテスト")
    inner class IsNotEmptyTest {
        val direction: VideoDisplayPositionElementValidation = mock()

        @ParameterizedTest
        @MethodSource("notEmptyParams")
        @DisplayName("垂直・水平方向のいずれかが入力あり")
        fun isNotEmpty(
            vertical: VideoDisplayPositionElementValidation?,
            horizontal: VideoDisplayPositionElementValidation?
        ) {
            val sut = VideoDisplayPositionValidation(vertical, horizontal)

            assertTrue(sut.isNotEmpty())
        }

        private fun notEmptyParams() = listOf(
            Arguments.of(direction, direction),
            Arguments.of(direction, null),
            Arguments.of(null, direction)
        )

        @Test
        @DisplayName("垂直・水平方向のいずれも未入力")
        fun isEmpty() {
            val sut = VideoDisplayPositionValidation(null, null)

            assertFalse(sut.isNotEmpty())
        }
    }

    @Nested
    @DisplayName("@Validは機能しているか")
    inner class ValidTest {
        val direction = VideoDisplayPositionElementValidation(-1)

        @Test
        @DisplayName("垂直位置")
        fun isVertical() {
            validator.validate(VideoDisplayPositionValidation(direction, null)).run {
                assertTrue(any { it.propertyPath.toString() == ("vertical.distance") })
            }
        }

        @Test
        @DisplayName("水平位置")
        fun isHorizontal() {
            validator.validate(VideoDisplayPositionValidation(null, direction)).run {
                assertTrue(any { it.propertyPath.toString() == ("horizontal.distance") })
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @Test
        @DisplayName("Formが未設定")
        fun isEmptyForms() {
            val actual = VideoDisplayPositionValidation.of(VideoDisplayPositionForm(null, null))

            assertEquals(VideoDisplayPositionValidation(null, null), actual)
        }

        @Test
        @DisplayName("Formが一方のみ入力済")
        fun isNotEmpty() {
            val form = VideoDisplayPositionForm(
                VideoDisplayPositionElementForm(Vertical.top, 10),
                null
            )

            val actual = VideoDisplayPositionValidation.of(form)

            assertEquals(
                VideoDisplayPositionValidation(
                    VideoDisplayPositionElementValidation(10),
                    null
                ),
                actual
            )
        }

        @Test
        @DisplayName("Formが両方入力済み")
        fun isEmptyDirectionOrDistance() {
            val form = VideoDisplayPositionForm(
                VideoDisplayPositionElementForm(Vertical.top, null),
                VideoDisplayPositionElementForm(Horizontal.left, 20)
            )

            val actual = VideoDisplayPositionValidation.of(form)

            assertEquals(
                VideoDisplayPositionValidation(
                    VideoDisplayPositionElementValidation(null),
                    VideoDisplayPositionElementValidation(20)
                ),
                actual
            )
        }
    }

    @Nested
    @DisplayName("VideoDisplayPositionElementValidationのテスト")
    inner class VideoDisplayPositionElementValidationTest {
        @Nested
        @DisplayName("距離のテスト")
        inner class DistanceTest {
            @ParameterizedTest
            @ValueSource(ints = [-1, 1001])
            @DisplayName("範囲外の値")
            fun isInvalid(value: Int) {
                validator.validate(VideoDisplayPositionElementValidation(value)).run {
                    assertTrue(any { it.propertyPath.toString() == "distance" })
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [0, 1, 999, 1000])
            @DisplayName("正常")
            fun isValid(value: Int) {
                validator.validate(VideoDisplayPositionElementValidation(value)).run {
                    assertTrue(none { it.propertyPath.toString() == "distance" })
                }
            }
        }
    }
}
