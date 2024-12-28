package jp.mangaka.ssp.application.service.spot.validation.basic

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DeliveryMethod
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotMaxSizeForm
import jp.mangaka.ssp.util.TestUtils
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.apache.commons.lang3.RandomStringUtils
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

@DisplayName("BasicSettingEditValidationのテスト")
private class BasicSettingEditValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("広告枠名のテスト")
    inner class SpotNameTest {
        @ParameterizedTest
        @MethodSource("jp.mangaka.ssp.util.TestUtils#emptyStrings")
        @DisplayName("未入力")
        fun isEmpty(value: String?) {
            validator.validate(BasicSettingEditValidation(value, null, null, null, emptyList())).run {
                assertTrue(any { it.propertyPath.toString() == "spotName" })
            }
        }

        @Test
        @DisplayName("文字数超過")
        fun isTooLong() {
            val sut = BasicSettingEditValidation(RandomStringUtils.random(86), null, null, null, emptyList())

            validator.validate(sut).run {
                assertTrue(any { it.propertyPath.toString() == "spotName" })
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 85])
        @DisplayName("正常")
        fun isValid(length: Int) {
            val sut = BasicSettingEditValidation(RandomStringUtils.random(length), null, null, null, emptyList())

            validator.validate(sut).run {
                assertTrue(none { it.propertyPath.toString() == "spotName" })
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
            val sut = BasicSettingEditValidation(null, SpotMaxSizeValidation(-1, -1), null, null, emptyList())

            validator.validate(sut).run {
                assertTrue(any { it.propertyPath.toString().startsWith("spotMaxSize") })
            }
        }

        @Test
        @DisplayName("未入力")
        fun isEmpty() {
            validator.validate(BasicSettingEditValidation(null, null, null, null, emptyList())).run {
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
            val sut = BasicSettingEditValidation(null, null, RandomStringUtils.random(86), null, emptyList())

            validator.validate(sut).run {
                assertTrue(any { it.propertyPath.toString() == "description" })
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(value: String?) {
            validator.validate(BasicSettingEditValidation(null, null, value, null, emptyList())).run {
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
            validator.validate(BasicSettingEditValidation(null, null, null, "aaa", emptyList())).run {
                assertTrue(any { it.propertyPath.toString() == "pageUrl" })
            }
        }

        @Test
        @DisplayName("文字数超過")
        fun isTooLong() {
            val url = "http://" + RandomStringUtils.random(1018)

            validator.validate(BasicSettingEditValidation(null, null, null, url, emptyList())).run {
                assertTrue(any { it.propertyPath.toString() == "pageUrl" })
            }
        }

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isValid(value: String?) {
            validator.validate(BasicSettingEditValidation(null, null, null, value, emptyList())).run {
                assertTrue(none { it.propertyPath.toString() == "pageUrl" })
            }
        }

        private fun validParams() = listOf(null, "http://" + RandomStringUtils.randomAlphabetic(1017))
    }

    @Nested
    @DisplayName("サイズ種別リストのテスト")
    inner class SizeTypesTest {
        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("サイズ種別リストが空でない")
        inner class NotEmptySizeTypesTest {
            val sizeTypes = listOf(
                mockSizeTypeInfo(100, 200), mockSizeTypeInfo(50, 300), mockSizeTypeInfo(150, 100)
            )

            @ParameterizedTest
            @MethodSource("noSpotMaxSizeParams")
            @DisplayName("固定表示が未設定")
            fun isNoSpotMaxSize(spotMaxSize: SpotMaxSizeValidation?) {
                validator.validate(BasicSettingEditValidation(null, spotMaxSize, null, null, sizeTypes)).run {
                    assertTrue(none { it.propertyPath.toString() == "sizeTypes" })
                }
            }

            private fun noSpotMaxSizeParams() = listOf(
                null, SpotMaxSizeValidation(null, null)
            )

            @Test
            @DisplayName("固定表示より大きい横幅が存在する")
            fun isOverWidth() {
                val spotMaxSize = SpotMaxSizeValidation(149, 500)

                validator.validate(BasicSettingEditValidation(null, spotMaxSize, null, null, sizeTypes)).run {
                    println(map { it.message })
                    assertTrue(any { it.propertyPath.toString() == "sizeTypes" })
                }
            }

            @Test
            @DisplayName("固定表示より大きい縦幅が存在する")
            fun isOverHeight() {
                val spotMaxSize = SpotMaxSizeValidation(500, 299)

                validator.validate(BasicSettingEditValidation(null, spotMaxSize, null, null, sizeTypes)).run {
                    println(map { it.message })
                    assertTrue(any { it.propertyPath.toString() == "sizeTypes" })
                }
            }

            @ParameterizedTest
            @MethodSource("validParams")
            @DisplayName("正常")
            fun isValid(spotMaxSize: SpotMaxSizeValidation) {
                validator.validate(BasicSettingEditValidation(null, spotMaxSize, null, null, sizeTypes)).run {
                    assertTrue(none { it.propertyPath.toString() == "sizeTypes" })
                }
            }

            private fun validParams() = listOf(
                SpotMaxSizeValidation(150, 500),
                SpotMaxSizeValidation(151, 500),
                SpotMaxSizeValidation(500, 300),
                SpotMaxSizeValidation(500, 301)
            )

            private fun mockSizeTypeInfo(width: Int, height: Int): SizeTypeInfo = mock {
                on { this.width } doReturn width
                on { this.height } doReturn height
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("サイズ種別リストが空")
        inner class EmptySizeTypesTest {
            val sizeTypes: List<SizeTypeInfo> = emptyList()

            @ParameterizedTest
            @MethodSource("correctParams")
            @DisplayName("正常")
            fun isCorrect(spotMaxSize: SpotMaxSizeValidation?) {
                validator.validate(BasicSettingEditValidation(null, spotMaxSize, null, null, sizeTypes)).run {
                    assertTrue(none { it.propertyPath.toString() == "sizeTypes" })
                }
            }

            private fun correctParams() = listOf(
                null, SpotMaxSizeValidation(100, 200)
            )
        }
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val form = BasicSettingEditForm("spot1", SpotMaxSizeForm(100, 200), "desc1", "pageUrl1")
        val spot: Spot = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(BasicSettingEditValidation)

            every { BasicSettingEditValidation.checkSpotMaxSize(any(), any(), any()) } returns Unit
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val sizeTypes: List<SizeTypeInfo> = mock()
            val actual = BasicSettingEditValidation.of(form, spot, UserType.ma_staff, true, sizeTypes)

            assertEquals(
                BasicSettingEditValidation("spot1", SpotMaxSizeValidation(100, 200), "desc1", "pageUrl1", sizeTypes),
                actual
            )
            verifyK { BasicSettingEditValidation.checkSpotMaxSize(form, spot, true) }
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
                BasicSettingEditValidation.checkSpotMaxSize(
                    form(spotMaxSize),
                    spot(upstreamType, deliveryMethod, displayType),
                    isDisplayControl
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
            upstreamType: UpstreamType, deliveryMethod: DeliveryMethod, displayType: DisplayType,
            isDisplayControl: Boolean, spotMaxSize: SpotMaxSizeForm?
        ) {
            assertDoesNotThrow {
                BasicSettingEditValidation.checkSpotMaxSize(
                    form(spotMaxSize),
                    spot(upstreamType, deliveryMethod, displayType),
                    isDisplayControl
                )
            }
        }

        private fun validParams() = listOf(
            // 関連項目が未入力の場合は未チェック
            Arguments.of(UpstreamType.prebidjs, DeliveryMethod.js, DisplayType.inline, true, null),
            // ヘッダービディングがnone かつ 広告配信方法がJS
            // かつ 表示種別が インライン または (オーバーレイ かつ 表示制御がオフ) の場合は設定可
            Arguments.of(UpstreamType.none, DeliveryMethod.js, DisplayType.inline, false, spotMaxSize),
            Arguments.of(UpstreamType.none, DeliveryMethod.js, DisplayType.overlay, false, spotMaxSize)
        )

        private fun form(spotMaxSize: SpotMaxSizeForm?): BasicSettingEditForm = mock {
            on { this.spotMaxSize } doReturn spotMaxSize
        }

        private fun spot(
            upstreamType: UpstreamType, deliveryMethod: DeliveryMethod, displayType: DisplayType
        ): Spot = mock {
            on { this.upstreamType } doReturn upstreamType
            on { this.deliveryMethod } doReturn deliveryMethod
            on { this.displayType } doReturn displayType
        }
    }
}
