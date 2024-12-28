package jp.mangaka.ssp.application.service.spot.validation

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.spot.validation._native.NativeSettingValidation
import jp.mangaka.ssp.application.service.spot.validation.banner.SpotSizeTypeDeleteRule
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotNativeEditForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import io.mockk.verify as verifyK

@DisplayName("SpotNativeEditValidationのテスト")
private class SpotNativeEditValidationTest {
    companion object {
        val spotId = SpotId(1)
    }

    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @DisplayName("ネイティブ設定のテスト")
    inner class NativeTest {
        @Test
        @DisplayName("@Validは機能しているか")
        fun isValid() {
            val native = NativeSettingValidation(null, null)

            validator.validate(SpotNativeEditValidation(native, emptyList(), true)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("native.") })
            }
        }

        @Test
        @DisplayName("ネイティブ設定なしのとき")
        fun isNoNative() {
            validator.validate(SpotNativeEditValidation(null, emptyList(), true)).run {
                assertTrue(none { it.propertyPath.toString().startsWith("native.") })
            }
        }
    }

    @Nested
    @DisplayName("ストラクト紐づきエラーのテスト")
    inner class StructSizeTypesTest {
        @Test
        @DisplayName("@Validは機能しているか")
        fun isValid() {
            val structSizeTypes = listOf(
                StructSizeTypeError(100, 200, listOf(1, 2, 3).map { StructId(it) })
            )

            validator.validate(SpotNativeEditValidation(null, structSizeTypes, true)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("structSizeTypes[0].") })
            }
        }
    }

    @Nested
    @DisplayName("他フォーマットとの相関チェックのテスト")
    inner class FormatsTest {
        @Nested
        @DisplayName("ネイティブ設定あり")
        inner class ActiveNativeTest {
            @ParameterizedTest
            @ValueSource(booleans = [true, false])
            @DisplayName("常に正常")
            fun isValid(hasOtherFormat: Boolean) {
                val native = NativeSettingValidation(null, null)

                validator.validate(SpotNativeEditValidation(native, emptyList(), hasOtherFormat)).run {
                    assertTrue(none { it.propertyPath.toString() == "formats" })
                }
            }
        }

        @Nested
        @DisplayName("バナー設定なし")
        inner class InactiveBannerTest {
            @Test
            @DisplayName("他フォーマット設定あり")
            fun isActiveOtherFormat() {
                validator.validate(SpotNativeEditValidation(null, emptyList(), true)).run {
                    assertTrue(none { it.propertyPath.toString() == "formats" })
                }
            }

            @Test
            @DisplayName("他フォーマット設定なし")
            fun isInactiveOtherFormat() {
                validator.validate(SpotNativeEditValidation(null, emptyList(), false)).run {
                    assertTrue(any { it.propertyPath.toString() == "formats" })
                }
            }
        }
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val spot: Spot = mock {
            on { this.spotId } doReturn spotId
        }
        val site: Site = mock()
        val deleteSpotSizeTypes: Collection<SizeTypeInfo> = mock()
        val spotSizeTypeDeleteRule: SpotSizeTypeDeleteRule = mock()
        val structSizeTypes: List<StructSizeTypeError> = mock()
        val nativeValidation: NativeSettingValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotNativeEditValidation, NativeSettingValidation, StructSizeTypeError)
            every { SpotNativeEditValidation.checkAllowNative(any(), any()) } returns Unit
            every { NativeSettingValidation.of(any(), any(), any(), any(), any(), any()) } returns nativeValidation
            every { StructSizeTypeError.of(any(), any(), any()) } returns structSizeTypes
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("ネイティブ設定あり")
        fun isActiveNative() {
            val nativeForm: NativeSettingForm = mock()
            val form = SpotNativeEditForm(nativeForm, mock())

            val actual = SpotNativeEditValidation.of(
                form, UserType.ma_staff, spot, site, true, false, deleteSpotSizeTypes, spotSizeTypeDeleteRule,
                false, true
            )

            assertEquals(
                SpotNativeEditValidation(nativeValidation, structSizeTypes, false),
                actual
            )
            verifyK { SpotNativeEditValidation.checkAllowNative(form, spot) }
            verifyK { NativeSettingValidation.of(nativeForm, UserType.ma_staff, site, true, false, true) }
            verifyK { StructSizeTypeError.of(spotId, deleteSpotSizeTypes, spotSizeTypeDeleteRule) }
        }

        @Test
        @DisplayName("ネイティブ設定なし")
        fun isInactiveNative() {
            val form = SpotNativeEditForm(null, mock())

            val actual = SpotNativeEditValidation.of(
                form, UserType.ma_staff, spot, site, true, false, deleteSpotSizeTypes, spotSizeTypeDeleteRule,
                false, true
            )

            assertEquals(SpotNativeEditValidation(null, structSizeTypes, false), actual)
            verifyK { SpotNativeEditValidation.checkAllowNative(form, spot) }
            verifyK(exactly = 0) { NativeSettingValidation.of(any(), any(), any(), any(), any(), any()) }
            verifyK { StructSizeTypeError.of(spotId, deleteSpotSizeTypes, spotSizeTypeDeleteRule) }
        }
    }

    @Nested
    @DisplayName("checkAllowNativeのテスト")
    inner class CheckAllowNativeTest {
        val spot: Spot = mock()

        @Test
        @DisplayName("ネイティブ設定が許可されていない広告枠")
        fun isNotAllow() {
            val form = SpotNativeEditForm(mock(), mock())
            doReturn(false).whenever(spot).isAllowNative()

            assertThrows<CompassManagerException> {
                SpotNativeEditValidation.checkAllowNative(form, spot)
            }
        }

        @Test
        @DisplayName("ネイティブ設定が許可されている広告枠")
        fun isAllow() {
            val form = SpotNativeEditForm(mock(), mock())
            doReturn(true).whenever(spot).isAllowNative()

            assertDoesNotThrow { SpotNativeEditValidation.checkAllowNative(form, spot) }
        }

        @Test
        @DisplayName("ネイティブ設定なし")
        fun isNoNative() {
            val form = SpotNativeEditForm(null, mock())

            assertDoesNotThrow { SpotNativeEditValidation.checkAllowNative(form, spot) }
        }
    }
}
