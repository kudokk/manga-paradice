package jp.mangaka.ssp.application.service.spot.helper

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.clearInvocations
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeElementId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio.AspectRatioStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatioDao
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster.CurrencyMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster.CurrencyMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.decoration.Decoration
import jp.mangaka.ssp.infrastructure.datasource.dao.decoration.DecorationDao
import jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster.DspMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster.DspMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate.NativeTemplateStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplateDao
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElement
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElementDao
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.site.SiteDao
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.SpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.EnumSource.Mode
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("SpotGetWithCheckHelperのテスト")
private class SpotGetWithCheckHelperTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val currencyId = CurrencyId(1)
        val decorationId = DecorationId(1)
        val siteId = SiteId(1)
        val spotId = SpotId(1)
        val nativeTemplateId = NativeTemplateId(1)
        val platformId = PlatformId(1)
    }

    val aspectRatioDao: AspectRatioDao = mock()
    val countryMasterDao: CountryMasterDao = mock()
    val currencyMasterDao: CurrencyMasterDao = mock()
    val decorationDao: DecorationDao = mock()
    val dspMasterDao: DspMasterDao = mock()
    val nativeTemplateDao: NativeTemplateDao = mock()
    val nativeTemplateElementDao: NativeTemplateElementDao = mock()
    val siteDao: SiteDao = mock()
    val sizeTypeInfoDao: SizeTypeInfoDao = mock()
    val spotDao: SpotDao = mock()

    val sut = spy(
        SpotGetWithCheckHelper(
            aspectRatioDao, countryMasterDao, currencyMasterDao, decorationDao, dspMasterDao, nativeTemplateDao,
            nativeTemplateElementDao, siteDao, sizeTypeInfoDao, spotDao
        )
    )

    @AfterEach
    fun afterEach() {
        clearInvocations(
            sut, aspectRatioDao, currencyMasterDao, decorationDao, dspMasterDao, nativeTemplateDao,
            nativeTemplateElementDao, siteDao, spotDao
        )
    }

    @Nested
    @DisplayName("getCurrencyWithCheckのテスト")
    inner class GetCurrencyWithCheckTest {
        val currency: CurrencyMaster = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(currency).whenever(currencyMasterDao).selectById(any())

            assertEquals(currency, sut.getCurrencyWithCheck(currencyId))

            verify(currencyMasterDao, times(1)).selectById(currencyId)
        }

        @Test
        @DisplayName("DBに存在しない")
        fun isNotExistingInDb() {
            doReturn(null).whenever(currencyMasterDao).selectById(any())

            assertThrows<CompassManagerException> { sut.getCurrencyWithCheck(currencyId) }

            verify(currencyMasterDao, times(1)).selectById(currencyId)
        }
    }

    @Nested
    @DisplayName("getDecorationWithCheckのテスト")
    inner class GetDecorationWithCheckTest {
        val decoration: Decoration = mock()

        @ParameterizedTest
        @ValueSource(ints = [0, 1])
        @DisplayName("正常")
        fun isCorrect(coAccountIdInt: Int) {
            doReturn(CoAccountId(coAccountIdInt)).whenever(decoration).coAccountId
            doReturn(decoration).whenever(decorationDao).selectById(any())

            assertEquals(decoration, sut.getDecorationWithCheck(coAccountId, decorationId))

            verify(decorationDao, times(1)).selectById(decorationId)
        }

        @Test
        @DisplayName("DBに存在しない")
        fun isNotExistingInDb() {
            doReturn(null).whenever(decorationDao).selectById(any())

            assertThrows<CompassManagerException> { sut.getDecorationWithCheck(coAccountId, decorationId) }

            verify(decorationDao, times(1)).selectById(decorationId)
        }

        @Test
        @DisplayName("CoアカウントIDが一致しない")
        fun isInvalidCoAccountId() {
            doReturn(CoAccountId(99)).whenever(decoration).coAccountId
            doReturn(decoration).whenever(decorationDao).selectById(any())

            assertThrows<CompassManagerException> { sut.getDecorationWithCheck(coAccountId, decorationId) }

            verify(decorationDao, times(1)).selectById(decorationId)
        }
    }

    @Nested
    @DisplayName("getSiteWithCheckのテスト")
    inner class GetSiteWithCheckTest {
        val statuses = listOf(SiteStatus.active, SiteStatus.requested)
        val site: Site = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(coAccountId).whenever(site).coAccountId
            doReturn(site).whenever(siteDao).selectByIdAndStatuses(siteId, statuses)

            assertEquals(site, sut.getSiteWithCheck(coAccountId, siteId, statuses))

            verify(siteDao, times(1)).selectByIdAndStatuses(siteId, statuses)
        }

        @Test
        @DisplayName("DBに存在しない")
        fun isNotExistingInDb() {
            doReturn(null).whenever(siteDao).selectByIdAndStatuses(any(), any())

            assertThrows<CompassManagerException> { sut.getSiteWithCheck(coAccountId, siteId, statuses) }

            verify(siteDao, times(1)).selectByIdAndStatuses(siteId, statuses)
        }

        @Test
        @DisplayName("CoアカウントIDが一致しない")
        fun isInvalidCoAccountId() {
            doReturn(CoAccountId(99)).whenever(site).coAccountId
            doReturn(site).whenever(siteDao).selectByIdAndStatuses(any(), any())

            assertThrows<CompassManagerException> { sut.getSiteWithCheck(coAccountId, siteId, statuses) }

            verify(siteDao, times(1)).selectByIdAndStatuses(siteId, statuses)
        }
    }

    @Nested
    @DisplayName("getDspsWithCheckのテスト")
    inner class GetDspsWithCheckTest {
        val dspIds = listOf(1, 2, 3, 4).map { DspId(it) }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val dspMasters = dspMasters(dspIds)
            doReturn(dspMasters).whenever(dspMasterDao).selectByIds(any())

            val actual = sut.getDspsWithCheck(dspIds)

            assertEquals(dspMasters, actual)
            verify(dspMasterDao, times(1)).selectByIds(dspIds)
        }

        @Test
        @DisplayName("存在しないDSPがある")
        fun isNotExist() {
            val dspMasters = dspMasters(dspIds.subList(0, 2))
            doReturn(dspMasters).whenever(dspMasterDao).selectByIds(any())

            assertThrows<CompassManagerException> { sut.getDspsWithCheck(dspIds) }

            verify(dspMasterDao, times(1)).selectByIds(dspIds)
        }

        private fun dspMasters(dspIds: Collection<DspId>): List<DspMaster> = dspIds.map { dspId ->
            mock {
                on { this.dspId } doReturn dspId
            }
        }
    }

    @Nested
    @DisplayName("getSpotWithCheck(spotId, spotStatuses)のテスト")
    inner class GetSpotWithCheckBySpotIdSpotStatusesTest {
        val spot: Spot = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(spot).whenever(spotDao).selectByIdAndStatus(spotId, SpotStatus.entries)

            assertEquals(spot, sut.getSpotWithCheck(spotId, SpotStatus.entries))

            verify(spotDao, times(1)).selectByIdAndStatus(spotId, SpotStatus.entries)
        }

        @Test
        @DisplayName("DBに存在しない")
        fun isNotExistingInDb() {
            doReturn(null).whenever(siteDao).selectByIdAndStatuses(any(), any())

            assertThrows<CompassManagerException> { sut.getSpotWithCheck(spotId, SpotStatus.entries) }

            verify(spotDao, times(1)).selectByIdAndStatus(spotId, SpotStatus.entries)
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpotWithCheck(spotId, spotStatuses, userType)のテスト")
    inner class GetSpotWithCheckBySpotIdSpotStatusesUserTypeTest {
        val spot: Spot = mock()
        val spotStatuses: List<SpotStatus> = mock()

        @BeforeEach
        fun beforeEach() {
            doReturn(spot).whenever(sut).getSpotWithCheck(any(), any())
        }

        @ParameterizedTest
        @EnumSource(value = UserType::class, mode = Mode.EXCLUDE, names = ["ma_staff"])
        @DisplayName("社員以外でヘッダービディング：prebidjsの広告枠のとき")
        fun isNotMaStaffAndPrebidJs(userType: UserType) {
            doReturn(UpstreamType.prebidjs).whenever(spot).upstreamType

            assertThrows<CompassManagerException> { sut.getSpotWithCheck(spotId, spotStatuses, userType) }

            verify(sut, times(1)).getSpotWithCheck(spotId, spotStatuses)
        }

        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("正常")
        fun isCorrect(userType: UserType, upstreamType: UpstreamType) {
            doReturn(upstreamType).whenever(spot).upstreamType

            val actual = sut.getSpotWithCheck(spotId, spotStatuses, userType)

            assertEquals(spot, actual)
            verify(sut, times(1)).getSpotWithCheck(spotId, spotStatuses)
        }

        private fun correctParams() = UserType.entries.map { Arguments.of(it, UpstreamType.none) } +
            Arguments.of(UserType.ma_staff, UpstreamType.prebidjs)
    }

    @Nested
    @DisplayName("getNativeTemplateWithCheckのテスト")
    inner class GetNativeTemplateWithCheckTest {
        val nativeTemplateId = NativeTemplateId(1)
        val nativeTemplate: NativeTemplate = mock()

        @Test
        @DisplayName("正常 - 共通定義テンプレート")
        fun isCorrectAndCommon() {
            doReturn(null).whenever(nativeTemplate).coAccountId
            doReturn(nativeTemplate).whenever(nativeTemplateDao).selectByIdAndStatues(any(), any())

            val actual = sut.getNativeTemplateWithCheck(coAccountId, nativeTemplateId, NativeTemplateStatus.entries)

            assertEquals(nativeTemplate, actual)

            verify(nativeTemplateDao, times(1)).selectByIdAndStatues(nativeTemplateId, NativeTemplateStatus.entries)
        }

        @Test
        @DisplayName("正常 - ユーザー定義テンプレート")
        fun isCorrectAndPersonal() {
            doReturn(coAccountId).whenever(nativeTemplate).coAccountId
            doReturn(nativeTemplate).whenever(nativeTemplateDao).selectByIdAndStatues(any(), any())

            val actual = sut.getNativeTemplateWithCheck(coAccountId, nativeTemplateId, NativeTemplateStatus.entries)

            assertEquals(nativeTemplate, actual)

            verify(nativeTemplateDao, times(1)).selectByIdAndStatues(nativeTemplateId, NativeTemplateStatus.entries)
        }

        @Test
        @DisplayName("DBに存在しない")
        fun isNotExistingInDb() {
            doReturn(null).whenever(nativeTemplateDao).selectByIdAndStatues(any(), any())

            assertThrows<CompassManagerException> {
                sut.getNativeTemplateWithCheck(coAccountId, nativeTemplateId, NativeTemplateStatus.entries)
            }

            verify(nativeTemplateDao, times(1)).selectByIdAndStatues(nativeTemplateId, NativeTemplateStatus.entries)
        }

        @Test
        @DisplayName("CoアカウントIDが一致しない")
        fun isInvalidCoAccountId() {
            doReturn(CoAccountId(99)).whenever(nativeTemplate).coAccountId
            doReturn(nativeTemplate).whenever(nativeTemplateDao).selectByIdAndStatues(any(), any())

            assertThrows<CompassManagerException> {
                sut.getNativeTemplateWithCheck(coAccountId, nativeTemplateId, NativeTemplateStatus.entries)
            }

            verify(nativeTemplateDao, times(1)).selectByIdAndStatues(nativeTemplateId, NativeTemplateStatus.entries)
        }
    }

    @Nested
    @DisplayName("getNativeStandardTemplateWithCheckのテスト")
    inner class GetNativeStandardTemplateWithCheckTest {
        val nativeTemplate: NativeTemplate = mock()

        @Test
        @DisplayName("動画要素を含むネイティブテンプレート")
        fun isContainsVideoElement() {
            doReturn(nativeTemplate to nativeTemplateElements(1, 2, 3, NativeElementId.video.value))
                .whenever(sut).getNativeTemplateAndElements(anyOrNull(), any(), any(), any())

            assertThrows<CompassManagerException> {
                sut.getNativeStandardTemplateWithCheck(
                    coAccountId, nativeTemplateId, listOf(NativeTemplateStatus.active), platformId
                )
            }

            verify(sut, times(1)).getNativeTemplateAndElements(
                coAccountId, nativeTemplateId, listOf(NativeTemplateStatus.active), platformId
            )
        }

        @Test
        @DisplayName("動画要素を含まないネイティブテンプレート")
        fun isNotContainsVideoElement() {
            doReturn(nativeTemplate to nativeTemplateElements(1, 2, 3))
                .whenever(sut).getNativeTemplateAndElements(anyOrNull(), any(), any(), any())

            val actual = sut.getNativeStandardTemplateWithCheck(
                coAccountId, nativeTemplateId, listOf(NativeTemplateStatus.active), platformId
            )

            assertEquals(nativeTemplate, actual)

            verify(sut, times(1)).getNativeTemplateAndElements(
                coAccountId, nativeTemplateId, listOf(NativeTemplateStatus.active), platformId
            )
        }
    }

    @Nested
    @DisplayName("getNativeVideoTemplateWithCheckのテスト")
    inner class GetNativeVideoTemplateWithCheckTest {
        val nativeTemplate: NativeTemplate = mock()

        @Test
        @DisplayName("動画要素を含まないネイティブテンプレート")
        fun isNotContainsVideoElement() {
            doReturn(nativeTemplate to nativeTemplateElements(1, 2, 3))
                .whenever(sut).getNativeTemplateAndElements(anyOrNull(), any(), any(), anyOrNull())

            assertThrows<CompassManagerException> {
                sut.getNativeVideoTemplateWithCheck(
                    nativeTemplateId, listOf(NativeTemplateStatus.active)
                )
            }

            verify(sut, times(1)).getNativeTemplateAndElements(
                null, nativeTemplateId, listOf(NativeTemplateStatus.active)
            )
        }

        @Test
        @DisplayName("動画要素を含むネイティブテンプレート")
        fun isContainsVideoElement() {
            doReturn(nativeTemplate to nativeTemplateElements(1, 2, 3, NativeElementId.video.value))
                .whenever(sut).getNativeTemplateAndElements(anyOrNull(), any(), any(), anyOrNull())

            val actual = sut.getNativeVideoTemplateWithCheck(
                nativeTemplateId, listOf(NativeTemplateStatus.active)
            )

            assertEquals(nativeTemplate, actual)

            verify(sut, times(1)).getNativeTemplateAndElements(
                null, nativeTemplateId, listOf(NativeTemplateStatus.active)
            )
        }
    }

    @Nested
    @DisplayName("getNativeTemplateAndElementsのテスト")
    inner class GetNativeTemplateAndElementsTest {
        val nativeTemplate: NativeTemplate = mock()
        val nativeTemplateElements: List<NativeTemplateElement> = mock()
        val statuses = listOf(NativeTemplateStatus.active)

        @Test
        @DisplayName("正常 - ユーザー定義テンプレート")
        fun isCorrectAndUserDefined() {
            doReturn(coAccountId).whenever(nativeTemplate).coAccountId
            doReturn(platformId).whenever(nativeTemplate).platformId
            doReturn(nativeTemplate).whenever(nativeTemplateDao).selectByIdAndStatues(any(), any())
            doReturn(nativeTemplateElements).whenever(nativeTemplateElementDao).selectByNativeTemplateIds(any())

            val actual = sut.getNativeTemplateAndElements(coAccountId, nativeTemplateId, statuses, platformId)

            assertEquals(nativeTemplate to nativeTemplateElements, actual)

            verify(nativeTemplateDao, times(1)).selectByIdAndStatues(nativeTemplateId, statuses)
            verify(nativeTemplateElementDao, times(1)).selectByNativeTemplateIds(listOf(nativeTemplateId))
        }

        @Test
        @DisplayName("正常 - 共通定義テンプレート（プラットフォーム指定なし）")
        fun isCorrectAndCommonDefined() {
            doReturn(null).whenever(nativeTemplate).coAccountId
            doReturn(platformId).whenever(nativeTemplate).platformId
            doReturn(nativeTemplate).whenever(nativeTemplateDao).selectByIdAndStatues(any(), any())
            doReturn(nativeTemplateElements).whenever(nativeTemplateElementDao).selectByNativeTemplateIds(any())

            val actual = sut.getNativeTemplateAndElements(null, nativeTemplateId, statuses)

            assertEquals(nativeTemplate to nativeTemplateElements, actual)

            verify(nativeTemplateDao, times(1)).selectByIdAndStatues(nativeTemplateId, statuses)
            verify(nativeTemplateElementDao, times(1)).selectByNativeTemplateIds(listOf(nativeTemplateId))
        }

        @Test
        @DisplayName("DBに存在しない")
        fun isNotFound() {
            doReturn(null).whenever(nativeTemplateDao).selectByIdAndStatues(any(), any())

            assertThrows<CompassManagerException> {
                sut.getNativeTemplateAndElements(coAccountId, nativeTemplateId, statuses, platformId)
            }

            verify(nativeTemplateDao, times(1)).selectByIdAndStatues(nativeTemplateId, statuses)
            verify(nativeTemplateElementDao, never()).selectByNativeTemplateIds(any())
        }

        @Test
        @DisplayName("CoアカウントIDが不一致")
        fun isInvalidCoAccountId() {
            doReturn(CoAccountId(99)).whenever(nativeTemplate).coAccountId
            doReturn(platformId).whenever(nativeTemplate).platformId
            doReturn(nativeTemplate).whenever(nativeTemplateDao).selectByIdAndStatues(any(), any())

            assertThrows<CompassManagerException> {
                sut.getNativeTemplateAndElements(null, nativeTemplateId, statuses, platformId)
            }

            verify(nativeTemplateDao, times(1)).selectByIdAndStatues(nativeTemplateId, statuses)
            verify(nativeTemplateElementDao, never()).selectByNativeTemplateIds(any())
        }

        @Test
        @DisplayName("プラットフォームIDが不一致")
        fun isInvalidPlatformId() {
            doReturn(coAccountId).whenever(nativeTemplate).coAccountId
            doReturn(PlatformId(99)).whenever(nativeTemplate).platformId
            doReturn(nativeTemplate).whenever(nativeTemplateDao).selectByIdAndStatues(any(), any())

            assertThrows<CompassManagerException> {
                sut.getNativeTemplateAndElements(null, nativeTemplateId, statuses, platformId)
            }

            verify(nativeTemplateDao, times(1)).selectByIdAndStatues(nativeTemplateId, statuses)
            verify(nativeTemplateElementDao, never()).selectByNativeTemplateIds(any())
        }
    }

    private fun nativeTemplateElements(
        vararg nativeElementIds: Int
    ): List<NativeTemplateElement> = nativeElementIds.map { nativeElementId ->
        mock {
            on { this.nativeElementId } doReturn NativeElementId(nativeElementId)
        }
    }

    @Nested
    @DisplayName("getAspectRatiosWithCheckのテスト")
    inner class GetAspectRatiosWithCheckTest {
        // 重複除去されることを確認するためのIDも含む
        val aspectRatioIds = listOf(1, 2, 3, 1, 4, 2).map { AspectRatioId(it) }
        val aspectRatios = aspectRatioIds.distinct().map { mockAspectRatio(it) }
        val statuses: List<AspectRatioStatus> = listOf(AspectRatioStatus.active)

        @Test
        @DisplayName("正常")
        fun isCorrectAndNotEmpty() {
            doReturn(aspectRatios).whenever(aspectRatioDao).selectByAspectRatioIdsAndStatuses(any(), any())

            val actual = sut.getAspectRatiosWithCheck(aspectRatioIds, statuses)

            assertEquals(aspectRatios, actual)
            verify(aspectRatioDao, times(1)).selectByAspectRatioIdsAndStatuses(aspectRatioIds, statuses)
        }

        @Test
        @DisplayName("取得できないアスペクト比あり")
        fun isNotFound() {
            val hasNotFoundIds = aspectRatioIds + AspectRatioId(99)
            doReturn(aspectRatios).whenever(aspectRatioDao).selectByAspectRatioIdsAndStatuses(any(), any())

            assertThrows<CompassManagerException> { sut.getAspectRatiosWithCheck(hasNotFoundIds, statuses) }
            verify(aspectRatioDao, times(1)).selectByAspectRatioIdsAndStatuses(hasNotFoundIds, statuses)
        }

        @Test
        @DisplayName("引数・戻り値が空")
        fun isEmpty() {
            doReturn(emptyList<AspectRatio>())
                .whenever(aspectRatioDao)
                .selectByAspectRatioIdsAndStatuses(any(), any())

            val actual = sut.getAspectRatiosWithCheck(emptyList(), emptyList())

            assertTrue(actual.isEmpty())
            verify(aspectRatioDao, times(1)).selectByAspectRatioIdsAndStatuses(emptyList(), emptyList())
        }

        private fun mockAspectRatio(aspectRatioId: AspectRatioId): AspectRatio = mock {
            on { this.aspectRatioId } doReturn aspectRatioId
        }
    }

    @Nested
    @DisplayName("getCountiesWithCheckのテスト")
    inner class GetCountiesWithCheckTest {
        // 重複除去されることを確認するためのIDも含む
        val countryIds = listOf(1, 2, 3, 1, 4, 2).map { CountryId(it) }
        val countries = countryIds.distinct().map { mockCountryMaster(it) }

        @Test
        @DisplayName("正常")
        fun isCorrectAndNotEmpty() {
            doReturn(countries).whenever(countryMasterDao).selectByIds(countryIds)

            val actual = sut.getCountiesWithCheck(countryIds)

            assertEquals(countries, actual)
            verify(countryMasterDao, times(1)).selectByIds(countryIds)
        }

        @Test
        @DisplayName("取得できない国あり")
        fun isNotFound() {
            val hasNotFoundIds = countryIds + CountryId(99)
            doReturn(countries).whenever(countryMasterDao).selectByIds(any())

            assertThrows<CompassManagerException> { sut.getCountiesWithCheck(hasNotFoundIds) }
            verify(countryMasterDao, times(1)).selectByIds(hasNotFoundIds)
        }

        @Test
        @DisplayName("引数・戻り値が空")
        fun isEmpty() {
            doReturn(emptyList<CountryMaster>()).whenever(countryMasterDao).selectByIds(any())

            val actual = sut.getCountiesWithCheck(emptyList())

            assertTrue(actual.isEmpty())
            verify(countryMasterDao, times(1)).selectByIds(emptyList())
        }

        private fun mockCountryMaster(countryId: CountryId): CountryMaster = mock {
            on { this.countryId } doReturn countryId
        }
    }

    @Nested
    @DisplayName("getSizeTypeInfosWithCheckのテスト")
    inner class GetSizeTypeInfosWithCheckTest {
        // 重複除去されることを確認するためのIDも含む
        val sizeTypeIds = listOf(1, 2, 3, 1, 4, 2).map { SizeTypeId(it) }
        val sizeTypeInfos = sizeTypeIds.distinct().map { mockSizeTypeInfo(it) }

        @Test
        @DisplayName("正常")
        fun isCorrectAndNotEmpty() {
            doReturn(sizeTypeInfos).whenever(sizeTypeInfoDao).selectByIds(sizeTypeIds)

            val actual = sut.getSizeTypeInfosWithCheck(sizeTypeIds)

            assertEquals(sizeTypeInfos, actual)
            verify(sizeTypeInfoDao, times(1)).selectByIds(sizeTypeIds)
        }

        @Test
        @DisplayName("取得できないサイズ種別あり")
        fun isNotFound() {
            val hasNotFoundIds = sizeTypeIds + SizeTypeId(99)
            doReturn(sizeTypeInfos).whenever(sizeTypeInfoDao).selectByIds(any())

            assertThrows<CompassManagerException> { sut.getSizeTypeInfosWithCheck(hasNotFoundIds) }
            verify(sizeTypeInfoDao, times(1)).selectByIds(hasNotFoundIds)
        }

        @Test
        @DisplayName("引数・戻り値が空")
        fun isEmpty() {
            doReturn(emptyList<SizeTypeInfo>()).whenever(sizeTypeInfoDao).selectByIds(any())

            val actual = sut.getSizeTypeInfosWithCheck(emptyList())

            assertTrue(actual.isEmpty())
            verify(sizeTypeInfoDao, times(1)).selectByIds(emptyList())
        }

        private fun mockSizeTypeInfo(sizeTypeId: SizeTypeId): SizeTypeInfo = mock {
            on { this.sizeTypeId } doReturn sizeTypeId
        }
    }
}
