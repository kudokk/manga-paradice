package jp.mangaka.ssp.application.service.spot

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.clearInvocations
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import jp.mangaka.ssp.application.service.spot.SpotServiceImpl.ExistingSpot
import jp.mangaka.ssp.application.service.spot.helper.SizeTypeInfoPersistHelper
import jp.mangaka.ssp.application.service.spot.helper.SpotGetWithCheckHelper
import jp.mangaka.ssp.application.service.spot.helper.SpotPersistHelper
import jp.mangaka.ssp.application.service.spot.util.SpotUtils
import jp.mangaka.ssp.application.service.spot.validation.SpotBannerEditValidation
import jp.mangaka.ssp.application.service.spot.validation.SpotBasicEditValidation
import jp.mangaka.ssp.application.service.spot.validation.SpotCreateValidation
import jp.mangaka.ssp.application.service.spot.validation.SpotDspEditValidation
import jp.mangaka.ssp.application.service.spot.validation.SpotNativeEditValidation
import jp.mangaka.ssp.application.service.spot.validation.SpotVideoEditValidation
import jp.mangaka.ssp.application.service.spot.validation.banner.SpotSizeTypeDeleteRule
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio16to5
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio16to9
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio32to5
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio.AspectRatioStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate.NativeTemplateStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetype
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetypeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBanner
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBannerDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNative
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNativeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideo
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpm
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpmDao
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.BannerSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm
import jp.mangaka.ssp.presentation.controller.spot.form.DspForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeStandardForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeVideoForm
import jp.mangaka.ssp.presentation.controller.spot.form.SizeTypeForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBannerEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBasicEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotDspEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotMaxSizeForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotNativeEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotVideoEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm
import jp.mangaka.ssp.presentation.controller.spot.view.SpotCreateResultView
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import jp.mangaka.ssp.util.exception.FormatValidationException
import jp.mangaka.ssp.util.exception.ResourceConflictException
import org.hibernate.validator.internal.engine.path.PathImpl
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import io.mockk.verify as verifyK

@DisplayName("SpotServiceImplのテスト")
private class SpotServiceImplTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val siteId = SiteId(1)
        val currencyId = CurrencyId(1)
        val decorationId = DecorationId(1)
        val nativeTemplateId1 = NativeTemplateId(1)
        val nativeTemplateId2 = NativeTemplateId(2)
        val platformId = PlatformId(1)
        val spotId = SpotId(1)
    }

    val notEmptyErrors = setOf(
        mock<ConstraintViolation<Any>> {
            on { this.propertyPath } doReturn PathImpl.createPathFromString("field")
            on { this.message } doReturn "error"
        }
    )
    val emptyErrors = emptySet<ConstraintViolation<Any>>()

    val validator: Validator = mock()
    val relaySpotSizetypeDao: RelaySpotSizetypeDao = mock()
    val spotBannerDao: SpotBannerDao = mock()
    val spotBannerDisplayDao: SpotBannerDisplayDao = mock()
    val spotNativeDao: SpotNativeDao = mock()
    val spotNativeDisplayDao: SpotNativeDisplayDao = mock()
    val spotNativeVideoDisplayDao: SpotNativeVideoDisplayDao = mock()
    val spotVideoDao: SpotVideoDao = mock()
    val spotVideoDisplayDao: SpotVideoDisplayDao = mock()
    val spotVideoFloorCpmDao: SpotVideoFloorCpmDao = mock()
    val spotGetWithCheckHelper: SpotGetWithCheckHelper = mock()
    val sizeTypeInfoPersistHelper: SizeTypeInfoPersistHelper = mock()
    val spotPersistHelper: SpotPersistHelper = mock()
    val spotSizeTypeDeleteRuleHelper: SpotSizeTypeDeleteRuleHelper = mock()

    val sut = spy(
        SpotServiceImpl(
            validator, relaySpotSizetypeDao, spotBannerDao, spotBannerDisplayDao, spotNativeDao, spotNativeDisplayDao,
            spotNativeVideoDisplayDao, spotVideoDao, spotVideoDisplayDao, spotVideoFloorCpmDao,
            sizeTypeInfoPersistHelper, spotGetWithCheckHelper, spotPersistHelper, spotSizeTypeDeleteRuleHelper
        )
    )

    @AfterEach
    fun after() {
        clearInvocations(
            validator, relaySpotSizetypeDao, spotBannerDao, spotBannerDisplayDao, spotNativeDao, spotNativeDisplayDao,
            spotNativeVideoDisplayDao, spotVideoDao, spotVideoDisplayDao, spotVideoFloorCpmDao,
            sizeTypeInfoPersistHelper, spotGetWithCheckHelper, spotPersistHelper, spotSizeTypeDeleteRuleHelper, sut
        )
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    @DisplayName("createのテスト")
    inner class CreateTest {
        val spotCreateValidation: SpotCreateValidation = mock()
        val spotId = SpotId(1)
        val aspectRatios: List<AspectRatio> = mock()

        @BeforeAll
        fun beforeAll() {
            mockkObject(SpotCreateValidation)
            every { SpotCreateValidation.of(any(), any(), any()) } returns spotCreateValidation

            doReturn(aspectRatios).whenever(sut).getAspectRatios(any())
            doNothing().whenever(sut).checkBasic(any())
            doNothing().whenever(sut).checkDsp(any())
            doNothing().whenever(sut).checkBanner(any(), any())
            doNothing().whenever(sut).checkNative(any(), any(), anyOrNull())
        }

        @AfterAll
        fun afterAll() {
            unmockkAll()
        }

        @Test
        @DisplayName("バリデーションエラー")
        fun isInvalid() {
            val form = SpotCreateForm(mock(), mock(), mock(), mock(), mock())
            doReturn(null).whenever(sut).getSite(any(), any())
            doReturn(notEmptyErrors).whenever(validator).validate(any<SpotCreateValidation>())

            // DBからの取得なし(バナー設定なし)のパターンも合わせてテスト
            assertThrows<FormatValidationException> { sut.create(coAccountId, UserType.ma_staff, form) }

            verify(sut, times(1)).getAspectRatios(form.video)
            verify(sut, times(1)).getSite(coAccountId, form.basic)
            verify(sut, times(1)).checkBasic(form.basic)
            verify(sut, times(1)).checkDsp(form.dsps)
            verify(sut, times(1)).checkBanner(coAccountId, form.banner)
            verify(sut, times(1)).checkNative(coAccountId, form.native, null)
            verify(validator, times(1)).validate(spotCreateValidation)
            verify(sizeTypeInfoPersistHelper, never()).bulkCreate(any(), any(), any<SpotCreateForm>())
            verify(spotPersistHelper, never()).create(any(), any(), any(), any(), any(), any())
            verifyK { SpotCreateValidation.of(form, UserType.ma_staff, null) }
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val form = SpotCreateForm(mock(), mock(), mock(), mock(), mock())
            val site: Site = mock {
                on { this.platformId } doReturn platformId
            }
            val sizeTypeIds: List<SizeTypeId> = mock()
            doReturn(site).whenever(sut).getSite(any(), any())
            doReturn(emptyErrors).whenever(validator).validate(any<SpotCreateValidation>())
            doReturn(sizeTypeIds).whenever(sizeTypeInfoPersistHelper).bulkCreate(any(), any(), any<SpotCreateForm>())
            doReturn(spotId).whenever(spotPersistHelper).create(any(), any(), any(), any(), any(), any())

            val actual = sut.create(coAccountId, UserType.agency, form)

            assertEquals(SpotCreateResultView(spotId), actual)

            verify(sut, times(1)).getAspectRatios(form.video)
            verify(sut, times(1)).getSite(coAccountId, form.basic)
            verify(sut, times(1)).checkBasic(form.basic)
            verify(sut, times(1)).checkDsp(form.dsps)
            verify(sut, times(1)).checkBanner(coAccountId, form.banner)
            verify(sut, times(1)).checkNative(coAccountId, form.native, site)
            verify(validator, times(1)).validate(spotCreateValidation)
            verify(sizeTypeInfoPersistHelper, times(1)).bulkCreate(coAccountId, platformId, form)
            verify(spotPersistHelper, times(1)).create(
                coAccountId, form, site, sizeTypeIds, UserType.agency, aspectRatios
            )
            verifyK { SpotCreateValidation.of(form, UserType.agency, site) }
        }
    }

    @Nested
    @DisplayName("getSiteのテスト")
    inner class GetSiteTest {
        val form: BasicSettingCreateForm = mock()
        val site: Site = mock()

        @Test
        @DisplayName("サイトID設定なし")
        fun isNoSiteId() {
            doReturn(null).whenever(form).siteId

            sut.getSite(coAccountId, form)

            verify(spotGetWithCheckHelper, never()).getSiteWithCheck(any(), any(), any())
        }

        @Test
        @DisplayName("サイトID設定あり")
        fun isFillSiteId() {
            doReturn(siteId).whenever(form).siteId
            doReturn(site).whenever(spotGetWithCheckHelper).getSiteWithCheck(any(), any(), any())

            val actual = sut.getSite(coAccountId, form)

            assertEquals(site, actual)

            verify(spotGetWithCheckHelper, times(1)).getSiteWithCheck(
                coAccountId, siteId, listOf(SiteStatus.active, SiteStatus.requested, SiteStatus.ng)
            )
        }
    }

    @Nested
    @DisplayName("getAspectRatiosのテスト")
    inner class GetAspectRatiosTest {
        val form: VideoSettingForm = mock()
        val statuses = listOf(AspectRatioStatus.active)

        @Test
        @DisplayName("ビデオが未設定")
        fun isNoVideo() {
            val actual = sut.getAspectRatios(null)

            assertEmpty(actual)
            verify(spotGetWithCheckHelper, never()).getAspectRatiosWithCheck(any(), any())
        }

        @Test
        @DisplayName("詳細設定が空")
        fun isEmptyDetails() {
            doReturn(emptyList<VideoDetailForm>()).whenever(form).details

            val actual = sut.getAspectRatios(form)

            assertEmpty(actual)
            verify(spotGetWithCheckHelper, times(1)).getAspectRatiosWithCheck(emptyList(), statuses)
        }

        @Test
        @DisplayName("詳細設定が空でない")
        fun isNotEmptyDetails() {
            val aspectRatios: List<AspectRatio> = mock()
            doReturn(listOf(1, 2, 3, null, 4).map { mockDetail(it) }).whenever(form).details
            doReturn(aspectRatios).whenever(spotGetWithCheckHelper).getAspectRatiosWithCheck(any(), any())

            val actual = sut.getAspectRatios(form)

            assertEquals(aspectRatios, actual)
            verify(spotGetWithCheckHelper, times(1)).getAspectRatiosWithCheck(
                listOf(1, 2, 3, 4).map { AspectRatioId(it) }, statuses
            )
        }

        private fun mockDetail(aspectRatioId: Int?): VideoDetailForm = mock {
            on { this.aspectRatioId } doReturn aspectRatioId?.let { AspectRatioId(it) }
        }
    }

    @Nested
    @DisplayName("checkBasicのテスト")
    inner class CheckBasicTest {
        val form: BasicSettingCreateForm = mock()

        @Test
        @DisplayName("通貨ID設定なし")
        fun isNoCurrencyId() {
            doReturn(null).whenever(form).currencyId

            sut.checkBasic(form)

            verify(spotGetWithCheckHelper, never()).getCurrencyWithCheck(any())
        }

        @Test
        @DisplayName("通貨ID設定あり")
        fun isFillCurrencyId() {
            doReturn(currencyId).whenever(form).currencyId

            sut.checkBasic(form)

            verify(spotGetWithCheckHelper, times(1)).getCurrencyWithCheck(currencyId)
        }
    }

    @Nested
    @DisplayName("checkDspのテスト")
    inner class CheckDspTest {
        @Test
        @DisplayName("DSP設定なし")
        fun isEmptyDsps() {
            sut.checkDsp(emptyList())

            verify(spotGetWithCheckHelper, never()).getDspsWithCheck(any())
        }

        @Test
        @DisplayName("DSP設定あり")
        fun isNotEmptyDsps() {
            val forms = listOf(DspId(1), DspId(2), null, DspId(3), null, DspId(4)).map { DspForm(it, null, null) }

            sut.checkDsp(forms)

            verify(spotGetWithCheckHelper, times(1)).getDspsWithCheck(
                listOf(1, 2, 3, 4).map { DspId(it) }
            )
        }
    }

    @Nested
    @DisplayName("checkBannerのテスト")
    inner class CheckBannerTest {
        val form: BannerSettingForm = mock()

        @Test
        @DisplayName("バナー未設定")
        fun isNoBanner() {
            sut.checkBanner(coAccountId, null)

            verify(spotGetWithCheckHelper, never()).getDecorationWithCheck(any(), any())
        }

        @Test
        @DisplayName("デコレーションID設定なし")
        fun isNoDecorationId() {
            doReturn(null).whenever(form).decorationId

            sut.checkBanner(coAccountId, form)

            verify(spotGetWithCheckHelper, never()).getDecorationWithCheck(any(), any())
        }

        @Test
        @DisplayName("デコレーションID設定あり")
        fun isFillDecorationId() {
            doReturn(decorationId).whenever(form).decorationId

            sut.checkBanner(coAccountId, form)

            verify(spotGetWithCheckHelper, times(1)).getDecorationWithCheck(coAccountId, decorationId)
        }
    }

    @Nested
    @DisplayName("checkNativeのテスト")
    inner class CheckNativeTest {
        val site: Site = mock {
            on { this.platformId } doReturn platformId
        }
        val statuses = listOf(NativeTemplateStatus.active)

        @Test
        @DisplayName("ネイティブ未設定")
        fun isNoNative() {
            sut.checkNative(coAccountId, null, mock())

            verify(spotGetWithCheckHelper, never()).getNativeStandardTemplateWithCheck(any(), any(), any(), any())
            verify(spotGetWithCheckHelper, never()).getNativeVideoTemplateWithCheck(any(), any())
        }

        @Test
        @DisplayName("サイトが存在しない")
        fun isNoSite() {
            sut.checkNative(coAccountId, mock(), null)

            verify(spotGetWithCheckHelper, never()).getNativeStandardTemplateWithCheck(any(), any(), any(), any())
            verify(spotGetWithCheckHelper, never()).getNativeVideoTemplateWithCheck(any(), any())
        }

        @Test
        @DisplayName("通常・動画デザイン設定なし")
        fun isNoDesign() {
            val native = NativeSettingForm(null, null)

            sut.checkNative(coAccountId, native, mock())

            verify(spotGetWithCheckHelper, never()).getNativeStandardTemplateWithCheck(any(), any(), any(), any())
            verify(spotGetWithCheckHelper, never()).getNativeVideoTemplateWithCheck(any(), any())
        }

        @Test
        @DisplayName("ネイティブテンプレートID設定なし")
        fun isNoNativeTemplateId() {
            val native = NativeSettingForm(
                NativeStandardForm(null, null),
                NativeVideoForm(null, null, false)
            )

            sut.checkNative(coAccountId, native, mock())

            verify(spotGetWithCheckHelper, never()).getNativeStandardTemplateWithCheck(any(), any(), any(), any())
            verify(spotGetWithCheckHelper, never()).getNativeVideoTemplateWithCheck(any(), any())
        }

        @Test
        @DisplayName("ネイティブテンプレートID設定あり")
        fun isFillNativeTemplateId() {
            val native = NativeSettingForm(
                NativeStandardForm(nativeTemplateId1, null),
                NativeVideoForm(nativeTemplateId2, null, false)
            )

            sut.checkNative(coAccountId, native, site)

            verify(spotGetWithCheckHelper, times(1)).getNativeStandardTemplateWithCheck(
                coAccountId, nativeTemplateId1, statuses, platformId
            )
            verify(spotGetWithCheckHelper, times(1)).getNativeVideoTemplateWithCheck(nativeTemplateId2, statuses)
        }
    }

    @Nested
    @DisplayName("editBasicのテスト")
    inner class EditBasicTest {
        val form: SpotBasicEditForm = mock()
        val correctedBasicForm: BasicSettingEditForm = mock()
        val correctedForm = SpotBasicEditForm(correctedBasicForm, LocalDateTime.MIN)
        val spot: Spot = mock {
            on { this.spotId } doReturn spotId
        }
        val existingSpot: ExistingSpot = mock {
            on { this.spot } doReturn spot
            on { this.isDisplayControl() } doReturn true
        }
        val sizeTypeIds = listOf(1, 2, 3).map { SizeTypeId(it) }
        val relaySpotSizetypes: List<RelaySpotSizetype> = sizeTypeIds.map { sizeTypeId ->
            mock {
                on { this.sizeTypeId } doReturn sizeTypeId
            }
        }
        val sizeTypeInfos: List<SizeTypeInfo> = mock()
        val validation: SpotBasicEditValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotBasicEditValidation, SpotUtils)
            every { SpotBasicEditValidation.of(any(), any(), any(), any(), any()) } returns validation
            every { SpotUtils.isDisplayControl(any(), any(), any(), any()) } returns true

            doReturn(existingSpot).whenever(sut).getExistingSpot(any(), any(), any())
            doReturn(relaySpotSizetypes).whenever(relaySpotSizetypeDao).selectBySpotId(any())
            doReturn(sizeTypeInfos).whenever(spotGetWithCheckHelper).getSizeTypeInfosWithCheck(any())
            doReturn(emptyErrors).whenever(validator).validate(any<SpotBasicEditValidation>())
            doReturn(correctedForm).whenever(sut).correctSpotBasicEditForm(any(), any(), any())
            doNothing().whenever(sut).checkConflict(any(), any())
            doNothing().whenever(spotPersistHelper).editBasic(any(), any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            sut.editBasic(coAccountId, spotId, UserType.ma_staff, form)

            verify(sut, times(1)).getExistingSpot(coAccountId, spotId, UserType.ma_staff)
            verify(relaySpotSizetypeDao, times(1)).selectBySpotId(spotId)
            verify(spotGetWithCheckHelper, times(1)).getSizeTypeInfosWithCheck(sizeTypeIds)
            verify(sut, times(1)).correctSpotBasicEditForm(form, existingSpot, UserType.ma_staff)
            verify(sut, times(1)).checkConflict(spot, LocalDateTime.MIN)
            verifyK { SpotBasicEditValidation.of(correctedForm, spot, UserType.ma_staff, true, sizeTypeInfos) }
            verify(spotPersistHelper, times(1)).editBasic(spotId, correctedBasicForm)
        }
    }

    @Nested
    @DisplayName("correctSpotBasicEditFormのテスト")
    inner class CorrectSpotBasicEditFormTest {
        @Test
        @DisplayName("社員のとき")
        fun isMaStaff() {
            val form: SpotBasicEditForm = mock()

            val actual = sut.correctSpotBasicEditForm(form, mock(), UserType.ma_staff)

            assertEquals(form, actual)
        }

        @Nested
        @TestInstance(Lifecycle.PER_CLASS)
        @DisplayName("社員以外のとき")
        inner class NotMaStaffTest {
            val maxSize = SpotMaxSizeForm(100, 200)
            val inputForm = form("inputPageUrl")
            val existingSpot: ExistingSpot = mock()

            @ParameterizedTest
            @MethodSource("correctParams")
            @DisplayName("正常")
            fun isCorrect(spot: Spot, expected: SpotBasicEditForm, userType: UserType) {
                doReturn(spot).whenever(existingSpot).spot

                val actual = sut.correctSpotBasicEditForm(inputForm, existingSpot, userType)

                assertEquals(expected, actual)
            }

            private fun correctParams() = listOf(
                Arguments.of(spot(null), form(null), UserType.agency),
                Arguments.of(spot("dbPageUrl"), form("dbPageUrl"), UserType.client),
                Arguments.of(spot("dbPageUrl"), form("dbPageUrl"), UserType.other)
            )

            private fun form(pageUrl: String?): SpotBasicEditForm = SpotBasicEditForm(
                BasicSettingEditForm("name", maxSize, "desc", pageUrl),
                LocalDateTime.MIN
            )

            private fun spot(pageUrl: String?): Spot = mock {
                on { this.pageUrl } doReturn pageUrl
            }
        }
    }

    @Nested
    @DisplayName("editDspのテスト")
    inner class EditDspTest {
        val dspForms: List<DspForm> = mock()
        val form = SpotDspEditForm(dspForms, LocalDateTime.MIN)
        val spot: Spot = mock {
            on { this.siteId } doReturn siteId
        }
        val validation: SpotDspEditValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotDspEditValidation)
            every { SpotDspEditValidation.of(any(), any()) } returns validation

            doReturn(spot).whenever(spotGetWithCheckHelper).getSpotWithCheck(any(), any(), any())
            doReturn(mock<Site>()).whenever(spotGetWithCheckHelper).getSiteWithCheck(any(), any(), any())
            doNothing().whenever(sut).checkConflict(any(), any())
            doNothing().whenever(sut).checkDsp(any())
            doReturn(emptyErrors).whenever(validator).validate(any<SpotDspEditValidation>())
            doNothing().whenever(spotPersistHelper).editDsps(any(), any(), any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            sut.editDsp(coAccountId, spotId, UserType.ma_staff, form)

            verify(spotGetWithCheckHelper, times(1)).getSpotWithCheck(
                spotId, SpotStatus.editableStatuses, UserType.ma_staff
            )
            verify(spotGetWithCheckHelper, times(1)).getSiteWithCheck(coAccountId, siteId, SiteStatus.entries)
            verify(sut, times(1)).checkConflict(spot, LocalDateTime.MIN)
            verify(sut, times(1)).checkDsp(dspForms)
            verifyK { SpotDspEditValidation.of(form, UserType.ma_staff) }
            verify(spotPersistHelper, times(1)).editDsps(coAccountId, spotId, dspForms)
        }
    }

    @Nested
    @DisplayName("editBannerのテスト")
    inner class EditBannerTest {
        val spot: Spot = mock()
        val site: Site = mock {
            on { this.platformId } doReturn platformId
        }
        val existingSpot: ExistingSpot = mock {
            on { this.spot } doReturn spot
            on { this.site } doReturn site
            on { this.isDisplayControl() } doReturn true
            on { this.hasOtherThanBanner() } doReturn false
            on { this.hasBanner() } doReturn true
        }
        val bannerSizeTypeIds = listOf(1, 2, 3).map { SizeTypeId(it) }
        val nativeSizeTypeIds = listOf(SizeTypeId.nativePc, SizeTypeId.nativeSp)
        val relaySpotSizeTypes = (bannerSizeTypeIds + nativeSizeTypeIds).map { mockRelaySpotSizeType(it) }
        val currentSizeTypes = bannerSizeTypeIds.map { mockSizeTypeInfo(it) }
        val deleteSizeTypes: List<SizeTypeInfo> = mock()
        val spotSizeTypeDeleteRule: SpotSizeTypeDeleteRule = mock()
        val nextSizeTypeIds: List<SizeTypeId> = mock()
        val validation: SpotBannerEditValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotBannerEditValidation)
            every {
                SpotBannerEditValidation.of(any(), any(), any(), any(), any(), any(), any(), any(), any())
            } returns validation

            doReturn(existingSpot).whenever(sut).getExistingSpot(any(), any(), any())
            doReturn(relaySpotSizeTypes).whenever(relaySpotSizetypeDao).selectBySpotId(any())
            doReturn(currentSizeTypes).whenever(spotGetWithCheckHelper).getSizeTypeInfosWithCheck(any())
            doNothing().whenever(sut).checkConflict(any(), any())
            doNothing().whenever(sut).checkBanner(any(), anyOrNull())
            doReturn(spotSizeTypeDeleteRule).whenever(spotSizeTypeDeleteRuleHelper).getRule(any())
            doReturn(deleteSizeTypes).whenever(sut).getDeleteSizeTypes(anyOrNull(), any())
            doReturn(nextSizeTypeIds)
                .whenever(sizeTypeInfoPersistHelper)
                .bulkCreate(any(), any(), any<List<SizeTypeForm>>())
        }

        @Test
        @DisplayName("バナー設定あり")
        fun isActiveBanner() {
            val form: SpotBannerEditForm = mock()
            val sizeTypeForms: List<SizeTypeForm> = mock()
            val correctedBannerForm: BannerSettingForm = mock {
                on { this.sizeTypes } doReturn sizeTypeForms
            }
            val correctedForm = SpotBannerEditForm(correctedBannerForm, mock())
            doReturn(correctedForm).whenever(sut).correctSpotBannerEditForm(any(), any(), any())

            sut.editBanner(coAccountId, spotId, UserType.ma_staff, form)

            verify(sut, times(1)).getExistingSpot(coAccountId, spotId, UserType.ma_staff)
            verify(relaySpotSizetypeDao, times(1)).selectBySpotId(spotId)
            verify(spotGetWithCheckHelper, times(1)).getSizeTypeInfosWithCheck(bannerSizeTypeIds)
            verify(sut, times(1)).correctSpotBannerEditForm(form, existingSpot, UserType.ma_staff)
            verify(sut, times(1)).checkConflict(spot, correctedForm.updateTime)
            verify(sut, times(1)).checkBanner(coAccountId, correctedBannerForm)
            verify(sut, times(1)).getDeleteSizeTypes(correctedBannerForm, currentSizeTypes)
            verify(spotSizeTypeDeleteRuleHelper, times(1)).getRule(spotId)
            verifyK {
                SpotBannerEditValidation.of(
                    correctedForm, UserType.ma_staff, spot, site, true, false, deleteSizeTypes, spotSizeTypeDeleteRule,
                    true
                )
            }
            verify(sizeTypeInfoPersistHelper, times(1)).bulkCreate(coAccountId, platformId, sizeTypeForms)
            verify(spotPersistHelper, times(1)).editBanner(
                spotId, correctedBannerForm, true, true, bannerSizeTypeIds, nextSizeTypeIds
            )
        }

        @Test
        @DisplayName("バナー設定なし")
        fun isInactiveBanner() {
            val form: SpotBannerEditForm = mock()
            val correctedForm = SpotBannerEditForm(null, mock())
            doReturn(correctedForm).whenever(sut).correctSpotBannerEditForm(any(), any(), any())

            sut.editBanner(coAccountId, spotId, UserType.ma_staff, form)

            verify(sut, times(1)).getExistingSpot(coAccountId, spotId, UserType.ma_staff)
            verify(relaySpotSizetypeDao, times(1)).selectBySpotId(spotId)
            verify(spotGetWithCheckHelper, times(1)).getSizeTypeInfosWithCheck(bannerSizeTypeIds)
            verify(sut, times(1)).correctSpotBannerEditForm(form, existingSpot, UserType.ma_staff)
            verify(sut, times(1)).checkConflict(spot, correctedForm.updateTime)
            verify(sut, times(1)).checkBanner(coAccountId, null)
            verify(sut, times(1)).getDeleteSizeTypes(null, currentSizeTypes)
            verify(spotSizeTypeDeleteRuleHelper, times(1)).getRule(spotId)
            verifyK {
                SpotBannerEditValidation.of(
                    correctedForm, UserType.ma_staff, spot, site, true, false, deleteSizeTypes, spotSizeTypeDeleteRule,
                    true
                )
            }
            verify(sizeTypeInfoPersistHelper, times(1)).bulkCreate(coAccountId, platformId, emptyList())
            verify(spotPersistHelper, times(1)).editBanner(
                spotId, null, true, true, bannerSizeTypeIds, nextSizeTypeIds
            )
        }
    }

    @Nested
    @DisplayName("correctSpotBannerEditFormのテスト")
    inner class CorrectSpotBannerEditFormTest {
        val existingSpot: ExistingSpot = mock()

        @Test
        @DisplayName("社員のとき")
        fun isMaStaff() {
            val form: SpotBannerEditForm = mock()

            val actual = sut.correctSpotBannerEditForm(form, mock(), UserType.ma_staff)

            assertEquals(form, actual)
        }

        @Nested
        @TestInstance(Lifecycle.PER_CLASS)
        @DisplayName("社員以外のとき")
        inner class NotMaStaffTest {
            val sizeTypeForms: List<SizeTypeForm> = mock()

            @ParameterizedTest
            @EnumSource(value = UserType::class, names = ["ma_staff"], mode = EnumSource.Mode.EXCLUDE)
            @DisplayName("新規のとき")
            fun isNew(userType: UserType) {
                val banner: BannerSettingForm = mock()
                val form = SpotBannerEditForm(banner, mock())
                doReturn(false).whenever(existingSpot).hasBanner()

                val actual = sut.correctSpotBannerEditForm(form, existingSpot, userType)

                assertEquals(form, actual)
            }

            @ParameterizedTest
            @EnumSource(value = UserType::class, names = ["ma_staff"], mode = EnumSource.Mode.EXCLUDE)
            @DisplayName("削除のとき")
            fun isDelete(userType: UserType) {
                val form = SpotBannerEditForm(null, mock())
                doReturn(true).whenever(existingSpot).hasBanner()

                val actual = sut.correctSpotBannerEditForm(form, existingSpot, userType)

                assertEquals(form, actual)
            }

            @ParameterizedTest
            @MethodSource("editParams")
            @DisplayName("編集のとき")
            fun isEdit(spotBannerDisplay: SpotBannerDisplay, expected: SpotBannerEditForm, userType: UserType) {
                val form = form(false, mock())
                doReturn(true).whenever(existingSpot).hasBanner()
                doReturn(spotBannerDisplay).whenever(existingSpot).spotBannerDisplay

                val actual = sut.correctSpotBannerEditForm(form, existingSpot, userType)

                assertEquals(expected, actual)
            }

            private fun editParams() = listOf(
                Arguments.of(
                    spotBannerDisplay(true, null, null, null, null, null),
                    form(true, null),
                    UserType.agency
                ),
                Arguments.of(
                    spotBannerDisplay(true, 1, 2, "rgba(10,20,30,0.4)", "rgba(20,30,40,0.5)", "rgba(30,40,50,0.6)"),
                    form(
                        true,
                        CloseButtonForm(
                            1,
                            2,
                            CloseButtonForm.ColorForm.of("rgba(10,20,30,0.4)"),
                            CloseButtonForm.ColorForm.of("rgba(20,30,40,0.5)"),
                            CloseButtonForm.ColorForm.of("rgba(30,40,50,0.6)")
                        )
                    ),
                    UserType.client
                ),
                Arguments.of(
                    spotBannerDisplay(true, 1, 2, "rgba(10,20,30,0.4)", "rgba(20,30,40,0.5)", "rgba(30,40,50,0.6)"),
                    form(
                        true,
                        CloseButtonForm(
                            1,
                            2,
                            CloseButtonForm.ColorForm.of("rgba(10,20,30,0.4)"),
                            CloseButtonForm.ColorForm.of("rgba(20,30,40,0.5)"),
                            CloseButtonForm.ColorForm.of("rgba(30,40,50,0.6)")
                        )
                    ),
                    UserType.other
                )
            )

            private fun form(isScalable: Boolean, closeButton: CloseButtonForm?) = SpotBannerEditForm(
                BannerSettingForm(sizeTypeForms, isScalable, true, closeButton, null),
                LocalDateTime.MIN
            )

            private fun spotBannerDisplay(
                isScalable: Boolean, closeButtonType: Int?, closeButtonSize: Int?, closeButtonLineColor: String?,
                closeButtonBgColor: String?, closeButtonFrameColor: String?
            ): SpotBannerDisplay = mock {
                on { this.isScalable } doReturn isScalable
                on { this.closeButtonType } doReturn closeButtonType
                on { this.closeButtonSize } doReturn closeButtonSize
                on { this.closeButtonLineColor } doReturn closeButtonLineColor
                on { this.closeButtonBgColor } doReturn closeButtonBgColor
                on { this.closeButtonFrameColor } doReturn closeButtonFrameColor
            }
        }
    }

    @Nested
    @DisplayName("getDeleteSizeTypesのテスト")
    inner class GetDeleteSizeTypesTest {
        val currentSizeTypeInfos = listOf(
            mockSizeTypeInfo(100, 200),
            mockSizeTypeInfo(300, 400),
            mockSizeTypeInfo(500, 600),
            mockSizeTypeInfo(700, 800)
        )

        @Test
        @DisplayName("バナー設定あり")
        fun isActiveBanner() {
            val form = listOf(
                mockSizeTypeForm(100, 200),
                mockSizeTypeForm(500, 600),
                mockSizeTypeForm(900, 1000)
            ).let { BannerSettingForm(it, true, true, null, null) }

            val actual = sut.getDeleteSizeTypes(form, currentSizeTypeInfos)

            assertEquals(
                listOf(currentSizeTypeInfos[1], currentSizeTypeInfos[3]),
                actual
            )
        }

        @Test
        @DisplayName("バナー設定なし")
        fun isInactiveBanner() {
            val actual = sut.getDeleteSizeTypes(null, currentSizeTypeInfos)

            assertEquals(currentSizeTypeInfos, actual)
        }

        fun mockSizeTypeInfo(width: Int, height: Int): SizeTypeInfo = mock {
            on { this.width } doReturn width
            on { this.height } doReturn height
        }

        fun mockSizeTypeForm(width: Int, height: Int): SizeTypeForm = mock {
            on { this.width } doReturn width
            on { this.height } doReturn height
        }
    }

    @Nested
    @DisplayName("editNativeのテスト")
    inner class EditNativeTest {
        val spot: Spot = mock()
        val site: Site = mock()
        val existingSpot: ExistingSpot = mock {
            on { this.spot } doReturn spot
            on { this.site } doReturn site
            on { this.isDisplayControl() } doReturn false
            on { this.hasOtherThanNative() } doReturn true
            on { this.hasNativeStandard() } doReturn false
            on { this.hasNativeVideo() } doReturn true
            on { this.getNativeSizeTypeId() } doReturn SizeTypeId.nativePc
        }
        val bannerSizeTypeIds = listOf(1, 2, 3).map { SizeTypeId(it) }
        val nativeSizeTypeIds = listOf(SizeTypeId.nativePc, SizeTypeId.nativeSp)
        val relaySpotSizetypes = (bannerSizeTypeIds + nativeSizeTypeIds).map { mockRelaySpotSizeType(it) }
        val currentSizeTypes = nativeSizeTypeIds.map { mockSizeTypeInfo(it) }
        val spotSizeTypeDeleteRule: SpotSizeTypeDeleteRule = mock()
        val validation: SpotNativeEditValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotNativeEditValidation)
            every {
                SpotNativeEditValidation.of(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
            } returns validation

            doReturn(existingSpot).whenever(sut).getExistingSpot(any(), any(), any())
            doReturn(relaySpotSizetypes).whenever(relaySpotSizetypeDao).selectBySpotId(any())
            doReturn(currentSizeTypes).whenever(spotGetWithCheckHelper).getSizeTypeInfosWithCheck(any())
            doReturn(spotSizeTypeDeleteRule).whenever(spotSizeTypeDeleteRuleHelper).getRule(any())
            doReturn(emptyErrors).whenever(validator).validate(any<SpotNativeEditValidation>())
            doNothing().whenever(sut).checkConflict(any(), any())
            doNothing().whenever(sut).checkNative(any(), anyOrNull(), any())
            doNothing().whenever(spotPersistHelper).editNative(any(), anyOrNull(), any(), any(), any(), any(), any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("ネイティブ設定あり")
        fun isActiveNative() {
            val form: SpotNativeEditForm = mock()
            val nativeForm: NativeSettingForm = mock()
            val correctedForm = SpotNativeEditForm(nativeForm, mock())
            doReturn(correctedForm).whenever(sut).correctSpotNativeEditForm(any(), any(), any())

            sut.editNative(coAccountId, spotId, UserType.agency, form)

            verify(sut, times(1)).getExistingSpot(coAccountId, spotId, UserType.agency)
            verify(relaySpotSizetypeDao, times(1)).selectBySpotId(spotId)
            verify(spotGetWithCheckHelper, times(1)).getSizeTypeInfosWithCheck(nativeSizeTypeIds)
            verify(sut, times(1)).correctSpotNativeEditForm(form, existingSpot, UserType.agency)
            verify(sut, times(1)).checkConflict(spot, correctedForm.updateTime)
            verify(sut, times(1)).checkNative(coAccountId, nativeForm, site)
            verify(spotSizeTypeDeleteRuleHelper, times(1)).getRule(spotId)
            verifyK {
                SpotNativeEditValidation.of(
                    correctedForm, UserType.agency, spot, site, false, true, emptyList(), spotSizeTypeDeleteRule,
                    false, true
                )
            }
            verify(spotPersistHelper, times(1)).editNative(
                spotId, nativeForm, false, false, true, nativeSizeTypeIds, listOf(SizeTypeId.nativePc)
            )
        }

        @Test
        @DisplayName("ネイティブ設定なし")
        fun isInactiveNative() {
            val form: SpotNativeEditForm = mock()
            val correctedForm = SpotNativeEditForm(null, mock())
            doReturn(correctedForm).whenever(sut).correctSpotNativeEditForm(any(), any(), any())

            sut.editNative(coAccountId, spotId, UserType.agency, form)

            verify(sut, times(1)).getExistingSpot(coAccountId, spotId, UserType.agency)
            verify(relaySpotSizetypeDao, times(1)).selectBySpotId(spotId)
            verify(spotGetWithCheckHelper, times(1)).getSizeTypeInfosWithCheck(nativeSizeTypeIds)
            verify(sut, times(1)).correctSpotNativeEditForm(form, existingSpot, UserType.agency)
            verify(sut, times(1)).checkConflict(spot, correctedForm.updateTime)
            verify(sut, times(1)).checkNative(coAccountId, null, site)
            verify(spotSizeTypeDeleteRuleHelper, times(1)).getRule(spotId)
            verifyK {
                SpotNativeEditValidation.of(
                    correctedForm, UserType.agency, spot, site, false, true, currentSizeTypes, spotSizeTypeDeleteRule,
                    false, true
                )
            }
            verify(spotPersistHelper, times(1)).editNative(
                spotId, null, false, false, true, nativeSizeTypeIds, emptyList()
            )
        }
    }

    @Nested
    @DisplayName("correctSpotNativeEditFormのテスト")
    inner class CorrectSpotNativeEditFormTest {
        @Test
        @DisplayName("社員のとき")
        fun isMaStaff() {
            val form: SpotNativeEditForm = mock()

            val actual = sut.correctSpotNativeEditForm(form, mock(), UserType.ma_staff)

            assertEquals(form, actual)
        }

        @Nested
        @DisplayName("社員以外のとき")
        inner class NotMaStaffTest {
            val existingSpot: ExistingSpot = mock()

            @ParameterizedTest
            @EnumSource(value = UserType::class, names = ["ma_staff"], mode = EnumSource.Mode.EXCLUDE)
            @DisplayName("新規のとき")
            fun isNew(userType: UserType) {
                val native: NativeSettingForm = mock()
                val form = SpotNativeEditForm(native, mock())
                doReturn(false).whenever(existingSpot).hasNative()

                val actual = sut.correctSpotNativeEditForm(form, existingSpot, userType)

                assertEquals(form, actual)
            }

            @ParameterizedTest
            @EnumSource(value = UserType::class, names = ["ma_staff"], mode = EnumSource.Mode.EXCLUDE)
            @DisplayName("削除のとき")
            fun isDelete(userType: UserType) {
                val form = SpotNativeEditForm(null, mock())
                doReturn(true).whenever(existingSpot).hasNative()

                val actual = sut.correctSpotNativeEditForm(form, existingSpot, userType)

                assertEquals(form, actual)
            }

            @ParameterizedTest
            @EnumSource(value = UserType::class, names = ["ma_staff"], mode = EnumSource.Mode.EXCLUDE)
            @DisplayName("編集のとき")
            fun isEdit(userType: UserType) {
                val native = NativeSettingForm(mock(), mock())
                val form: SpotNativeEditForm = SpotNativeEditForm(native, mock())

                val standardDisplay: SpotNativeDisplay = mock()
                val videoDisplay: SpotNativeVideoDisplay = mock()
                doReturn(standardDisplay).whenever(existingSpot).spotNativeDisplay
                doReturn(videoDisplay).whenever(existingSpot).spotNativeVideoDisplay
                doReturn(true).whenever(existingSpot).hasNative()

                val standardForm: NativeStandardForm = mock()
                val videoForm: NativeVideoForm = mock()
                doReturn(standardForm).whenever(sut).correctNativeStandardForm(any(), any())
                doReturn(videoForm).whenever(sut).correctNativeVideoForm(any(), any())

                val actual = sut.correctSpotNativeEditForm(form, existingSpot, userType)

                assertEquals(
                    SpotNativeEditForm(NativeSettingForm(standardForm, videoForm), form.updateTime),
                    actual
                )
            }
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    @DisplayName("correctNativeStandardFormのテスト")
    inner class CorrectNativeStandardFormTest {
        @Test
        @DisplayName("新規のとき")
        fun isNew() {
            val form: NativeStandardForm = mock()

            val actual = sut.correctNativeStandardForm(form, null)

            assertEquals(form, actual)
        }

        @Test
        @DisplayName("削除のとき")
        fun isDelete() {
            assertNull(sut.correctNativeStandardForm(null, mock()))
        }

        @ParameterizedTest
        @MethodSource("editParams")
        @DisplayName("編集のとき")
        fun isEdit(spotNativeDisplay: SpotNativeDisplay, expected: NativeStandardForm) {
            val form = form(mock())

            val actual = sut.correctNativeStandardForm(form, spotNativeDisplay)

            assertEquals(expected, actual)
        }

        private fun editParams() = listOf(
            Arguments.of(
                spotNativeDisplay(null, null, null, null, null),
                form(null)
            ),
            Arguments.of(
                spotNativeDisplay(1, 2, "rgba(10,20,30,0.4)", "rgba(20,30,40,0.5)", "rgba(30,40,50,0.6)"),
                form(
                    CloseButtonForm(
                        1,
                        2,
                        CloseButtonForm.ColorForm.of("rgba(10,20,30,0.4)"),
                        CloseButtonForm.ColorForm.of("rgba(20,30,40,0.5)"),
                        CloseButtonForm.ColorForm.of("rgba(30,40,50,0.6)")
                    )
                )
            )
        )

        private fun form(closeButton: CloseButtonForm?) = NativeStandardForm(null, closeButton)

        private fun spotNativeDisplay(
            closeButtonType: Int?, closeButtonSize: Int?, closeButtonLineColor: String?,
            closeButtonBgColor: String?, closeButtonFrameColor: String?
        ): SpotNativeDisplay = mock {
            on { this.closeButtonType } doReturn closeButtonType
            on { this.closeButtonSize } doReturn closeButtonSize
            on { this.closeButtonLineColor } doReturn closeButtonLineColor
            on { this.closeButtonBgColor } doReturn closeButtonBgColor
            on { this.closeButtonFrameColor } doReturn closeButtonFrameColor
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    @DisplayName("correctNativeVideoFormのテスト")
    inner class CorrectNativeVideoFormTest {
        @Test
        @DisplayName("新規のとき")
        fun isNew() {
            val form: NativeVideoForm = mock()

            val actual = sut.correctNativeVideoForm(form, null)

            assertEquals(form, actual)
        }

        @Test
        @DisplayName("削除のとき")
        fun isDelete() {
            assertNull(sut.correctNativeVideoForm(null, mock()))
        }

        @ParameterizedTest
        @MethodSource("editParams")
        @DisplayName("編集のとき")
        fun isEdit(spotNativeVideoDisplay: SpotNativeVideoDisplay, expected: NativeVideoForm) {
            val form = form(false, mock())

            val actual = sut.correctNativeVideoForm(form, spotNativeVideoDisplay)

            assertEquals(expected, actual)
        }

        private fun editParams() = listOf(
            Arguments.of(
                spotNativeVideoDisplay(true, null, null, null, null, null),
                form(true, null)
            ),
            Arguments.of(
                spotNativeVideoDisplay(true, 1, 2, "rgba(10,20,30,0.4)", "rgba(20,30,40,0.5)", "rgba(30,40,50,0.6)"),
                form(
                    true,
                    CloseButtonForm(
                        1,
                        2,
                        CloseButtonForm.ColorForm.of("rgba(10,20,30,0.4)"),
                        CloseButtonForm.ColorForm.of("rgba(20,30,40,0.5)"),
                        CloseButtonForm.ColorForm.of("rgba(30,40,50,0.6)")
                    )
                )
            )
        )

        private fun form(isScalable: Boolean, closeButton: CloseButtonForm?) =
            NativeVideoForm(null, closeButton, false)

        private fun spotNativeVideoDisplay(
            isScalable: Boolean, closeButtonType: Int?, closeButtonSize: Int?, closeButtonLineColor: String?,
            closeButtonBgColor: String?, closeButtonFrameColor: String?
        ): SpotNativeVideoDisplay = mock {
            on { this.isScalable } doReturn isScalable
            on { this.closeButtonType } doReturn closeButtonType
            on { this.closeButtonSize } doReturn closeButtonSize
            on { this.closeButtonLineColor } doReturn closeButtonLineColor
            on { this.closeButtonBgColor } doReturn closeButtonBgColor
            on { this.closeButtonFrameColor } doReturn closeButtonFrameColor
        }
    }

    @Nested
    @DisplayName("editVideoのテスト")
    inner class EditVideoTest {
        val spot: Spot = mock()
        val site: Site = mock()
        val existingAspectRatioIds = listOf(aspectRatio16to9, aspectRatio16to5)
        val currentSpotVideoDisplays: List<SpotVideoDisplay> =
            existingAspectRatioIds.map { id ->
                mock {
                    on { this.aspectRatioId } doReturn id
                }
            }
        val currentSpotVideoFloorCpms: List<SpotVideoFloorCpm> = mock()
        val existingSpot: ExistingSpot = mock {
            on { this.spot } doReturn spot
            on { this.site } doReturn site
            on { this.spotVideoDisplays } doReturn currentSpotVideoDisplays
            on { this.isDisplayControl() } doReturn false
            on { this.hasOtherThanVideo() } doReturn true
            on { this.hasVideo() } doReturn false
        }
        val aspectRatios: List<AspectRatio> = mock()
        val validation: SpotVideoEditValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotVideoEditValidation)
            every {
                SpotVideoEditValidation.of(any(), any(), any(), any(), any(), any(), any())
            } returns validation

            doReturn(existingSpot).whenever(sut).getExistingSpot(any(), any(), any())
            doReturn(currentSpotVideoFloorCpms).whenever(spotVideoFloorCpmDao).selectBySpotId(any())
            doReturn(emptyErrors).whenever(validator).validate(any<SpotNativeEditValidation>())
            doNothing().whenever(sut).checkConflict(any(), any())
            doNothing().whenever(spotPersistHelper).editVideo(any(), any(), any(), any(), any(), any(), any())
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("ビデオ設定あり")
        fun isActiveVideo() {
            val form: SpotVideoEditForm = mock()
            val videoForm: VideoSettingForm = mock()
            val correctedForm = SpotVideoEditForm(videoForm, mock())
            doReturn(correctedForm).whenever(sut).correctSpotVideoEditForm(any(), any(), any())
            doReturn(aspectRatios).whenever(sut).getAspectRatios(any())

            sut.editVideo(coAccountId, spotId, UserType.agency, form)

            verify(sut, times(1)).getExistingSpot(coAccountId, spotId, UserType.agency)
            verify(spotVideoFloorCpmDao, times(1)).selectBySpotId(spotId)
            verify(sut, times(1)).correctSpotVideoEditForm(form, existingSpot, UserType.agency)
            verify(sut, times(1)).getAspectRatios(videoForm)
            verify(sut, times(1)).checkConflict(spot, correctedForm.updateTime)
            verifyK {
                SpotVideoEditValidation.of(
                    correctedForm, UserType.agency, spot, site, false, true, existingAspectRatioIds
                )
            }
            verify(sut, times(1)).checkConflict(spot, correctedForm.updateTime)
            verify(spotPersistHelper, times(1)).editVideo(
                spotId, videoForm, false, false, currentSpotVideoDisplays, currentSpotVideoFloorCpms, aspectRatios
            )
        }

        @Test
        @DisplayName("ビデオ設定なし")
        fun isInactiveVideo() {
            doReturn(emptyList<AspectRatio>()).whenever(sut).getAspectRatios(anyOrNull())

            val form: SpotVideoEditForm = mock()
            val correctedForm = SpotVideoEditForm(null, mock())
            doReturn(correctedForm).whenever(sut).correctSpotVideoEditForm(any(), any(), any())

            sut.editVideo(coAccountId, spotId, UserType.agency, form)

            verify(sut, times(1)).getExistingSpot(coAccountId, spotId, UserType.agency)
            verify(spotVideoFloorCpmDao, times(1)).selectBySpotId(spotId)
            verify(sut, times(1)).correctSpotVideoEditForm(form, existingSpot, UserType.agency)
            verify(sut, times(1)).getAspectRatios(null)
            verify(sut, times(1)).checkConflict(spot, correctedForm.updateTime)
            verifyK {
                SpotVideoEditValidation.of(
                    correctedForm, UserType.agency, spot, site, false, true, existingAspectRatioIds
                )
            }
            verify(spotPersistHelper, times(1)).editVideo(
                spotId, null, false, false, currentSpotVideoDisplays, currentSpotVideoFloorCpms, emptyList()
            )
        }
    }

    @Nested
    @DisplayName("correctSpotVideoEditFormのテスト")
    inner class CorrectSpotVideoEditFormTest {
        @Test
        @DisplayName("社員のとき")
        fun isMaStaff() {
            val form: SpotVideoEditForm = mock()

            val actual = sut.correctSpotVideoEditForm(form, mock(), UserType.ma_staff)

            assertEquals(form, actual)
        }

        @Nested
        @DisplayName("社員以外のとき")
        inner class NotMaStaffTest {
            val existingSpot: ExistingSpot = mock()

            @ParameterizedTest
            @EnumSource(value = UserType::class, names = ["ma_staff"], mode = EnumSource.Mode.EXCLUDE)
            @DisplayName("新規のとき")
            fun isNew(userType: UserType) {
                val video: VideoSettingForm = mock()
                val form = SpotVideoEditForm(video, mock())
                doReturn(false).whenever(existingSpot).hasVideo()

                val actual = sut.correctSpotVideoEditForm(form, existingSpot, userType)

                assertEquals(form, actual)
            }

            @ParameterizedTest
            @EnumSource(value = UserType::class, names = ["ma_staff"], mode = EnumSource.Mode.EXCLUDE)
            @DisplayName("削除のとき")
            fun isDelete(userType: UserType) {
                val form = SpotVideoEditForm(null, mock())
                doReturn(true).whenever(existingSpot).hasVideo()

                val actual = sut.correctSpotVideoEditForm(form, existingSpot, userType)

                assertEquals(form, actual)
            }

            @ParameterizedTest
            @EnumSource(value = UserType::class, names = ["ma_staff"], mode = EnumSource.Mode.EXCLUDE)
            @DisplayName("編集のとき")
            fun isEdit(userType: UserType) {
                val details: List<VideoDetailForm> = listOf(mock())
                val correctedDetails: List<VideoDetailForm> = listOf(mock())
                val video = VideoSettingForm(1, true, 2, details)
                val form = SpotVideoEditForm(video, mock())
                doReturn(correctedDetails[0]).whenever(sut).correctVideoDetailForm(any(), any())

                val spotVideoDisplays: List<SpotVideoDisplay> = mock()
                doReturn(spotVideoDisplays).whenever(existingSpot).spotVideoDisplays
                doReturn(true).whenever(existingSpot).hasVideo()

                val actual = sut.correctSpotVideoEditForm(form, existingSpot, userType)

                assertEquals(
                    SpotVideoEditForm(
                        VideoSettingForm(1, true, 2, correctedDetails),
                        form.updateTime
                    ),
                    actual
                )
                verify(sut, times(1)).correctVideoDetailForm(details[0], spotVideoDisplays)
            }
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    @DisplayName("correctVideoDetailFormのテスト")
    inner class CorrectVideoDetailFormTest {
        val displayPosition: VideoDetailForm.VideoDisplayPositionForm = mock()
        val spotVideoDisplays = listOf(
            spotVideoDisplay(aspectRatio16to9, true, null, null, null, null, null),
            spotVideoDisplay(
                aspectRatio32to5,
                false,
                1,
                2,
                "rgba(10,20,30,0.4)",
                "rgba(20,30,40,0.5)",
                "rgba(30,40,50,0.6)"
            )
        )

        @Test
        @DisplayName("新規のとき")
        fun isNew() {
            val form = form(aspectRatio16to5, true, mock())

            val actual = sut.correctVideoDetailForm(form, spotVideoDisplays)

            assertEquals(form, actual)
        }

        @ParameterizedTest
        @MethodSource("editParams")
        @DisplayName("編集のとき")
        fun isEdit(form: VideoDetailForm, expected: VideoDetailForm) {
            val actual = sut.correctVideoDetailForm(form, spotVideoDisplays)

            assertEquals(expected, actual)
        }

        private fun editParams() = listOf(
            Arguments.of(
                form(
                    aspectRatio16to9,
                    false,
                    CloseButtonForm(
                        1,
                        2,
                        CloseButtonForm.ColorForm.of("rgba(10,20,30,0.4)"),
                        CloseButtonForm.ColorForm.of("rgba(20,30,40,0.5)"),
                        CloseButtonForm.ColorForm.of("rgba(30,40,50,0.6)")
                    )
                ),
                form(aspectRatio16to9, true, null)
            ),
            Arguments.of(
                form(aspectRatio32to5, true, null),
                form(
                    aspectRatio32to5,
                    false,
                    CloseButtonForm(
                        1,
                        2,
                        CloseButtonForm.ColorForm.of("rgba(10,20,30,0.4)"),
                        CloseButtonForm.ColorForm.of("rgba(20,30,40,0.5)"),
                        CloseButtonForm.ColorForm.of("rgba(30,40,50,0.6)")
                    )
                )
            )
        )

        private fun form(
            aspectRatioId: AspectRatioId,
            isScalable: Boolean,
            closeButton: CloseButtonForm?
        ) = VideoDetailForm(
            aspectRatioId, isScalable, 10, closeButton, displayPosition, true, false, BigDecimal.ONE,
            LocalDate.MIN
        )

        private fun spotVideoDisplay(
            aspectRatioId: AspectRatioId, isScalable: Boolean, closeButtonType: Int?, closeButtonSize: Int?,
            closeButtonLineColor: String?, closeButtonBgColor: String?, closeButtonFrameColor: String?
        ): SpotVideoDisplay = mock {
            on { this.aspectRatioId } doReturn aspectRatioId
            on { this.isScalable } doReturn isScalable
            on { this.closeButtonType } doReturn closeButtonType
            on { this.closeButtonSize } doReturn closeButtonSize
            on { this.closeButtonLineColor } doReturn closeButtonLineColor
            on { this.closeButtonBgColor } doReturn closeButtonBgColor
            on { this.closeButtonFrameColor } doReturn closeButtonFrameColor
        }
    }

    @Nested
    @DisplayName("checkConflictのテスト")
    inner class CheckConflictTest {
        val spot: Spot = mock {
            on { this.updateTime } doReturn LocalDateTime.MIN
        }

        @Test
        @DisplayName("コンフリクトあり")
        fun isConflict() {
            assertThrows<ResourceConflictException> {
                sut.checkConflict(spot, LocalDateTime.MIN.plusSeconds(1))
            }
        }

        @Test
        @DisplayName("コンフリクトなし")
        fun isNotConflict() {
            assertDoesNotThrow { sut.checkConflict(spot, LocalDateTime.MIN) }
        }
    }

    @Nested
    @DisplayName("getExistingSpotのテスト")
    inner class GetExistingSpotTest {
        val spot: Spot = mock {
            on { this.spotId } doReturn spotId
            on { this.siteId } doReturn siteId
        }
        val site: Site = mock()
        val spotBanner: SpotBanner = mock()
        val spotBannerDisplay: SpotBannerDisplay = mock()
        val spotNative: SpotNative = mock()
        val spotNativeDisplay: SpotNativeDisplay = mock()
        val spotNativeVideoDisplay: SpotNativeVideoDisplay = mock()
        val spotVideo: SpotVideo = mock()
        val spotVideoDisplays: List<SpotVideoDisplay> = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotUtils)
            every { SpotUtils.checkSpotBannerConsistency(any(), any(), any()) } returns Unit
            every { SpotUtils.checkSpotNativeConsistency(any(), any(), any(), any()) } returns Unit
            every { SpotUtils.checkSpotVideoConsistency(any(), any(), any()) } returns Unit
        }

        @Test
        @DisplayName("正常 - 全フォーマットあり")
        fun isCorrectAndAllFormat() {
            doReturn(spot).whenever(spotGetWithCheckHelper).getSpotWithCheck(any(), any(), any())
            doReturn(site).whenever(spotGetWithCheckHelper).getSiteWithCheck(any(), any(), any())
            doReturn(spotBanner).whenever(spotBannerDao).selectById(any())
            doReturn(spotBannerDisplay).whenever(spotBannerDisplayDao).selectById(any())
            doReturn(spotNative).whenever(spotNativeDao).selectById(any())
            doReturn(spotNativeDisplay).whenever(spotNativeDisplayDao).selectById(any())
            doReturn(spotNativeVideoDisplay).whenever(spotNativeVideoDisplayDao).selectBySpotId(any())
            doReturn(spotVideo).whenever(spotVideoDao).selectById(any())
            doReturn(spotVideoDisplays).whenever(spotVideoDisplayDao).selectBySpotId(any())

            val actual = sut.getExistingSpot(coAccountId, spotId, UserType.ma_staff)

            assertEquals(
                ExistingSpot(
                    spot,
                    site,
                    spotBannerDisplay,
                    spotNativeDisplay,
                    spotNativeVideoDisplay,
                    spotVideoDisplays
                ),
                actual
            )
            verify(spotGetWithCheckHelper, times(1)).getSpotWithCheck(
                spotId, SpotStatus.editableStatuses, UserType.ma_staff
            )
            verify(spotGetWithCheckHelper, times(1)).getSiteWithCheck(coAccountId, siteId, SiteStatus.entries)
            verify(spotBannerDao, times(1)).selectById(spotId)
            verify(spotBannerDisplayDao, times(1)).selectById(spotId)
            verify(spotNativeDao, times(1)).selectById(spotId)
            verify(spotNativeDisplayDao, times(1)).selectById(spotId)
            verify(spotNativeVideoDisplayDao, times(1)).selectBySpotId(spotId)
            verify(spotVideoDao, times(1)).selectById(spotId)
            verify(spotVideoDisplayDao, times(1)).selectBySpotId(spotId)
            verifyK { SpotUtils.checkSpotBannerConsistency(spotId, spotBanner, spotBannerDisplay) }
            verifyK {
                SpotUtils.checkSpotNativeConsistency(spotId, spotNative, spotNativeDisplay, spotNativeVideoDisplay)
            }
            verifyK { SpotUtils.checkSpotVideoConsistency(spotId, spotVideo, spotVideoDisplays) }
        }

        @Test
        @DisplayName("正常 - 全フォーマットなし")
        fun isCorrectAndNoFormat() {
            doReturn(spot).whenever(spotGetWithCheckHelper).getSpotWithCheck(any(), any(), any())
            doReturn(site).whenever(spotGetWithCheckHelper).getSiteWithCheck(any(), any(), any())
            doReturn(null).whenever(spotBannerDao).selectById(any())
            doReturn(null).whenever(spotNativeDao).selectById(any())
            doReturn(null).whenever(spotVideoDao).selectById(any())

            val actual = sut.getExistingSpot(coAccountId, spotId, UserType.ma_staff)

            assertEquals(
                ExistingSpot(spot, site, null, null, null, emptyList()),
                actual
            )
            verify(spotGetWithCheckHelper, times(1)).getSpotWithCheck(
                spotId, SpotStatus.editableStatuses, UserType.ma_staff
            )
            verify(spotGetWithCheckHelper, times(1)).getSiteWithCheck(coAccountId, siteId, SiteStatus.entries)
            verify(spotBannerDao, times(1)).selectById(spotId)
            verify(spotBannerDisplayDao, never()).selectById(any())
            verify(spotNativeDao, times(1)).selectById(spotId)
            verify(spotNativeDisplayDao, never()).selectById(any())
            verify(spotNativeVideoDisplayDao, never()).selectBySpotId(any())
            verify(spotVideoDao, times(1)).selectById(spotId)
            verify(spotVideoDisplayDao, never()).selectBySpotId(any())
            verifyK { SpotUtils.checkSpotBannerConsistency(spotId, null, null) }
            verifyK { SpotUtils.checkSpotNativeConsistency(spotId, null, null, null) }
            verifyK { SpotUtils.checkSpotVideoConsistency(spotId, null, emptyList()) }
        }
    }

    @Nested
    @DisplayName("ExistingSpotのテスト")
    inner class ExistingSpotTest {
        @Nested
        @DisplayName("isDisplayControlのテスト")
        inner class IsDisplayControlTest {
            @BeforeEach
            fun beforeEach() {
                mockkObject(SpotUtils)
                every { SpotUtils.isDisplayControl(any(), any(), any(), any()) } returns true
            }

            @AfterEach
            fun afterEach() {
                unmockkAll()
            }

            @Test
            @DisplayName("正常")
            fun isCorrect() {
                val sut = ExistingSpot(mock(), mock(), mock(), mock(), mock(), mock())

                assertTrue(sut.isDisplayControl())

                verifyK {
                    SpotUtils.isDisplayControl(
                        sut.spotBannerDisplay,
                        sut.spotNativeDisplay,
                        sut.spotNativeVideoDisplay,
                        sut.spotVideoDisplays
                    )
                }
            }
        }

        @Nested
        @DisplayName("hasBannerのテスト")
        inner class HasBannerTest {
            @Test
            @DisplayName("バナー設定あり")
            fun isActiveBanner() {
                val sut = ExistingSpot(mock(), mock(), mock(), mock(), mock(), mock())

                assertTrue(sut.hasBanner())
            }

            @Test
            @DisplayName("バナー設定なし")
            fun isInactiveBanner() {
                val sut = ExistingSpot(mock(), mock(), null, mock(), mock(), mock())

                assertFalse(sut.hasBanner())
            }
        }

        @Nested
        @TestInstance(Lifecycle.PER_CLASS)
        @DisplayName("hasNativeのテスト")
        inner class HasNativeTest {
            val standard: SpotNativeDisplay = mock()
            val video: SpotNativeVideoDisplay = mock()

            @ParameterizedTest
            @MethodSource("activeNativeParams")
            @DisplayName("ネイティブ設定あり")
            fun isActiveNaive(standard: SpotNativeDisplay?, video: SpotNativeVideoDisplay?) {
                val sut = ExistingSpot(mock(), mock(), mock(), standard, video, mock())

                assertTrue(sut.hasNative())
            }

            private fun activeNativeParams() = listOf(
                Arguments.of(standard, video),
                Arguments.of(null, video),
                Arguments.of(standard, null),
            )

            @Test
            @DisplayName("ネイティブ設定なし")
            fun isInactiveNative() {
                val sut = ExistingSpot(mock(), mock(), mock(), null, null, mock())

                assertFalse(sut.hasNative())
            }
        }

        @Nested
        @TestInstance(Lifecycle.PER_CLASS)
        @DisplayName("hasNativeStandardのテスト")
        inner class HasNativeStandardTest {
            @Test
            @DisplayName("設定あり")
            fun isActiveNaive() {
                val sut = ExistingSpot(mock(), mock(), mock(), mock(), mock(), mock())

                assertTrue(sut.hasNativeStandard())
            }

            @Test
            @DisplayName("設定なし")
            fun isInactiveNative() {
                val sut = ExistingSpot(mock(), mock(), mock(), null, mock(), mock())

                assertFalse(sut.hasNativeStandard())
            }
        }

        @Nested
        @TestInstance(Lifecycle.PER_CLASS)
        @DisplayName("hasNativeVideoのテスト")
        inner class HasNativeVideoTest {
            @Test
            @DisplayName("設定あり")
            fun isActiveNaive() {
                val sut = ExistingSpot(mock(), mock(), mock(), mock(), mock(), mock())

                assertTrue(sut.hasNativeVideo())
            }

            @Test
            @DisplayName("設定なし")
            fun isInactiveNative() {
                val sut = ExistingSpot(mock(), mock(), mock(), mock(), null, mock())

                assertFalse(sut.hasNativeVideo())
            }
        }

        @Nested
        @DisplayName("hasVideoのテスト")
        inner class HasVideoTest {
            @Test
            @DisplayName("ビデオ設定あり")
            fun isActiveVideo() {
                val sut = ExistingSpot(mock(), mock(), mock(), mock(), mock(), listOf(mock()))

                assertTrue(sut.hasVideo())
            }

            @Test
            @DisplayName("ビデオ設定なし")
            fun isInactiveVideo() {
                val sut = ExistingSpot(mock(), mock(), mock(), mock(), mock(), listOf())

                assertFalse(sut.hasVideo())
            }
        }

        @Nested
        @DisplayName("hasOtherThanBannerのテスト")
        inner class HasOtherThanBannerTest {
            val sut = spy(ExistingSpot(mock(), mock(), mock(), mock(), mock(), mock()))

            @ParameterizedTest
            @CsvSource(value = ["true,true", "true,false", "false,true"])
            @DisplayName("設定あり")
            fun isExist(hasNative: Boolean, hasVideo: Boolean) {
                doReturn(hasNative).whenever(sut).hasNative()
                doReturn(hasVideo).whenever(sut).hasVideo()

                assertTrue(sut.hasOtherThanBanner())
            }

            @Test
            @DisplayName("設定なし")
            fun isNotExist() {
                doReturn(false).whenever(sut).hasNative()
                doReturn(false).whenever(sut).hasVideo()

                assertFalse(sut.hasOtherThanBanner())
            }
        }

        @Nested
        @DisplayName("hasOtherThanNativeのテスト")
        inner class HasOtherThanNativeTest {
            val sut = spy(ExistingSpot(mock(), mock(), mock(), mock(), mock(), mock()))

            @ParameterizedTest
            @CsvSource(value = ["true,true", "true,false", "false,true"])
            @DisplayName("設定あり")
            fun isExist(hasBanner: Boolean, hasVideo: Boolean) {
                doReturn(hasBanner).whenever(sut).hasBanner()
                doReturn(hasVideo).whenever(sut).hasVideo()

                assertTrue(sut.hasOtherThanNative())
            }

            @Test
            @DisplayName("設定なし")
            fun isNotExist() {
                doReturn(false).whenever(sut).hasBanner()
                doReturn(false).whenever(sut).hasVideo()

                assertFalse(sut.hasOtherThanNative())
            }
        }

        @Nested
        @TestInstance(Lifecycle.PER_CLASS)
        @DisplayName("hasOtherThanVideoのテスト")
        inner class HasOtherThanVideoTest {
            val sut = spy(ExistingSpot(mock(), mock(), mock(), mock(), mock(), mock()))

            @ParameterizedTest
            @CsvSource(value = ["true,true", "true,false", "false,true"])
            @DisplayName("設定あり")
            fun isExist(hasBanner: Boolean, hasNative: Boolean) {
                doReturn(hasBanner).whenever(sut).hasBanner()
                doReturn(hasNative).whenever(sut).hasNative()

                assertTrue(sut.hasOtherThanVideo())
            }

            @Test
            @DisplayName("設定なし")
            fun isNotExist() {
                doReturn(false).whenever(sut).hasBanner()
                doReturn(false).whenever(sut).hasNative()

                assertFalse(sut.hasOtherThanVideo())
            }
        }
    }

    fun mockRelaySpotSizeType(sizeTypeId: SizeTypeId): RelaySpotSizetype = mock {
        on { this.sizeTypeId } doReturn sizeTypeId
    }

    fun mockSizeTypeInfo(sizeTypeId: SizeTypeId): SizeTypeInfo = mock {
        on { this.sizeTypeId } doReturn sizeTypeId
    }
}
