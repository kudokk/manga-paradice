package jp.mangaka.ssp.application.service.spot.validation

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.spot.validation._native.NativeSettingValidation
import jp.mangaka.ssp.application.service.spot.validation.banner.BannerSettingValidation
import jp.mangaka.ssp.application.service.spot.validation.banner.SizeTypeValidation
import jp.mangaka.ssp.application.service.spot.validation.basic.BasicSettingCreateValidation
import jp.mangaka.ssp.application.service.spot.validation.dsp.DspValidation
import jp.mangaka.ssp.application.service.spot.validation.video.VideoDetailValidation
import jp.mangaka.ssp.application.service.spot.validation.video.VideoSettingValidation
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DeliveryMethod
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.DspForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
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
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource

@DisplayName("SpotCreateValidationのテスト")
private class SpotCreateValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @DisplayName("基本設定のテスト")
    inner class BasicTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            val basic = BasicSettingCreateValidation(null, null, null, UpstreamType.none, null, null, null, null, null, null)

            validator.validate(SpotCreateValidation(basic, emptyList(), null, null, null)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("basic") })
            }
        }
    }

    @Nested
    @DisplayName("DSP設定のテスト")
    inner class DspsTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            val dsps = listOf(DspValidation(null, null, null))

            validator.validate(SpotCreateValidation(mock(), dsps, null, null, null)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("dsps[0]") })
            }
        }
    }

    @Nested
    @DisplayName("バナー設定のテスト")
    inner class BannerTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            val banner = BannerSettingValidation(
                listOf(SizeTypeValidation(-1, -1)), false, false, null, null, false, null
            )

            validator.validate(SpotCreateValidation(mock(), emptyList(), banner, null, null)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("banner") })
            }
        }

        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(SpotCreateValidation(mock(), emptyList(), null, null, null)).run {
                assertTrue(none { it.propertyPath.toString().startsWith("banner") })
            }
        }
    }

    @Nested
    @DisplayName("ネイティブ設定のテスト")
    inner class NativeTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            val native = NativeSettingValidation(null, null)

            validator.validate(SpotCreateValidation(mock(), emptyList(), null, native, null)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("native") })
            }
        }

        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(SpotCreateValidation(mock(), emptyList(), null, null, null)).run {
                assertTrue(none { it.propertyPath.toString().startsWith("native") })
            }
        }
    }

    @Nested
    @DisplayName("ビデオ設定のテスト")
    inner class VideoTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            val video = VideoSettingValidation(
                null,
                listOf(VideoDetailValidation(null, null, null, null, null, false))
            )

            validator.validate(SpotCreateValidation(mock(), emptyList(), null, null, video)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("video.") })
            }
        }

        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(SpotCreateValidation(mock(), emptyList(), null, null, null)).run {
                assertTrue(none { it.propertyPath.toString().startsWith("video.") })
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("フォーマット関連のテスト")
    inner class FormatsTest {
        val banner = BannerSettingValidation(emptyList(), true, true, null, null, true, null)
        val native: NativeSettingValidation = mock()
        val video: VideoSettingValidation = mock()

        @Test
        @DisplayName("フォーマット関連項目が未設定")
        fun isEmptyFormats() {
            validator.validate(SpotCreateValidation(mock(), emptyList(), null, null, null)).run {
                assertTrue(any { it.propertyPath.toString() == "formats" })
            }
        }

        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("正常")
        fun isCorrect(
            banner: BannerSettingValidation?,
            native: NativeSettingValidation?,
            video: VideoSettingValidation?
        ) {
            validator.validate(SpotCreateValidation(mock(), mock(), banner, native, video)).run {
                assertTrue(none { it.propertyPath.toString() == "formats" })
            }
        }

        private fun correctParams() = listOf(
            Arguments.of(banner, native, video),
            Arguments.of(banner, null, null),
            Arguments.of(null, native, null),
            Arguments.of(null, null, video)
        )
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkMaStaffOnlyのテスト")
    inner class CheckMaStaffOnlyTest {
        val form: SpotCreateForm = mock()
        val emptyDsps: List<DspForm> = emptyList()
        val notEmptyDsps: List<DspForm> = listOf(mock())

        @ParameterizedTest
        @EnumSource(value = UserType::class, names = ["ma_staff"], mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("不正入力")
        fun isInvalid(userType: UserType) {
            doReturn(notEmptyDsps).whenever(form).dsps

            assertThrows<CompassManagerException> {
                SpotCreateValidation.checkMaStaffOnly(form, userType)
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(dsps: List<DspForm>, userType: UserType) {
            doReturn(dsps).whenever(form).dsps

            assertDoesNotThrow { SpotCreateValidation.checkMaStaffOnly(form, userType) }
        }

        private fun validParams() = listOf(
            Arguments.of(emptyDsps, UserType.ma_staff),
            Arguments.of(notEmptyDsps, UserType.ma_staff),
            Arguments.of(emptyDsps, UserType.agency),
            Arguments.of(emptyDsps, UserType.client),
            Arguments.of(emptyDsps, UserType.other)
        )
    }

    @Nested
    @DisplayName("checkOptionalFormatsのテスト")
    inner class CheckOptionalFormatsTest {
        val basic: BasicSettingCreateForm = mock()
        val native: NativeSettingForm = mock()
        val video: VideoSettingForm = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotCreateValidation)
        }

        @AfterEach
        fun after() {
            unmockkAll()
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("ネイティブ・ビデオを設定できない条件")
        inner class NotAllowOptionalFormatTest {
            @BeforeEach
            fun beforeEach() {
                every { SpotCreateValidation.isNotAllowOptionalFormat(any()) } returns true
            }

            @ParameterizedTest
            @MethodSource("notAllowParams")
            @DisplayName("設定できない条件で設定されている")
            fun isNotAllow(native: NativeSettingForm?, video: VideoSettingForm?) {
                assertThrows<CompassManagerException> {
                    SpotCreateValidation.checkOptionalFormat(SpotCreateForm(basic, mock(), mock(), native, video))
                }
            }

            private fun notAllowParams() = listOf(
                Arguments.of(native, video),
                Arguments.of(native, null),
                Arguments.of(null, video)
            )

            @Test
            @DisplayName("正常")
            fun isAllow() {
                assertDoesNotThrow {
                    SpotCreateValidation.checkOptionalFormat(SpotCreateForm(basic, mock(), mock(), null, null))
                }
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("ネイティブ・ビデオを設定できる条件")
        inner class AllowOptionalFormatTest {
            @BeforeEach
            fun beforeEach() {
                every { SpotCreateValidation.isNotAllowOptionalFormat(any()) } returns false
            }

            @ParameterizedTest
            @MethodSource("allowParams")
            @DisplayName("正常")
            fun isAllow(native: NativeSettingForm?, video: VideoSettingForm?) {
                assertDoesNotThrow {
                    SpotCreateValidation.checkOptionalFormat(SpotCreateForm(basic, mock(), mock(), native, video))
                }
            }

            private fun allowParams() = listOf(
                Arguments.of(native, video),
                Arguments.of(native, null),
                Arguments.of(null, video),
                Arguments.of(null, null)
            )
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("isNotAllowOptionalFormatのテスト")
    inner class IsNotAllowOptionalFormatTest {
        @ParameterizedTest
        @MethodSource("notAllowParams")
        @DisplayName("設定できない条件")
        fun isNotAllow(form: BasicSettingCreateForm) {
            assertTrue(SpotCreateValidation.isNotAllowOptionalFormat(form))
        }

        private fun notAllowParams() = listOf(
            form(UpstreamType.prebidjs, null, false),
            form(UpstreamType.none, DeliveryMethod.sdk, false),
            form(UpstreamType.none, DeliveryMethod.js, true)
        )

        @ParameterizedTest
        @MethodSource("allowParams")
        @DisplayName("設定できる条件")
        fun isAllow(form: BasicSettingCreateForm) {
            assertFalse(SpotCreateValidation.isNotAllowOptionalFormat(form))
        }

        private fun allowParams() = listOf(
            form(UpstreamType.none, null, false),
            form(UpstreamType.none, DeliveryMethod.js, false),
        )

        private fun form(
            upstreamType: UpstreamType,
            deliveryMethod: DeliveryMethod?,
            isAmp: Boolean
        ): BasicSettingCreateForm = mock {
            on { this.upstreamType } doReturn upstreamType
            on { this.deliveryMethod } doReturn deliveryMethod
            on { this.isAmp } doReturn isAmp
        }
    }
}
