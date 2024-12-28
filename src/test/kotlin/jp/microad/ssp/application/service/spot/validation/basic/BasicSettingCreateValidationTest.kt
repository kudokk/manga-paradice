package jp.mangaka.ssp.application.service.spot.validation.basic

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DeliveryMethod
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotMaxSizeForm
import jp.mangaka.ssp.util.TestUtils
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.apache.commons.lang3.RandomStringUtils
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
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import io.mockk.verify as verifyK

@DisplayName("BasicSettingCreateValidationのテスト")
private class BasicSettingCreateValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("広告枠名のテスト")
    inner class SpotNameTest {
        @ParameterizedTest
        @MethodSource("jp.mangaka.ssp.util.TestUtils#emptyStrings")
        @DisplayName("未入力")
        fun isEmpty(value: String?) {
            validator.validate(sut(spotName = value)).run {
                assertTrue(any { it.propertyPath.toString() == "spotName" })
            }
        }

        @Test
        @DisplayName("文字数超過")
        fun isTooLong() {
            validator.validate(sut(spotName = RandomStringUtils.random(86))).run {
                assertTrue(any { it.propertyPath.toString() == "spotName" })
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 85])
        @DisplayName("正常")
        fun isValid(length: Int) {
            validator.validate(sut(spotName = RandomStringUtils.random(length))).run {
                assertTrue(none { it.propertyPath.toString() == "spotName" })
            }
        }
    }

    @Nested
    @DisplayName("サイトIDのテスト")
    inner class SiteIdTest {
        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(sut(siteId = null)).run {
                assertTrue(any { it.propertyPath.toString() == "siteId" })
            }
        }

        @Test
        @DisplayName("正常")
        fun isValid() {
            validator.validate(sut(siteId = SiteId(100))).run {
                assertTrue(none { it.propertyPath.toString() == "siteId" })
            }
        }
    }

    @Nested
    @DisplayName("広告枠ステータスのテスト")
    inner class SpotStatusTest {
        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(sut(spotStatus = null)).run {
                assertTrue(any { it.propertyPath.toString() == "spotStatus" })
            }
        }

        @Test
        @DisplayName("正常")
        fun isValid() {
            validator.validate(sut(spotStatus = SpotStatus.active)).run {
                assertTrue(none { it.propertyPath.toString() == "spotStatus" })
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("ヘッダービディング通貨IDのテスト")
    inner class CurrencyIdTest {
        @Test
        @DisplayName("ヘッダービディングありで未入力")
        fun isActiveHeaderBiddingAndEmpty() {
            validator.validate(sut(upstreamType = UpstreamType.prebidjs, currencyId = null)).run {
                assertTrue(any { it.propertyPath.toString() == "currencyId" })
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(upstreamType: UpstreamType, currencyId: CurrencyId?) {
            validator.validate(sut(upstreamType = upstreamType, currencyId = currencyId)).run {
                assertTrue(none { it.propertyPath.toString() == "currencyId" })
            }
        }

        private fun validParams() = listOf(
            Arguments.of(UpstreamType.none, null),
            Arguments.of(UpstreamType.prebidjs, CurrencyId(100))
        )
    }

    @Nested
    @DisplayName("広告配信方法")
    inner class DeliveryMethodTest {
        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(sut(deliveryMethod = null)).run {
                assertTrue(any { it.propertyPath.toString() == "deliveryMethod" })
            }
        }

        @Test
        @DisplayName("正常")
        fun isValid() {
            validator.validate(sut(deliveryMethod = DeliveryMethod.js)).run {
                assertTrue(none { it.propertyPath.toString() == "deliveryMethod" })
            }
        }
    }

    @Nested
    @DisplayName("表示種別のテスト")
    inner class DisplayTypeTest {
        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(sut(displayType = null)).run {
                assertTrue(any { it.propertyPath.toString() == "displayType" })
            }
        }

        @Test
        @DisplayName("正常")
        fun isValid() {
            validator.validate(sut(displayType = DisplayType.overlay)).run {
                assertTrue(none { it.propertyPath.toString() == "displayType" })
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("固定表示 (横×縦)のテスト")
    inner class SpotMaxSizeTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            validator.validate(sut(spotMaxSize = SpotMaxSizeValidation(-1, -1))).run {
                assertTrue(any { it.propertyPath.toString().startsWith("spotMaxSize") })
            }
        }

        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(sut(spotMaxSize = null)).run {
                assertTrue(none { it.propertyPath.toString().startsWith("spotMaxSize") })
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("説明のテスト")
    inner class DescriptionTest {
        @Test
        @DisplayName("文字数超過")
        fun isTooLong() {
            validator.validate(sut(description = RandomStringUtils.random(86))).run {
                assertTrue(any { it.propertyPath.toString() == "description" })
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(value: String?) {
            validator.validate(sut(description = value)).run {
                assertTrue(none { it.propertyPath.toString() == "description" })
            }
        }

        private fun validParams() = TestUtils.emptyStrings() + listOf("a", RandomStringUtils.random(85))
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("掲載面URLのテスト")
    inner class PageUrlTest {
        @Test
        @DisplayName("形式不正")
        fun isInvalidFormat() {
            validator.validate(sut(pageUrl = "aaa")).run {
                assertTrue(any { it.propertyPath.toString() == "pageUrl" })
            }
        }

        @Test
        @DisplayName("文字数超過")
        fun isTooLong() {
            validator.validate(sut(pageUrl = "http://" + RandomStringUtils.random(1018))).run {
                assertTrue(any { it.propertyPath.toString() == "pageUrl" })
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(value: String?) {
            validator.validate(sut(pageUrl = value)).run {
                assertTrue(none { it.propertyPath.toString() == "pageUrl" })
            }
        }

        private fun validParams() = listOf(null, "http://" + RandomStringUtils.randomAlphabetic(1017))
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("ファクトリ関数のテスト")
    inner class OfTest {
        val form = BasicSettingCreateForm(
            SiteId(1), "spot", SpotStatus.active, UpstreamType.prebidjs, CurrencyId(1), DeliveryMethod.js,
            DisplayType.overlay, true, false, SpotMaxSizeForm(100, 200), "description", "pageUrl"
        )
        val site: Site = mock()

        init {
            mockkObject(BasicSettingCreateValidation)

            every { BasicSettingCreateValidation.checkMaStaffOnly(any(), any()) } returns Unit
            every { BasicSettingCreateValidation.checkSpotStatus(any()) } returns Unit
            every { BasicSettingCreateValidation.checkCurrencyId(any()) } returns Unit
            every { BasicSettingCreateValidation.checkDeliveryMethod(any(), any()) } returns Unit
            every { BasicSettingCreateValidation.checkDisplayType(any()) } returns Unit
            every { BasicSettingCreateValidation.checkIsDisplayControl(any()) } returns Unit
            every { BasicSettingCreateValidation.checkIsAmp(any(), any()) } returns Unit
            every { BasicSettingCreateValidation.checkSpotMaxSize(any()) } returns Unit
        }

        @AfterAll
        fun afterAll() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = BasicSettingCreateValidation.of(form, UserType.ma_staff, site)

            assertEquals(
                BasicSettingCreateValidation(
                    "spot", SiteId(1), SpotStatus.active, UpstreamType.prebidjs, CurrencyId(1), DeliveryMethod.js,
                    DisplayType.overlay, SpotMaxSizeValidation(100, 200), "description", "pageUrl"
                ),
                actual
            )
            verifyK { BasicSettingCreateValidation.checkMaStaffOnly(form, UserType.ma_staff) }
            verifyK { BasicSettingCreateValidation.checkSpotStatus(form) }
            verifyK { BasicSettingCreateValidation.checkCurrencyId(form) }
            verifyK { BasicSettingCreateValidation.checkDeliveryMethod(form, site) }
            verifyK { BasicSettingCreateValidation.checkDisplayType(form) }
            verifyK { BasicSettingCreateValidation.checkIsDisplayControl(form) }
            verifyK { BasicSettingCreateValidation.checkIsAmp(form, site) }
            verifyK { BasicSettingCreateValidation.checkSpotMaxSize(form) }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkMaStaffOnlyのテスト")
    inner class CheckMaStaffOnlyTest {
        val currencyId = CurrencyId(1)

        @ParameterizedTest
        @MethodSource("invalidParams")
        @DisplayName("不正入力")
        fun isInvalid(form: BasicSettingCreateForm, userType: UserType) {
            assertThrows<CompassManagerException> { BasicSettingCreateValidation.checkMaStaffOnly(form, userType) }
        }

        private fun invalidParams() = listOf(
            Arguments.of(form(UpstreamType.prebidjs, null, null), UserType.agency),
            Arguments.of(form(UpstreamType.none, currencyId, null), UserType.client),
            Arguments.of(form(UpstreamType.none, null, "http://test.com"), UserType.other)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(form: BasicSettingCreateForm, userType: UserType) {
            assertDoesNotThrow { BasicSettingCreateValidation.checkMaStaffOnly(form, userType) }
        }

        private fun validParams() = listOf(
            // マイクロアド社員は設定可
            Arguments.of(form(UpstreamType.prebidjs, currencyId, "http://test.com"), UserType.ma_staff),
            // マイクロアド社員以外は未設定のみ可
            Arguments.of(form(UpstreamType.none, null, null), UserType.agency),
            Arguments.of(form(UpstreamType.none, null, null), UserType.client),
            Arguments.of(form(UpstreamType.none, null, null), UserType.other)

        )

        private fun form(
            upstreamType: UpstreamType, currencyId: CurrencyId?, pageUrl: String?
        ): BasicSettingCreateForm = mock {
            on { this.upstreamType } doReturn upstreamType
            on { this.currencyId } doReturn currencyId
            on { this.pageUrl } doReturn pageUrl
        }
    }

    @Nested
    @DisplayName("checkSpotStatusのテスト")
    inner class CheckSpotStatusTest {

        @Test
        @DisplayName("未入力")
        fun isNoValue() {
            assertDoesNotThrow { BasicSettingCreateValidation.checkSpotStatus(form(null)) }
        }

        @ParameterizedTest
        @EnumSource(value = SpotStatus::class, names = ["active", "standby"], mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("不正入力")
        fun isInvalid(spotStatus: SpotStatus) {
            assertThrows<CompassManagerException> { BasicSettingCreateValidation.checkSpotStatus(form(spotStatus)) }
        }

        @ParameterizedTest
        @EnumSource(value = SpotStatus::class, names = ["active", "standby"], mode = EnumSource.Mode.INCLUDE)
        @DisplayName("正常")
        fun isValid(spotStatus: SpotStatus) {
            assertDoesNotThrow { BasicSettingCreateValidation.checkSpotStatus(form(spotStatus)) }
        }

        private fun form(spotStatus: SpotStatus?): BasicSettingCreateForm = mock {
            on { this.spotStatus } doReturn spotStatus
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkCurrencyIdのテスト")
    inner class CheckCurrencyIdTest {
        val currencyId = CurrencyId(1)

        @Test
        @DisplayName("不正入力")
        fun isInvalid() {
            assertThrows<CompassManagerException> {
                BasicSettingCreateValidation.checkCurrencyId(form(UpstreamType.none, currencyId))
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(upstreamType: UpstreamType, currencyId: CurrencyId?) {
            assertDoesNotThrow { BasicSettingCreateValidation.checkCurrencyId(form(upstreamType, currencyId)) }
        }

        private fun validParams() = listOf(
            // ヘッダービディング通貨が未入力の場合は未チェック
            Arguments.of(UpstreamType.none, null),
            Arguments.of(UpstreamType.prebidjs, null),
            // ヘッダービディングがnoneでない場合は設定可
            Arguments.of(UpstreamType.prebidjs, currencyId)
        )

        private fun form(upstreamType: UpstreamType, currencyId: CurrencyId?): BasicSettingCreateForm = mock {
            on { this.upstreamType } doReturn upstreamType
            on { this.currencyId } doReturn currencyId
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkDeliveryMethodのテスト")
    inner class CheckDeliveryMethodTest {
        @ParameterizedTest
        @MethodSource("invalidParams")
        @DisplayName("不正入力")
        fun isInvalid(form: BasicSettingCreateForm, site: Site) {
            assertThrows<CompassManagerException> {
                BasicSettingCreateValidation.checkDeliveryMethod(form, site)
            }
        }

        private fun invalidParams() = listOf(
            // ヘッダービディングがnoneでない場合はJS以外は設定不可
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.sdk), site(SiteType.pc_web)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.sdk), site(SiteType.sp_web)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.sdk), site(SiteType.i_app)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.sdk), site(SiteType.a_app)),
            // サイト種別がiphone/android以外の場合はSDKは設定不可
            Arguments.of(form(UpstreamType.none, DeliveryMethod.sdk), site(SiteType.pc_web)),
            Arguments.of(form(UpstreamType.none, DeliveryMethod.sdk), site(SiteType.sp_web))
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(form: BasicSettingCreateForm, site: Site?) {
            assertDoesNotThrow { BasicSettingCreateValidation.checkDeliveryMethod(form, site) }
        }

        private fun validParams() = listOf(
            // 関連項目が未入力の場合は未チェック
            Arguments.of(form(UpstreamType.prebidjs, null), site(SiteType.pc_web)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js), null),
            // ヘッダービディングがnoneでない場合はJSのみ設定可
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js), site(SiteType.pc_web)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js), site(SiteType.sp_web)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js), site(SiteType.i_app)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js), site(SiteType.a_app)),
            // サイト種別がiphone/android以外 の場合はJSのみ設定可
            Arguments.of(form(UpstreamType.none, DeliveryMethod.js), site(SiteType.pc_web)),
            Arguments.of(form(UpstreamType.none, DeliveryMethod.js), site(SiteType.sp_web)),
            // サイト種別がiphone/android の場合はJS/SDKを設定可
            Arguments.of(form(UpstreamType.none, DeliveryMethod.js), site(SiteType.i_app)),
            Arguments.of(form(UpstreamType.none, DeliveryMethod.sdk), site(SiteType.i_app)),
            Arguments.of(form(UpstreamType.none, DeliveryMethod.js), site(SiteType.a_app)),
            Arguments.of(form(UpstreamType.none, DeliveryMethod.sdk), site(SiteType.a_app))
        )

        private fun form(upstreamType: UpstreamType, deliveryMethod: DeliveryMethod?): BasicSettingCreateForm = mock {
            on { this.upstreamType } doReturn upstreamType
            on { this.deliveryMethod } doReturn deliveryMethod
        }

        private fun site(siteType: SiteType): Site = mock {
            on { this.siteType } doReturn siteType
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkDisplayTypeのテスト")
    inner class CheckDisplayTypeTest {
        @Test
        @DisplayName("不正入力")
        fun isInvalid() {
            assertThrows<CompassManagerException> {
                // 広告配信方法がSDKの場合はオーバーレイは設定不可
                BasicSettingCreateValidation.checkDisplayType(form(DisplayType.overlay, DeliveryMethod.sdk))
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(displayType: DisplayType?, deliveryMethod: DeliveryMethod?) {
            assertDoesNotThrow { BasicSettingCreateValidation.checkDisplayType(form(displayType, deliveryMethod)) }
        }

        private fun validParams() = listOf(
            // 関連項目が未入力の場合は未チェック
            Arguments.of(null, null),
            Arguments.of(null, DeliveryMethod.js),
            Arguments.of(null, DeliveryMethod.sdk),
            Arguments.of(DisplayType.inline, null),
            Arguments.of(DisplayType.overlay, null),
            Arguments.of(DisplayType.interstitial, null),
            // 広告配信方法がJSのとき全ての表示種別を設定可
            Arguments.of(DisplayType.inline, DeliveryMethod.js),
            Arguments.of(DisplayType.overlay, DeliveryMethod.js),
            Arguments.of(DisplayType.interstitial, DeliveryMethod.js),
            // 広告配信方法がSDKのときオーバーレイ以外の表示種別を設定可
            Arguments.of(DisplayType.inline, DeliveryMethod.sdk),
            Arguments.of(DisplayType.interstitial, DeliveryMethod.sdk)
        )

        private fun form(displayType: DisplayType?, deliveryMethod: DeliveryMethod?): BasicSettingCreateForm = mock {
            on { this.displayType } doReturn displayType
            on { this.deliveryMethod } doReturn deliveryMethod
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkIsDisplayControlのテスト")
    inner class CheckIsDisplayControlTest {
        @ParameterizedTest
        @MethodSource("invalidParams")
        @DisplayName("不正入力")
        fun isInvalid(
            upstreamType: UpstreamType, deliveryMethod: DeliveryMethod, displayType: DisplayType,
            isDisplayControl: Boolean
        ) {
            assertThrows<CompassManagerException> {
                BasicSettingCreateValidation.checkIsDisplayControl(
                    form(upstreamType, deliveryMethod, displayType, isDisplayControl)
                )
            }
        }

        private fun invalidParams() = listOf(
            // ヘッダービディングがnonedでない場合は設定不可
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, true),
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.overlay, true),
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.interstitial, true),
            // 広告配信方法がJS かつ 表示種別がオーバーレイ以外 の場合は設定不可
            Arguments.of(UpstreamType.none, DeliveryMethod.js, DisplayType.inline, true),
            Arguments.of(UpstreamType.none, DeliveryMethod.js, DisplayType.interstitial, true),
            // 広告配信方法がSDK かつ 表示種別がインタースティシャル以外 の場合は設定不可
            Arguments.of(UpstreamType.none, DeliveryMethod.sdk, DisplayType.inline, true),
            Arguments.of(UpstreamType.none, DeliveryMethod.sdk, DisplayType.overlay, true),
            // 広告配信方法がSDK かつ 表示種別がインタースティシャル の場合は true 固定のため false は設定不可
            Arguments.of(UpstreamType.none, DeliveryMethod.sdk, DisplayType.interstitial, false)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(
            upstreamType: UpstreamType, deliveryMethod: DeliveryMethod?, displayType: DisplayType?,
            isDisplayControl: Boolean
        ) {
            assertDoesNotThrow {
                BasicSettingCreateValidation.checkIsDisplayControl(
                    form(upstreamType, deliveryMethod, displayType, isDisplayControl)
                )
            }
        }

        private fun validParams() = listOf(
            // 関連項目が未入力の場合は未チェック
            Arguments.of(UpstreamType.prebidjs, null, null, true),
            Arguments.of(UpstreamType.prebidjs, null, DisplayType.inline, true),
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, null, true),
            // ヘッダービディングがnoneでない場合は false 固定
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, false),
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.overlay, false),
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.interstitial, false),
            // 広告配信方法がJS かつ 表示種別がオーバーレイ以外 の場合は false固定
            Arguments.of(UpstreamType.none, DeliveryMethod.js, DisplayType.inline, false),
            Arguments.of(UpstreamType.none, DeliveryMethod.js, DisplayType.interstitial, false),
            // 広告配信方法がJS かつ 表示種別がオーバーレイ の場合は設定可
            Arguments.of(UpstreamType.none, DeliveryMethod.js, DisplayType.overlay, true),
            Arguments.of(UpstreamType.none, DeliveryMethod.js, DisplayType.overlay, false),
            // 広告配信方法がSDK かつ 表示種別がインタースティシャル以外 の場合はfalse固定
            Arguments.of(UpstreamType.none, DeliveryMethod.sdk, DisplayType.inline, false),
            Arguments.of(UpstreamType.none, DeliveryMethod.sdk, DisplayType.overlay, false),
            // 広告配信方法がSDK かつ 表示種別がインタースティシャル の場合はtrue固定
            Arguments.of(UpstreamType.none, DeliveryMethod.sdk, DisplayType.interstitial, true)
        )

        private fun form(
            upstreamType: UpstreamType, deliveryMethod: DeliveryMethod?, displayType: DisplayType?,
            isDisplayControl: Boolean
        ): BasicSettingCreateForm = mock {
            on { this.upstreamType } doReturn upstreamType
            on { this.deliveryMethod } doReturn deliveryMethod
            on { this.displayType } doReturn displayType
            on { this.isDisplayControl } doReturn isDisplayControl
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkIsAmpのテスト")
    inner class CheckIsAmpTest {
        @ParameterizedTest
        @MethodSource("invalidParams")
        @DisplayName("不正入力")
        fun isInvalid(form: BasicSettingCreateForm, site: Site) {
            assertThrows<CompassManagerException> { BasicSettingCreateValidation.checkIsAmp(form, site) }
        }

        private fun invalidParams() = listOf(
            // ヘッダービディングがnoneでない場合は設定不可
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, true, true), site(PlatformId.pc)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, false, true), site(PlatformId.pc)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, true, true), site(PlatformId.smartPhone)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, false, true), site(PlatformId.smartPhone)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.sdk, DisplayType.inline, true, true), site(PlatformId.smartPhone)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.sdk, DisplayType.inline, false, true), site(PlatformId.smartPhone)),
            // サイトがPCの場合は設定不可
            Arguments.of(form(UpstreamType.none, DeliveryMethod.js, DisplayType.inline, true, true), site(PlatformId.pc)),
            Arguments.of(form(UpstreamType.none, DeliveryMethod.js, DisplayType.inline, false, true), site(PlatformId.pc)),
            // 広告配信方法がSDKの場合は設定不可
            Arguments.of(form(UpstreamType.none, DeliveryMethod.sdk, DisplayType.inline, true, true), site(PlatformId.smartPhone)),
            Arguments.of(form(UpstreamType.none, DeliveryMethod.sdk, DisplayType.inline, false, true), site(PlatformId.smartPhone)),
            // 表示種別が インライン または (オーバーレイ かつ 表示制御がオフ) でない場合は設定不可
            Arguments.of(form(UpstreamType.none, DeliveryMethod.js, DisplayType.overlay, true, true), site(PlatformId.smartPhone)),
            Arguments.of(form(UpstreamType.none, DeliveryMethod.js, DisplayType.interstitial, true, true), site(PlatformId.smartPhone)),
            Arguments.of(form(UpstreamType.none, DeliveryMethod.js, DisplayType.interstitial, false, true), site(PlatformId.smartPhone)),
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(form: BasicSettingCreateForm, site: Site?) {
            assertDoesNotThrow { BasicSettingCreateValidation.checkIsAmp(form, site) }
        }

        private fun validParams() = listOf(
            // AMP対応が false の場合は未チェック
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, true, false), site(PlatformId.pc)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, false, false), site(PlatformId.pc)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, true, false), site(PlatformId.smartPhone)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, false, false), site(PlatformId.smartPhone)),
            // 関連項目が未入力の場合は未チェック
            Arguments.of(form(UpstreamType.prebidjs, null, null, true, true), site(PlatformId.pc)),
            Arguments.of(form(UpstreamType.prebidjs, DeliveryMethod.js, null, true, true), null),
            Arguments.of(form(UpstreamType.prebidjs, null, DisplayType.inline, true, true), null),
            // ヘッダービディングがnone かつ サイトがスマートフォン かつ 広告配信方法がJS かつ
            // (表示種別が インライン または (オーバーレイ かつ 表示制御がオフ)) の場合は設定可
            Arguments.of(form(UpstreamType.none, DeliveryMethod.js, DisplayType.inline, false, true), site(PlatformId.smartPhone)),
            Arguments.of(form(UpstreamType.none, DeliveryMethod.js, DisplayType.overlay, false, true), site(PlatformId.smartPhone)),
            Arguments.of(form(UpstreamType.none, DeliveryMethod.js, DisplayType.inline, false, false), site(PlatformId.smartPhone)),
            Arguments.of(form(UpstreamType.none, DeliveryMethod.js, DisplayType.overlay, false, false), site(PlatformId.smartPhone)),
        )

        private fun form(
            upstreamType: UpstreamType, deliveryMethod: DeliveryMethod?, displayType: DisplayType?, isDisplayControl: Boolean, isAmp: Boolean
        ): BasicSettingCreateForm = mock {
            on { this.upstreamType } doReturn upstreamType
            on { this.deliveryMethod } doReturn deliveryMethod
            on { this.displayType } doReturn displayType
            on { this.isDisplayControl } doReturn isDisplayControl
            on { this.isAmp } doReturn isAmp
        }

        private fun site(platformId: PlatformId): Site = mock {
            on { this.platformId } doReturn platformId
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("checkSpotMaxSizeのテスト")
    inner class CheckSpotMaxSizeTest {
        val spotMaxSize: SpotMaxSizeForm = mock()

        @ParameterizedTest
        @MethodSource("invalidParams")
        @DisplayName("不正入力")
        fun isInvalid(
            upstreamType: UpstreamType, deliveryMethod: DeliveryMethod, displayType: DisplayType,
            isDisplayControl: Boolean
        ) {
            assertThrows<CompassManagerException> {
                BasicSettingCreateValidation.checkSpotMaxSize(
                    form(upstreamType, deliveryMethod, displayType, isDisplayControl, spotMaxSize)
                )
            }
        }

        private fun invalidParams() = listOf(
            // ヘッダービディングがnoneでない場合は設定不可
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, true),
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, false),
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.overlay, true),
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.overlay, false),
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.interstitial, true),
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.interstitial, false),
            // 広告配信方法がJSでない場合は設定不可
            Arguments.of(UpstreamType.none, DeliveryMethod.sdk, DisplayType.inline, true),
            Arguments.of(UpstreamType.none, DeliveryMethod.sdk, DisplayType.inline, false),
            Arguments.of(UpstreamType.none, DeliveryMethod.sdk, DisplayType.interstitial, true),
            Arguments.of(UpstreamType.none, DeliveryMethod.sdk, DisplayType.interstitial, false),
            // 表示種別が インライン または (オーバーレイ かつ 表示制御がオフ) でない場合は設定不可
            Arguments.of(UpstreamType.none, DeliveryMethod.js, DisplayType.interstitial, true),
            Arguments.of(UpstreamType.none, DeliveryMethod.js, DisplayType.interstitial, false),
            Arguments.of(UpstreamType.none, DeliveryMethod.js, DisplayType.overlay, true)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(
            upstreamType: UpstreamType, deliveryMethod: DeliveryMethod?, displayType: DisplayType?,
            isDisplayControl: Boolean, spotMaxSize: SpotMaxSizeForm?
        ) {
            assertDoesNotThrow {
                BasicSettingCreateValidation.checkSpotMaxSize(
                    form(upstreamType, deliveryMethod, displayType, isDisplayControl, spotMaxSize)
                )
            }
        }

        private fun validParams() = listOf(
            // 関連項目が未入力の場合は未チェック
            Arguments.of(UpstreamType.prebidjs, null, DisplayType.inline, true, spotMaxSize),
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, null, true, spotMaxSize),
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, true, null),
            // ヘッダービディングがnone かつ 広告配信方法がJS
            // かつ 表示種別が インライン または (オーバーレイ かつ 表示制御がオフ) の場合は設定可
            Arguments.of(UpstreamType.none, DeliveryMethod.js, DisplayType.inline, false, spotMaxSize),
            Arguments.of(UpstreamType.none, DeliveryMethod.js, DisplayType.overlay, false, spotMaxSize)
        )

        private fun form(
            upstreamType: UpstreamType, deliveryMethod: DeliveryMethod?, displayType: DisplayType?,
            isDisplayControl: Boolean, spotMaxSize: SpotMaxSizeForm?
        ): BasicSettingCreateForm = mock {
            on { this.upstreamType } doReturn upstreamType
            on { this.deliveryMethod } doReturn deliveryMethod
            on { this.displayType } doReturn displayType
            on { this.isDisplayControl } doReturn isDisplayControl
            on { this.spotMaxSize } doReturn spotMaxSize
        }
    }

    private fun sut(
        spotName: String? = "aaa", siteId: SiteId? = SiteId(1), spotStatus: SpotStatus? = SpotStatus.active,
        upstreamType: UpstreamType = UpstreamType.none, currencyId: CurrencyId? = CurrencyId(1),
        deliveryMethod: DeliveryMethod? = DeliveryMethod.js, displayType: DisplayType? = DisplayType.interstitial,
        spotMaxSize: SpotMaxSizeValidation? = SpotMaxSizeValidation(10, 20),
        description: String? = "description", pageUrl: String? = "http://test.com"
    ) = BasicSettingCreateValidation(
        spotName, siteId, spotStatus, upstreamType, currencyId, deliveryMethod, displayType, spotMaxSize, description,
        pageUrl
    )
}
