package jp.mangaka.ssp.application.service.spot.validation

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.spot.validation.CloseButtonValidation.ColorValidation
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm.ColorForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

@DisplayName("CloseButtonValidationのテスト")
private class CloseButtonValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @DisplayName("表示位置のテスト")
    inner class DisplayPositionTest {
        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(CloseButtonValidation(null, null, null, null, null)).run {
                assertTrue(any { it.propertyPath.toString() == "displayPosition" })
            }
        }

        @Test
        @DisplayName("正常")
        fun isValid() {
            validator.validate(CloseButtonValidation(1, null, null, null, null)).run {
                assertTrue(none { it.propertyPath.toString() == "displayPosition" })
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("表示サイズのテスト")
    inner class DisplaySizeTest {
        @ParameterizedTest
        @ValueSource(ints = [-1, 256])
        @DisplayName("範囲外の値")
        fun isOutOfRange(value: Int?) {
            validator.validate(CloseButtonValidation(1, value, null, null, null)).run {
                assertTrue(any { it.propertyPath.toString() == "displaySize" })
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(value: Int?) {
            validator.validate(CloseButtonValidation(1, value, null, null, null)).run {
                assertTrue(none { it.propertyPath.toString() == "displaySize" })
            }
        }

        private fun validParams() = listOf(null, 0, 1, 254, 255)
    }

    @Nested
    @DisplayName("本体配色のテスト")
    inner class LineColorTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            validator.validate(CloseButtonValidation(1, 1, ColorValidation(-1, -1, -1, 0.0), null, null)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("lineColor") })
            }
        }
    }

    @Nested
    @DisplayName("背景配色のテスト")
    inner class BackgroundColorTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            validator.validate(CloseButtonValidation(1, 1, null, ColorValidation(-1, -1, -1, 0.0), null)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("backgroundColor") })
            }
        }
    }

    @Nested
    @DisplayName("ボーダー配色のテスト")
    inner class FrameColorTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            validator.validate(CloseButtonValidation(1, 1, null, null, ColorValidation(-1, -1, -1, 0.0))).run {
                assertTrue(any { it.propertyPath.toString().startsWith("frameColor") })
            }
        }
    }

    @Nested
    @DisplayName("配色のテスト")
    inner class ColorTest {
        @Nested
        @DisplayName("赤のテスト")
        inner class RedTest {
            @Test
            @DisplayName("未入力")
            fun isEmpty() {
                validator.validate(ColorValidation(null, 0, 0, 0.0)).run {
                    assertTrue(any { it.propertyPath.toString() == "red" })
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [-1, 256])
            @DisplayName("範囲外の値")
            fun isOutOfRange(value: Int) {
                validator.validate(ColorValidation(value, 0, 0, 0.0)).run {
                    assertTrue(any { it.propertyPath.toString() == "red" })
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [0, 1, 254, 255])
            @DisplayName("正常")
            fun isValid(value: Int) {
                validator.validate(ColorValidation(value, 0, 0, 0.0)).run {
                    assertTrue(none { it.propertyPath.toString() == "red" })
                }
            }
        }

        @Nested
        @DisplayName("緑のテスト")
        inner class GreenTest {
            @Test
            @DisplayName("未入力")
            fun isEmpty() {
                validator.validate(ColorValidation(0, null, 0, 0.0)).run {
                    assertTrue(any { it.propertyPath.toString() == "green" })
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [-1, 256])
            @DisplayName("範囲外の値")
            fun isOutOfRange(value: Int) {
                validator.validate(ColorValidation(0, value, 0, 0.0)).run {
                    assertTrue(any { it.propertyPath.toString() == "green" })
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [0, 1, 254, 255])
            @DisplayName("正常")
            fun isValid(value: Int) {
                validator.validate(ColorValidation(0, value, 0, 0.0)).run {
                    assertTrue(none { it.propertyPath.toString() == "green" })
                }
            }
        }

        @Nested
        @DisplayName("青のテスト")
        inner class BlueTest {
            @Test
            @DisplayName("未入力")
            fun isEmpty() {
                validator.validate(ColorValidation(0, 0, null, 0.0)).run {
                    assertTrue(any { it.propertyPath.toString() == "blue" })
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [-1, 256])
            @DisplayName("範囲外の値")
            fun isOutOfRange(value: Int) {
                validator.validate(ColorValidation(0, 0, value, 0.0)).run {
                    assertTrue(any { it.propertyPath.toString() == "blue" })
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [0, 1, 254, 255])
            @DisplayName("正常")
            fun isValid(value: Int) {
                validator.validate(ColorValidation(0, 0, value, 0.0)).run {
                    assertTrue(none { it.propertyPath.toString() == "blue" })
                }
            }
        }

        @Nested
        @DisplayName("透明度のテスト")
        inner class OpacityTest {
            @ParameterizedTest
            @ValueSource(doubles = [0.98765, 0.4321])
            @DisplayName("桁数の多いの値")
            fun isTooLongOfNumber(value: Double) {
                validator.validate(ColorValidation(0, 0, 0, value)).run {
                    assertTrue(any { it.propertyPath.toString() == "opacity" })
                }
            }

            @ParameterizedTest
            @ValueSource(doubles = [1.0, 0.1, 0.23, 0.456])
            @DisplayName("正常")
            fun isValid(value: Double) {
                validator.validate(ColorValidation(0, 0, 0, value)).run {
                    assertTrue(none { it.propertyPath.toString() == "opacity" })
                }
            }
        }

        @Nested
        @DisplayName("checkOpacityのテスト")
        inner class CheckOpacityTest {
            val form: ColorForm = mock()

            @ParameterizedTest
            @ValueSource(doubles = [-0.1, 1.1])
            @DisplayName("範囲外の値")
            fun isOutOfRange(value: Double) {
                doReturn(value).whenever(form).opacity

                assertThrows<CompassManagerException> { ColorValidation.checkOpacity(form) }
            }

            @ParameterizedTest
            @ValueSource(doubles = [0.0, 0.1, 0.9, 1.0])
            @DisplayName("正常")
            fun isValid(value: Double) {
                doReturn(value).whenever(form).opacity

                assertDoesNotThrow { ColorValidation.checkOpacity(form) }
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("ファクトリ関数のテスト")
    inner class OfTest {
        init {
            mockkObject(CloseButtonValidation, ColorValidation)

            every { CloseButtonValidation.checkDisplayPosition(any()) } returns Unit
            every { ColorValidation.checkOpacity(any()) } returns Unit
        }

        @AfterAll
        fun afterAll() {
            unmockkAll()
        }

        @Test
        @DisplayName("必須項目のみ")
        fun isOnlyRequired() {
            val form = CloseButtonForm(1, null, null, null, null)

            val actual = CloseButtonValidation.of(form)

            assertEquals(CloseButtonValidation(1, null, null, null, null), actual)

            verifyK { CloseButtonValidation.checkDisplayPosition(form) }
        }

        @Test
        @DisplayName("全項目")
        fun isAll() {
            val form = CloseButtonForm(
                1, 10, ColorForm(null, null, null, 0.0), ColorForm(0, 0, 0, 0.0), ColorForm(255, 255, 255, 1.0)
            )

            val actual = CloseButtonValidation.of(form)

            assertEquals(
                CloseButtonValidation(
                    1,
                    10,
                    ColorValidation(null, null, null, 0.0),
                    ColorValidation(0, 0, 0, 0.0),
                    ColorValidation(255, 255, 255, 1.0)
                ),
                actual
            )

            verifyK { CloseButtonValidation.checkDisplayPosition(form) }
            verifyK(exactly = 1) { ColorValidation.checkOpacity(form.lineColor!!) }
            verifyK(exactly = 1) { ColorValidation.checkOpacity(form.backgroundColor!!) }
            verifyK(exactly = 1) { ColorValidation.checkOpacity(form.frameColor!!) }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkDisplayPositionのテスト")
    inner class CheckDisplayPositionTest {
        val form: CloseButtonForm = mock()

        @ParameterizedTest
        @ValueSource(ints = [0, 19])
        @DisplayName("範囲外の値")
        fun isOutOfRange(value: Int) {
            doReturn(value).whenever(form).displayPosition

            assertThrows<CompassManagerException> { CloseButtonValidation.checkDisplayPosition(form) }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(value: Int?) {
            doReturn(value).whenever(form).displayPosition

            assertDoesNotThrow { CloseButtonValidation.checkDisplayPosition(form) }
        }

        private fun validParams() = listOf(null, 1, 18)
    }
}
