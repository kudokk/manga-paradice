package jp.mangaka.ssp.application.service.spot.validation

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.spot.validation.banner.BannerSettingValidation
import jp.mangaka.ssp.application.service.spot.validation.banner.SpotSizeTypeDeleteRule
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DeliveryMethod
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.BannerSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBannerEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotMaxSizeForm
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import io.mockk.verify as verifyK

@DisplayName("SpotBannerEditValidationのテスト")
private class SpotBannerEditValidationTest {
    companion object {
        val spotId = SpotId(1)
    }

    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @DisplayName("バナー設定のテスト")
    inner class BannerTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            val banner = BannerSettingValidation(emptyList(), true, true, null, null, true, null)

            validator.validate(SpotBannerEditValidation(banner, emptyList(), true)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("banner.") })
            }
        }

        @Test
        @DisplayName("未設定のとき")
        fun isNull() {
            validator.validate(SpotBannerEditValidation(null, emptyList(), true)).run {
                assertTrue(none { it.propertyPath.toString().startsWith("banner.") })
            }
        }
    }

    @Nested
    @DisplayName("他フォーマットとの相関チェックのテスト")
    inner class FormatsTest {
        @Nested
        @DisplayName("バナー設定あり")
        inner class ActiveBannerTest {
            @ParameterizedTest
            @ValueSource(booleans = [true, false])
            @DisplayName("常に正常")
            fun isValid(hasOtherFormat: Boolean) {
                val banner = BannerSettingValidation(emptyList(), true, true, null, null, true, null)

                validator.validate(SpotBannerEditValidation(banner, emptyList(), true)).run {
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
                validator.validate(SpotBannerEditValidation(null, emptyList(), true)).run {
                    assertTrue(none { it.propertyPath.toString() == "formats" })
                }
            }

            @Test
            @DisplayName("他フォーマット設定なし")
            fun isInactiveOtherFormat() {
                validator.validate(SpotBannerEditValidation(null, emptyList(), false)).run {
                    assertTrue(any { it.propertyPath.toString() == "formats" })
                }
            }
        }
    }

    @Nested
    @DisplayName("広告枠に紐づくストラクトのサイズ種別エラーのテスト")
    inner class StructSizeTypesTest {
        @Test
        @DisplayName("ストラクト紐づき違反")
        fun isInvalid() {
            val structs = listOf(
                StructSizeTypeError(100, 200, listOf(1, 2, 3).map { StructId(it) }),
                StructSizeTypeError(200, 300, listOf(4, 5).map { StructId(it) }),
            )

            validator.validate(SpotBannerEditValidation(null, structs, true)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("structSizeTypes") })
            }
        }

        @Test
        @DisplayName("正常")
        fun isValid() {
            validator.validate(SpotBannerEditValidation(null, emptyList(), true)).run {
                assertTrue(none { it.propertyPath.toString().startsWith("structSizeTypes") })
            }
        }
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val spot: Spot = mock {
            on { this.spotId } doReturn spotId
            on { this.upstreamType } doReturn UpstreamType.none
            on { this.isAmp } doReturn false
            on { this.width } doReturn 200
            on { this.height } doReturn 400
        }
        val site: Site = mock()
        val deleteSizeTypes: List<SizeTypeInfo> = mock()
        val spotSizeTypeDeleteRule: SpotSizeTypeDeleteRule = mock()
        val structSizeTypes: List<StructSizeTypeError> = mock()
        val bannerValidation: BannerSettingValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(BannerSettingValidation, StructSizeTypeError)
            every {
                BannerSettingValidation.of(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
            } returns bannerValidation
            every { StructSizeTypeError.of(any(), any(), any()) } returns structSizeTypes
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "js,inline,false,true",
                "js,overlay,true,true",
                "js,interstitial,false,true",
                "sdk,inline,false,true",
                "sdk,overlay,false,false",
                "sdk,interstitial,false,false",
            ]
        )
        @DisplayName("バナー設定あり")
        fun isActiveBanner(
            deliveryMethod: DeliveryMethod,
            displayType: DisplayType,
            isJsByOverlay: Boolean,
            isJsOrInline: Boolean
        ) {
            val bannerForm: BannerSettingForm = mock()
            doReturn(deliveryMethod).whenever(spot).deliveryMethod
            doReturn(displayType).whenever(spot).displayType

            val actual = SpotBannerEditValidation.of(
                SpotBannerEditForm(bannerForm, mock()),
                UserType.ma_staff,
                spot,
                site,
                false,
                true,
                deleteSizeTypes,
                spotSizeTypeDeleteRule,
                true
            )

            assertEquals(SpotBannerEditValidation(bannerValidation, structSizeTypes, true), actual)

            verifyK {
                BannerSettingValidation.of(
                    bannerForm,
                    UserType.ma_staff,
                    site,
                    UpstreamType.none,
                    false,
                    false,
                    isJsByOverlay,
                    isJsOrInline,
                    SpotMaxSizeForm(200, 400),
                    true
                )
            }
            verifyK { StructSizeTypeError.of(spotId, deleteSizeTypes, spotSizeTypeDeleteRule) }
        }

        @Test
        @DisplayName("バナー設定なし")
        fun isInactiveBanner() {
            val form = SpotBannerEditForm(null, mock())

            val actual = SpotBannerEditValidation.of(
                form, UserType.ma_staff, spot, site, true, true,
                deleteSizeTypes, spotSizeTypeDeleteRule, false
            )

            assertEquals(SpotBannerEditValidation(null, structSizeTypes, true), actual)
        }
    }
}
