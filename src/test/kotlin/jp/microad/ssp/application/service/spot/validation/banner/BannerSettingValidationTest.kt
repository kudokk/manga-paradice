package jp.mangaka.ssp.application.service.spot.validation.banner

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.spot.validation.CloseButtonValidation
import jp.mangaka.ssp.application.service.spot.validation.CloseButtonValidation.ColorValidation
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.BannerSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm.ColorForm
import jp.mangaka.ssp.presentation.controller.spot.form.SizeTypeForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotMaxSizeForm
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
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import io.mockk.verify as verifyK

@DisplayName("BannerSettingValidationのテスト")
private class BannerSettingValidationTest {
    companion object {
        val decorationId = DecorationId(1)
    }

    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("サイズ種別リストのテスト")
    inner class SizeTypesTest {
        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(
                BannerSettingValidation(emptyList(), false, false, null, null, false, null)
            ).run { assertTrue(any { it.propertyPath.toString() == "sizeTypes" }) }
        }

        @Test
        @DisplayName("縦幅が0のサイズが含まれている")
        fun isContainsHeightZero() {
            val sizeTypes = listOf(SizeTypeValidation(10, 20), SizeTypeValidation(30, 0), SizeTypeValidation(50, 60))
            validator.validate(
                BannerSettingValidation(sizeTypes, false, false, null, null, false, null)
            ).run { assertTrue(any { it.propertyPath.toString() == "sizeTypes" }) }
        }

        @Test
        @DisplayName("サイト種別がPC かつ 横幅0のサイズが含まれている")
        fun isPcAndContainsWidthZero() {
            val sizeTypes = listOf(SizeTypeValidation(10, 20), SizeTypeValidation(0, 40), SizeTypeValidation(50, 60))
            validator.validate(
                BannerSettingValidation(sizeTypes, false, false, null, null, true, null)
            ).run { assertTrue(any { it.propertyPath.toString() == "sizeTypes" }) }
        }

        @Test
        @DisplayName("固定表示の横幅を超えるサイズが存在する")
        fun isOverSpotMaxWidth() {
            val sizeTypes = listOf(SizeTypeValidation(10, 20), SizeTypeValidation(31, 40), SizeTypeValidation(50, 60))
            validator.validate(
                BannerSettingValidation(sizeTypes, false, false, null, null, false, SpotMaxSizeForm(30, 40))
            ).run { assertTrue(any { it.propertyPath.toString() == "sizeTypes" }) }
        }

        @Test
        @DisplayName("固定表示の縦幅を超えるサイズが存在する")
        fun isOverSpotMaxHeight() {
            val sizeTypes = listOf(SizeTypeValidation(10, 20), SizeTypeValidation(30, 201), SizeTypeValidation(50, 60))
            validator.validate(
                BannerSettingValidation(sizeTypes, false, false, null, null, false, SpotMaxSizeForm(30, 40))
            ).run { assertTrue(any { it.propertyPath.toString() == "sizeTypes" }) }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(sizeTypes: List<SizeTypeValidation>, isPc: Boolean, spotMaxSize: SpotMaxSizeForm?) {
            validator.validate(
                BannerSettingValidation(sizeTypes, false, false, null, null, isPc, spotMaxSize)
            ).run { assertTrue(none { it.propertyPath.toString() == "sizeTypes" }) }
        }

        private fun validParams() = listOf(
            Arguments.of(listOf(SizeTypeValidation(0, 1), SizeTypeValidation(1, 1)), false, null),
            Arguments.of(
                listOf(SizeTypeValidation(10, 20), SizeTypeValidation(30, 40)), true, SpotMaxSizeForm(null, null)
            ),
            Arguments.of(
                listOf(SizeTypeValidation(10, 20), SizeTypeValidation(9, 19)), true, SpotMaxSizeForm(10, 20)
            )
        )
    }

    @Nested
    @DisplayName("サイズ種別リストの要素のテスト")
    inner class SizeTypesElementTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            val sizeTypes = listOf(SizeTypeValidation(-1, -1))

            validator.validate(BannerSettingValidation(sizeTypes, false, false, null, null, false, null)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("sizeTypes[0]") })
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

            validator.validate(BannerSettingValidation(emptyList(), false, false, closeButton, null, false, null)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("closeButton") })
            }
        }

        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(BannerSettingValidation(emptyList(), false, false, null, null, false, null)).run {
                assertTrue(none { it.propertyPath.toString().startsWith("closeButton") })
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("ファクトリ関数のテスト")
    inner class OfTest {
        val spotMaxSizeForm = SpotMaxSizeForm(10, 20)

        init {
            mockkObject(BannerSettingValidation)

            every { BannerSettingValidation.checkMaStaffOnly(any(), any(), any()) } returns Unit
            every { BannerSettingValidation.checkIsScalable(any(), any(), any(), any(), any()) } returns Unit
            every { BannerSettingValidation.checkIsDisplayScrolling(any(), any(), any()) } returns Unit
            every { BannerSettingValidation.checkCloseButton(any(), any(), any()) } returns Unit
            every { BannerSettingValidation.checkDecorationId(any(), any(), any()) } returns Unit
        }

        @AfterAll
        fun afterAll() {
            unmockkAll()
        }

        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("正常")
        fun isCorrect(site: Site?, isPcSite: Boolean) {
            val form = BannerSettingForm(
                listOf(SizeTypeForm(10, 20), SizeTypeForm(30, 40)),
                true,
                false,
                CloseButtonForm(
                    1, 100, ColorForm(0, 0, 0, 0.0), ColorForm(50, 100, 150, 0.5), ColorForm(255, 255, 255, 1.0)
                ),
                decorationId
            )

            val actual = BannerSettingValidation.of(
                form, UserType.agency, site, UpstreamType.prebidjs, true, false, true, true, spotMaxSizeForm, true
            )

            assertEquals(
                BannerSettingValidation(
                    listOf(SizeTypeValidation(10, 20), SizeTypeValidation(30, 40)),
                    true,
                    false,
                    CloseButtonValidation(
                        1,
                        100,
                        ColorValidation(0, 0, 0, 0.0),
                        ColorValidation(50, 100, 150, 0.5),
                        ColorValidation(255, 255, 255, 1.0)
                    ),
                    decorationId,
                    isPcSite,
                    spotMaxSizeForm
                ),
                actual
            )

            verifyK { BannerSettingValidation.checkMaStaffOnly(form, UserType.agency, true) }
            verifyK { BannerSettingValidation.checkIsScalable(form, UpstreamType.prebidjs, true, true, isPcSite) }
            verifyK {
                BannerSettingValidation.checkCloseButton(form, UpstreamType.prebidjs, true)
            }
            verifyK { BannerSettingValidation.checkDecorationId(form, false, true) }
        }

        private fun correctParams() = listOf(
            Arguments.of(null, false),
            Arguments.of(site(PlatformId.pc), true),
            Arguments.of(site(PlatformId.smartPhone), false)
        )

        private fun site(platformId: PlatformId): Site = mock {
            on { this.platformId } doReturn platformId
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
        fun isInvalid(isScalable: Boolean, closeButton: CloseButtonForm?, userType: UserType) {
            assertThrows<CompassManagerException> {
                BannerSettingValidation.checkMaStaffOnly(form(isScalable, closeButton), userType, false)
            }
        }

        private fun invalidParams() = listOf(
            // マイクロアド社員以外は設定不可
            Arguments.of(true, null, UserType.agency),
            Arguments.of(true, null, UserType.client),
            Arguments.of(true, null, UserType.other),
            Arguments.of(false, closeButton, UserType.agency),
            Arguments.of(false, closeButton, UserType.client),
            Arguments.of(false, closeButton, UserType.other)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(isScalable: Boolean, closeButton: CloseButtonForm?, userType: UserType, isSkip: Boolean) {
            assertDoesNotThrow {
                BannerSettingValidation.checkMaStaffOnly(form(isScalable, closeButton), userType, isSkip)
            }
        }

        private fun validParams() = listOf(
            Arguments.of(true, closeButton, UserType.ma_staff, false),
            Arguments.of(true, closeButton, UserType.agency, true),
            Arguments.of(false, null, UserType.ma_staff, false),
            Arguments.of(false, null, UserType.agency, false),
            Arguments.of(false, null, UserType.client, false),
            Arguments.of(false, null, UserType.other, false)
        )

        private fun form(isScalable: Boolean, closeButton: CloseButtonForm?): BannerSettingForm = mock {
            on { this.isScalable } doReturn isScalable
            on { this.closeButton } doReturn closeButton
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkIsScalableのテスト")
    inner class CheckIsScalableTest {
        @ParameterizedTest
        @MethodSource("invalidParams")
        @DisplayName("不正入力")
        fun isInvalid(upstreamType: UpstreamType, isAmp: Boolean, isJsOrInline: Boolean, isSpSite: Boolean) {
            assertThrows<CompassManagerException> {
                BannerSettingValidation.checkIsScalable(form(true), upstreamType, isAmp, isJsOrInline, isSpSite)
            }
        }

        private fun invalidParams() = listOf(
            // ヘッダービディングがnoneでない場合は設定不可
            Arguments.of(UpstreamType.prebidjs, true, true, true),
            Arguments.of(UpstreamType.prebidjs, true, true, false),
            Arguments.of(UpstreamType.prebidjs, true, false, true),
            Arguments.of(UpstreamType.prebidjs, true, false, false),
            Arguments.of(UpstreamType.prebidjs, false, true, true),
            Arguments.of(UpstreamType.prebidjs, false, true, false),
            Arguments.of(UpstreamType.prebidjs, false, false, true),
            Arguments.of(UpstreamType.prebidjs, false, false, false),
            // AMP対応がオンの場合は設定不可
            Arguments.of(UpstreamType.none, true, true, true),
            Arguments.of(UpstreamType.none, true, true, false),
            Arguments.of(UpstreamType.none, true, false, true),
            Arguments.of(UpstreamType.none, true, false, false),
            // 広告配信方法がJS もしくは 表示種別がインライン でない場合は設定不可
            Arguments.of(UpstreamType.none, false, false, true),
            Arguments.of(UpstreamType.none, false, false, false),
            // サイトがスマートフォンでない場合は設定不可
            Arguments.of(UpstreamType.none, false, true, true)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(
            isScalable: Boolean, upstreamType: UpstreamType, isAmp: Boolean, isJsOrInline: Boolean,
            isPcSite: Boolean
        ) {
            assertDoesNotThrow {
                BannerSettingValidation.checkIsScalable(
                    form(isScalable), upstreamType, isAmp, isJsOrInline, isPcSite
                )
            }
        }

        private fun validParams() = listOf(
            // 拡大オフのときは未チェック
            Arguments.of(false, UpstreamType.prebidjs, true, true, true),
            Arguments.of(false, UpstreamType.none, true, true, true),
            Arguments.of(false, UpstreamType.none, false, true, true),
            Arguments.of(false, UpstreamType.none, false, false, false),
            // ヘッダービディングがnone かつ AMP対応がオフ かつ (広告配信方法がJS もしくは 表示種別がインライン のいずれか)
            // かつ サイトがスマートフォン の場合は設定可
            Arguments.of(true, UpstreamType.none, false, true, false)
        )

        private fun form(isScalable: Boolean): BannerSettingForm = mock {
            on { this.isScalable } doReturn isScalable
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkIsDisplayScrollingのテスト")
    inner class CheckIsDisplayScrollingTest {
        @ParameterizedTest
        @MethodSource("invalidParams")
        @DisplayName("不正入力")
        fun isInvalid(isDisplayControl: Boolean, isJsByOverlay: Boolean) {
            assertThrows<CompassManagerException> {
                // スクロール中表示は画面表示と値が反転しているため、falseのときオン
                BannerSettingValidation.checkIsDisplayScrolling(form(false), isDisplayControl, isJsByOverlay)
            }
        }

        private fun invalidParams() = listOf(
            // 表示制御がオフのとき設定不可
            Arguments.of(false, true),
            Arguments.of(false, false),
            // 広告配信方法がJS かつ 表示種別がオーバーレイ でない場合は設定不可
            Arguments.of(true, false)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(isDisplayScrolling: Boolean, isDisplayControl: Boolean, isJsByOverlay: Boolean) {
            assertDoesNotThrow {
                BannerSettingValidation.checkIsDisplayScrolling(
                    form(isDisplayScrolling), isDisplayControl, isJsByOverlay
                )
            }
        }

        private fun validParams() = listOf(
            // スクロール中表示がオフの場合は未チェック
            Arguments.of(true, true, true),
            Arguments.of(true, true, false),
            Arguments.of(true, false, true),
            Arguments.of(true, false, false),
            // 広告配信方法がJS かつ 表示種別がオーバーレイ かつ 表示制御がオン の場合のみ設定可
            Arguments.of(false, true, true)
        )

        private fun form(isDisplayScrolling: Boolean): BannerSettingForm = mock {
            on { this.isDisplayScrolling } doReturn isDisplayScrolling
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkCloseButtonのテスト")
    inner class CheckCloseButtonTest {
        val closeButton: CloseButtonForm = mock()

        @ParameterizedTest
        @MethodSource("invalidParams")
        @DisplayName("不正入力")
        fun isInvalid(upstreamType: UpstreamType, isJsByOverlay: Boolean) {
            assertThrows<CompassManagerException> {
                BannerSettingValidation.checkCloseButton(form(closeButton), upstreamType, isJsByOverlay)
            }
        }

        private fun invalidParams() = listOf(
            // ヘッダービディングがnoneでない場合は設定不可
            Arguments.of(UpstreamType.prebidjs, true),
            Arguments.of(UpstreamType.prebidjs, false),
            // 広告配信方法がJS かつ 表示種別がオーバーレイ でない場合は設定不可
            Arguments.of(UpstreamType.none, false)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(closeButton: CloseButtonForm?, upstreamType: UpstreamType, isJsByOverlay: Boolean) {
            assertDoesNotThrow {
                BannerSettingValidation.checkCloseButton(form(closeButton), upstreamType, isJsByOverlay)
            }
        }

        private fun validParams() = listOf(
            // 関連項目が未入力の場合は未チェック
            Arguments.of(null, UpstreamType.prebidjs, true),
            Arguments.of(null, UpstreamType.none, false),
            // ヘッダービディングがnone かつ 広告配信方法がJS かつ 表示種別がオーバーレイ の場合は設定可
            Arguments.of(closeButton, UpstreamType.none, true)
        )

        private fun form(closeButton: CloseButtonForm?): BannerSettingForm = mock {
            on { this.closeButton } doReturn closeButton
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkDecorationIdのテスト")
    inner class CheckDecorationIdTest {
        @ParameterizedTest
        @MethodSource("invalidParams")
        @DisplayName("不正入力")
        fun isInvalid(isDisplayControl: Boolean, isJsByOverlay: Boolean) {
            assertThrows<CompassManagerException> {
                BannerSettingValidation.checkDecorationId(form(decorationId), isDisplayControl, isJsByOverlay)
            }
        }

        private fun invalidParams() = listOf(
            // 表示制御:オフの場合は設定不可
            Arguments.of(false, true),
            Arguments.of(false, false),
            // 広告配信方法がJS かつ 表示種別がオーバーレイ でない場合はせってい不可
            Arguments.of(true, false)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(decorationId: DecorationId?, isDisplayControl: Boolean, isJsByOverlay: Boolean) {
            assertDoesNotThrow {
                BannerSettingValidation.checkDecorationId(form(decorationId), isDisplayControl, isJsByOverlay)
            }
        }

        private fun validParams() = listOf(
            // 関連項目が未入力の場合は未チェック
            Arguments.of(null, true, true),
            Arguments.of(null, true, false),
            Arguments.of(null, false, true),
            Arguments.of(null, false, false),
            // 表示制御がオン かつ 広告配信方法がJS かつ 表示種別がオーバーレイ の場合は設定可
            Arguments.of(decorationId, true, true)
        )

        private fun form(decorationId: DecorationId?): BannerSettingForm = mock {
            on { this.decorationId } doReturn decorationId
        }
    }
}
