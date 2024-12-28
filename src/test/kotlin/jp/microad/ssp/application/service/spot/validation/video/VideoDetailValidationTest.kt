package jp.mangaka.ssp.application.service.spot.validation.video

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.spot.validation.CloseButtonValidation
import jp.mangaka.ssp.application.service.spot.validation.video.VideoDisplayPositionValidation.VideoDisplayPositionElementValidation
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio16to9
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm.VideoDisplayPositionForm
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
import org.junit.jupiter.params.provider.ValueSource
import io.mockk.verify as verifyK

@DisplayName("VideoDetailValidationのテスト")
private class VideoDetailValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @DisplayName("アスペクト比IDのテスト")
    inner class AspectRatioIdTest {
        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(VideoDetailValidation(null, null, null, null, null, false)).run {
                assertTrue(any { it.propertyPath.toString() == "aspectRatioId" })
            }
        }

        @Test
        @DisplayName("正常")
        fun isValid() {
            validator.validate(VideoDetailValidation(AspectRatioId(1), null, null, null, null, false)).run {
                assertTrue(none { it.propertyPath.toString() == "aspectRatioId" })
            }
        }
    }

    @Nested
    @DisplayName("動画プレイヤー横幅")
    inner class VideoPlayerWidthTest {
        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(VideoDetailValidation(null, null, null, null, null, false)).run {
                assertTrue(any { it.propertyPath.toString() == "videoPlayerWidth" })
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, 0, 65536])
        @DisplayName("範囲外の値")
        fun isOutOfRange(value: Int) {
            validator.validate(VideoDetailValidation(null, value, null, null, null, false)).run {
                assertTrue(any { it.propertyPath.toString() == "videoPlayerWidth" })
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 2, 65534, 65535])
        @DisplayName("正常")
        fun isValid(value: Int) {
            validator.validate(VideoDetailValidation(null, value, null, null, null, false)).run {
                assertTrue(none { it.propertyPath.toString() == "videoPlayerWidth" })
            }
        }
    }

    @Nested
    @DisplayName("閉じるボタンのテスト")
    inner class CloseButtonTest {
        @Test
        @DisplayName("@Validが機能しているか")
        fun isValid() {
            val closeButton = CloseButtonValidation(null, null, null, null, null)

            validator.validate(VideoDetailValidation(null, null, closeButton, null, null, false)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("closeButton.") })
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("表示位置のテスト")
    inner class DisplayPositionTest {
        @Test
        @DisplayName("@Validが機能しているか")
        fun isValid() {
            val displayPosition = VideoDisplayPositionValidation(VideoDisplayPositionElementValidation(-1), null)

            validator.validate(VideoDetailValidation(null, null, null, displayPosition, null, false)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("displayPosition.") })
            }
        }

        @ParameterizedTest
        @MethodSource("overlayDisplayControlledAndEmptyParams")
        @DisplayName("表示制御オンのオーバーレイ広告で未設定")
        fun isOverlayDisplayControlledAndEmpty(displayPosition: VideoDisplayPositionValidation?) {
            validator.validate(VideoDetailValidation(null, null, null, displayPosition, null, true)).run {
                assertTrue(any { it.propertyPath.toString() == "displayPosition" })
            }
        }

        private fun overlayDisplayControlledAndEmptyParams() = listOf(null, mockDisplayPosition(false))

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(
            displayPosition: VideoDisplayPositionValidation?,
            isOverlayDisplayControl: Boolean
        ) {
            validator.validate(VideoDetailValidation(null, null, null, displayPosition, null, isOverlayDisplayControl))
                .run { assertTrue(none { it.propertyPath.toString() == "displayPosition" }) }
        }

        // 表示制御オンのオーバーレイ広告以外で表示位置設定ありは生成時のチェックで弾かれている想定
        private fun validParams() = listOf(
            Arguments.of(null, false),
            Arguments.of(mockDisplayPosition(false), false),
            Arguments.of(mockDisplayPosition(true), true),
        )

        private fun mockDisplayPosition(isNotEmpty: Boolean): VideoDisplayPositionValidation =
            mock {
                on { this.isNotEmpty() } doReturn isNotEmpty
            }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("フロアCPMのテスト")
    inner class FloorCpmTest {
        @ParameterizedTest
        @ValueSource(
            strings = [
                // 範囲外（最大値超過は桁数チェックに掛かる）
                "-0.00000001",
                // 桁数不正
                "12345678901.0",
                "1.123456789"
            ]
        )
        @DisplayName("不正な値")
        fun isInvalid(value: String) {
            validator.validate(VideoDetailValidation(null, null, null, null, value.toBigDecimal(), false)).run {
                assertTrue(any { it.propertyPath.toString() == "floorCpm" })
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["0.00000000", "0.00000001", "9999999999.99999998", "9999999999.99999999"])
        @DisplayName("正常")
        fun isValid(value: String) {
            validator.validate(VideoDetailValidation(null, null, null, null, value.toBigDecimal(), false)).run {
                assertTrue(none { it.propertyPath.toString() == "floorCpm" })
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val closeButton: CloseButtonValidation = mock()
        val displayPosition: VideoDisplayPositionValidation = mock()
        val closeButtonForm: CloseButtonForm = mock()
        val displayPositionForm: VideoDisplayPositionForm = mock()
        val platformId: PlatformId = mock()
        val site: Site = mock {
            on { this.platformId } doReturn platformId
        }

        @BeforeEach
        fun beforeEach() {
            mockkObject(VideoDetailValidation, CloseButtonValidation, VideoDisplayPositionValidation)

            every { VideoDetailValidation.checkMaStaffOnly(any(), any(), any()) } returns Unit
            every { VideoDetailValidation.checkNonPcSiteOnly(any(), any()) } returns Unit
            every { VideoDetailValidation.checkOverlayOnly(any(), any()) } returns Unit
            every { VideoDetailValidation.checkOverlayDisplayControlledOnly(any(), any()) } returns Unit
            every { CloseButtonValidation.of(any()) } returns closeButton
            every { VideoDisplayPositionValidation.of(any()) } returns displayPosition
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @ParameterizedTest
        @MethodSource("fullParams")
        @DisplayName("全項目設定あり")
        fun isFull(
            displayType: DisplayType?,
            isDisplayControl: Boolean,
            isOverlay: Boolean,
            isOverlayDisplayControlled: Boolean
        ) {
            val form = VideoDetailForm(
                aspectRatio16to9,
                true,
                100,
                closeButtonForm,
                displayPositionForm,
                true,
                true,
                1000.toBigDecimal(),
                mock()
            )
            doReturn(true).whenever(platformId).isPc()

            val actual = VideoDetailValidation.of(form, UserType.ma_staff, site, displayType, isDisplayControl, true)

            assertEquals(
                VideoDetailValidation(
                    aspectRatio16to9, 100, closeButton, displayPosition, 1000.toBigDecimal(),
                    isOverlayDisplayControlled
                ),
                actual
            )

            verifyK { VideoDetailValidation.checkMaStaffOnly(form, UserType.ma_staff, true) }
            verifyK { VideoDetailValidation.checkNonPcSiteOnly(form, true) }
            verifyK { VideoDetailValidation.checkOverlayOnly(form, isOverlay) }
            verifyK { VideoDetailValidation.checkOverlayDisplayControlledOnly(form, isOverlayDisplayControlled) }
            verifyK { CloseButtonValidation.of(closeButtonForm) }
            verifyK { VideoDisplayPositionValidation.of(displayPositionForm) }
        }

        private fun fullParams() = listOf(
            Arguments.of(null, true, false, false),
            Arguments.of(null, false, false, false),
            Arguments.of(DisplayType.inline, true, false, false),
            Arguments.of(DisplayType.inline, false, false, false),
            Arguments.of(DisplayType.overlay, true, true, true),
            Arguments.of(DisplayType.overlay, false, true, false),
            Arguments.of(DisplayType.interstitial, true, false, false),
            Arguments.of(DisplayType.interstitial, false, false, false)
        )

        @Test
        @DisplayName("必須項目のみ")
        fun isNotFull() {
            val closeButtonForm: CloseButtonForm = mock()
            val displayPositionForm: VideoDisplayPositionForm = mock()
            val form = VideoDetailForm(null, true, null, null, null, true, true, null, null)
            doReturn(false).whenever(platformId).isPc()

            val actual = VideoDetailValidation.of(form, UserType.other, site, null, false, false)

            assertEquals(VideoDetailValidation(null, null, null, null, null, false), actual)

            verifyK { VideoDetailValidation.checkMaStaffOnly(form, UserType.other, false) }
            verifyK { VideoDetailValidation.checkNonPcSiteOnly(form, false) }
            verifyK { VideoDetailValidation.checkOverlayOnly(form, false) }
            verifyK { VideoDetailValidation.checkOverlayDisplayControlledOnly(form, false) }
            verifyK(exactly = 0) { CloseButtonValidation.of(any()) }
            verifyK(exactly = 0) { VideoDisplayPositionValidation.of(any()) }
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
        fun isInvalid(
            userType: UserType,
            closeButton: CloseButtonForm?,
            isScalable: Boolean,
            isExistVideo: Boolean
        ) {
            val form = form(isScalable, closeButton)

            assertThrows<CompassManagerException> {
                VideoDetailValidation.checkMaStaffOnly(form, userType, isExistVideo)
            }
        }

        private fun invalidParams() = listOf(
            Arguments.of(UserType.agency, closeButton, false, false),
            Arguments.of(UserType.client, null, true, false),
            Arguments.of(UserType.other, null, true, false)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(
            userType: UserType,
            closeButton: CloseButtonForm?,
            isScalable: Boolean,
            isExistVideo: Boolean
        ) {
            val form = form(isScalable, closeButton)

            assertDoesNotThrow { VideoDetailValidation.checkMaStaffOnly(form, userType, isExistVideo) }
        }

        private fun validParams() = listOf(
            // 社員
            Arguments.of(UserType.ma_staff, null, true, false),
            Arguments.of(UserType.ma_staff, closeButton, false, false),
            // 既存設定あり
            Arguments.of(UserType.agency, closeButton, false, true),
            Arguments.of(UserType.client, closeButton, false, true),
            Arguments.of(UserType.other, closeButton, false, true),
            // 社員限定項目未設定
            Arguments.of(UserType.agency, null, false, false),
            Arguments.of(UserType.client, null, false, false),
            Arguments.of(UserType.other, null, false, false)
        )

        private fun form(isScalable: Boolean, closeButton: CloseButtonForm?): VideoDetailForm = mock {
            on { this.isScalable } doReturn isScalable
            on { this.closeButton } doReturn closeButton
        }
    }

    @Nested
    @DisplayName("checkNonPcSiteOnlyのテスト")
    inner class CheckNonPcSiteOnlyTest {
        val wipeAspectRatioId: AspectRatioId = mock {
            on { this.isWipeVideo() } doReturn true
        }
        val notWipeAspectRatioId: AspectRatioId = mock {
            on { this.isWipeVideo() } doReturn false
        }

        val alwaysAllowedForms = listOf(
            form(null, false),
            form(wipeAspectRatioId, false),
            form(notWipeAspectRatioId, false)
        )

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("PCサイトのとき")
        inner class PcSiteTest {
            @ParameterizedTest
            @MethodSource("validParams")
            @DisplayName("正常")
            fun isValid(form: VideoDetailForm) {
                assertDoesNotThrow { VideoDetailValidation.checkNonPcSiteOnly(form, true) }
            }

            private fun validParams() = alwaysAllowedForms
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("PCサイト以外のとき")
        inner class NotPcSiteTest {
            @ParameterizedTest
            @MethodSource("validParams")
            @DisplayName("正常")
            fun isValid(form: VideoDetailForm) {
                assertDoesNotThrow { VideoDetailValidation.checkNonPcSiteOnly(form, false) }
            }

            private fun validParams() = alwaysAllowedForms
        }

        private fun form(aspectRatioId: AspectRatioId?, isScalable: Boolean): VideoDetailForm = mock {
            on { this.aspectRatioId } doReturn aspectRatioId
            on { this.isScalable } doReturn isScalable
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkOverlayOnlyのテスト")
    inner class CheckOverlayOnlyTest {
        @Test
        @DisplayName("オーバーレイ広告以外で設定できない項目の入力があるとき")
        fun isInvalid() {
            assertThrows<CompassManagerException> {
                VideoDetailValidation.checkOverlayOnly(form(mock()), false)
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(form: VideoDetailForm, isOverlay: Boolean) {
            assertDoesNotThrow { VideoDetailValidation.checkOverlayOnly(form, isOverlay) }
        }

        private fun validParams() = listOf(
            Arguments.of(form(null), true),
            Arguments.of(form(null), false),
            Arguments.of(form(mock()), true)
        )

        private fun form(closeButton: CloseButtonForm?): VideoDetailForm = mock {
            on { this.closeButton } doReturn closeButton
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkOverlayDisplayControlledOnlyのテスト")
    inner class CheckOverlayDisplayControlledOnlyTest {
        @ParameterizedTest
        @MethodSource("invalidParams")
        @DisplayName("表示制御オンのオーバーレイ広告以外で設定できない項目の入力があるとき")
        fun isInvalid(form: VideoDetailForm, isOverlayDisplayControlled: Boolean) {
            assertThrows<CompassManagerException> {
                VideoDetailValidation.checkOverlayDisplayControlledOnly(form, isOverlayDisplayControlled)
            }
        }

        private fun invalidParams() = listOf(
            Arguments.of(form(mock(), false, false), false),
            Arguments.of(form(null, true, false), false),
            Arguments.of(form(null, false, true), false),
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(form: VideoDetailForm, isOverlayDisplayControlled: Boolean) {
            assertDoesNotThrow {
                VideoDetailValidation.checkOverlayDisplayControlledOnly(form, isOverlayDisplayControlled)
            }
        }

        private fun validParams() = listOf(
            Arguments.of(form(mock(), false, false), true),
            Arguments.of(form(null, true, true), true),
            Arguments.of(form(null, false, false), false),
        )

        private fun form(
            displayPosition: VideoDisplayPositionForm?,
            isRoundedRectangle: Boolean,
            isAllowedDrag: Boolean
        ): VideoDetailForm = mock {
            on { this.displayPosition } doReturn displayPosition
            on { this.isRoundedRectangle } doReturn isRoundedRectangle
            on { this.isAllowedDrag } doReturn isAllowedDrag
        }
    }
}
