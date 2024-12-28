package jp.mangaka.ssp.application.service.spot.validation

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.spot.validation.video.VideoSettingValidation
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster
import jp.mangaka.ssp.presentation.controller.spot.form.SpotVideoEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
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

@DisplayName("SpotVideoEditValidationのテスト")
private class SpotVideoEditValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @DisplayName("ビデオ設定のテスト")
    inner class VideoTest {
        @Test
        @DisplayName("@Validは機能しているか")
        fun isValid() {
            val video = VideoSettingValidation(null, emptyList())

            validator.validate(SpotVideoEditValidation(video, true)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("video.") })
            }
        }

        @Test
        @DisplayName("ビデオ設定なしのとき")
        fun isNoNative() {
            validator.validate(SpotVideoEditValidation(null, true)).run {
                assertTrue(none { it.propertyPath.toString().startsWith("video.") })
            }
        }
    }

    @Nested
    @DisplayName("他フォーマットとの相関チェックのテスト")
    inner class FormatsTest {
        @Nested
        @DisplayName("ビデオ設定あり")
        inner class ActiveNativeTest {
            @ParameterizedTest
            @ValueSource(booleans = [true, false])
            @DisplayName("常に正常")
            fun isValid(hasOtherFormat: Boolean) {
                val video = VideoSettingValidation(null, emptyList())

                validator.validate(SpotVideoEditValidation(video, hasOtherFormat)).run {
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
                validator.validate(SpotVideoEditValidation(null, true)).run {
                    assertTrue(none { it.propertyPath.toString() == "formats" })
                }
            }

            @Test
            @DisplayName("他フォーマット設定なし")
            fun isInactiveOtherFormat() {
                validator.validate(SpotVideoEditValidation(null, false)).run {
                    assertTrue(any { it.propertyPath.toString() == "formats" })
                }
            }
        }
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val spot: Spot = mock {
            on { displayType } doReturn DisplayType.overlay
        }
        val site: Site = mock()
        val videoValidation: VideoSettingValidation = mock()
        val existingAspectRatioIds: List<AspectRatioId> = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotVideoEditValidation, VideoSettingValidation)
            every { SpotVideoEditValidation.checkAllowVideo(any(), any()) } returns Unit
            every { VideoSettingValidation.of(any(), any(), any(), any(), any(), any()) } returns videoValidation
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("ビデオ設定あり")
        fun isActiveVideo() {
            val videoForm: VideoSettingForm = mock()
            val form = SpotVideoEditForm(videoForm, mock())

            val actual = SpotVideoEditValidation.of(
                form, UserMaster.UserType.ma_staff, spot, site, true, false, existingAspectRatioIds
            )

            assertEquals(
                SpotVideoEditValidation(videoValidation, false),
                actual
            )
            verify { SpotVideoEditValidation.checkAllowVideo(form, spot) }
            verify {
                VideoSettingValidation.of(
                    videoForm, UserMaster.UserType.ma_staff, site, DisplayType.overlay, true, existingAspectRatioIds
                )
            }
        }

        @Test
        @DisplayName("ビデオ設定なし")
        fun isInactiveVideo() {
            val form = SpotVideoEditForm(null, mock())

            val actual = SpotVideoEditValidation.of(
                form, UserMaster.UserType.ma_staff, spot, site, true, false, existingAspectRatioIds
            )

            assertEquals(SpotVideoEditValidation(null, false), actual)
            verify { SpotVideoEditValidation.checkAllowVideo(form, spot) }
            verify(exactly = 0) { VideoSettingValidation.of(any(), any(), any(), any(), any()) }
        }
    }

    @Nested
    @DisplayName("checkAllowVideoのテスト")
    inner class CheckAllowVideoTest {
        val spot: Spot = mock()

        @Test
        @DisplayName("ビデオ設定が許可されていない広告枠")
        fun isNotAllow() {
            val form = SpotVideoEditForm(mock(), mock())
            doReturn(false).whenever(spot).isAllowVideo()

            assertThrows<CompassManagerException> {
                SpotVideoEditValidation.checkAllowVideo(form, spot)
            }
        }

        @Test
        @DisplayName("ビデオ設定が許可されている広告枠")
        fun isAllow() {
            val form = SpotVideoEditForm(mock(), mock())
            doReturn(true).whenever(spot).isAllowVideo()

            assertDoesNotThrow { SpotVideoEditValidation.checkAllowVideo(form, spot) }
        }

        @Test
        @DisplayName("ビデオ設定なし")
        fun isNoNative() {
            val form = SpotVideoEditForm(null, mock())

            assertDoesNotThrow { SpotVideoEditValidation.checkAllowVideo(form, spot) }
        }
    }
}
