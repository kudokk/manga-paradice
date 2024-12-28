package jp.mangaka.ssp.application.service.spot.helper

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.clearInvocations
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfoInsert
import jp.mangaka.ssp.presentation.controller.spot.form.SizeTypeForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.BannerSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@DisplayName("SizeTypeInfoPersistHelperのテスト")
private class SizeTypeInfoPersistHelperTest {
    companion object {
        val coAccountId = CoAccountId(1)
    }

    val sizeTypeInfoDao: SizeTypeInfoDao = mock()

    val sut = spy(SizeTypeInfoPersistHelper(sizeTypeInfoDao))

    @AfterEach
    fun after() {
        clearInvocations(sizeTypeInfoDao, sut)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("bulkCreateのテスト")
    inner class BulkCreateTest {
        val bannerSizeTypes = listOf(
            SizeTypeForm(10, 20), SizeTypeForm(11, 21), SizeTypeForm(12, 22), SizeTypeForm(13, 23), SizeTypeForm(14, 24)
        )

        @Test
        @DisplayName("既存のみ")
        fun isOnlyExistingAndNoNative() {
            mapOf(
                sizeToId(10, 20, 1),
                sizeToId(11, 21, 2),
                sizeToId(12, 22, 3),
                sizeToId(13, 23, 4),
                sizeToId(14, 24, 5),
                sizeToId(0, 0, 99)
            ).let { doReturn(it).whenever(sut).getExistingSizeTypeInfoMap(any()) }
            doReturn(emptyMap<Pair<Int, Int>, SizeTypeId>()).whenever(sut).bulkCreateNewSizeTypeInfos(any(), any())

            val actual = sut.bulkCreate(coAccountId, PlatformId.pc, spotForm(bannerForm(bannerSizeTypes), mock()))

            assertEquals(listOf(1, 2, 3, 4, 5, 99), actual.map { it.value })
            verify(sut, times(1)).getExistingSizeTypeInfoMap(PlatformId.pc)
            verify(sut, times(1)).bulkCreateNewSizeTypeInfos(PlatformId.pc, emptyList())
        }

        @Test
        @DisplayName("新規のみ")
        fun isOnlyNew() {
            doReturn(emptyMap<Pair<Int, Int>, SizeTypeId>()).whenever(sut).getExistingSizeTypeInfoMap(any())
            mapOf(
                sizeToId(10, 20, 1), sizeToId(11, 21, 2), sizeToId(12, 22, 3), sizeToId(13, 23, 4), sizeToId(14, 24, 5)
            ).let { doReturn(it).whenever(sut).bulkCreateNewSizeTypeInfos(any(), any()) }

            val actual = sut.bulkCreate(coAccountId, PlatformId.pc, spotForm(bannerForm(bannerSizeTypes), null))

            assertEquals(listOf(1, 2, 3, 4, 5), actual.map { it.value })
            verify(sut, times(1)).getExistingSizeTypeInfoMap(PlatformId.pc)
            verify(sut, times(1)).bulkCreateNewSizeTypeInfos(PlatformId.pc, bannerSizeTypes)
        }

        @Test
        @DisplayName("新規・既存混合")
        fun isNewAndExisting() {
            doReturn(mapOf(sizeToId(10, 20, 1), sizeToId(12, 22, 2), sizeToId(14, 24, 3), sizeToId(0, 0, 199)))
                .whenever(sut).getExistingSizeTypeInfoMap(any())
            doReturn(mapOf(sizeToId(11, 21, 4), sizeToId(13, 23, 5)))
                .whenever(sut).bulkCreateNewSizeTypeInfos(any(), any())

            val actual = sut.bulkCreate(
                coAccountId,
                PlatformId.smartPhone,
                spotForm(bannerForm(bannerSizeTypes), mock())
            )

            assertEquals(listOf(1, 4, 2, 5, 3, 199), actual.map { it.value })
            verify(sut, times(1)).getExistingSizeTypeInfoMap(PlatformId.smartPhone)
            verify(sut, times(1)).bulkCreateNewSizeTypeInfos(
                PlatformId.smartPhone,
                listOf(bannerSizeTypes[1], bannerSizeTypes[3])
            )
        }

        @ParameterizedTest
        @MethodSource("emptyParams")
        @DisplayName("登録データなし")
        fun isEmpty(bannerForm: BannerSettingForm?) {
            val actual = sut.bulkCreate(coAccountId, PlatformId.pc, spotForm(bannerForm, null))

            assertTrue(actual.isEmpty())
            verify(sut, never()).getExistingSizeTypeInfoMap(any())
            verify(sut, never()).bulkCreateNewSizeTypeInfos(any(), any())
        }

        private fun emptyParams() = listOf(
            null, bannerForm(emptyList())
        )

        private fun spotForm(
            bannerForm: BannerSettingForm?,
            nativeForm: NativeSettingForm?
        ) = SpotCreateForm(mock(), mock(), bannerForm, nativeForm, mock())

        private fun bannerForm(sizeTypes: List<SizeTypeForm>): BannerSettingForm = mock {
            on { this.sizeTypes } doReturn sizeTypes
        }
    }

    @Nested
    @DisplayName("getExistingSizeTypeInfoMapのテスト")
    inner class GetExistingSizeTypeInfoMapTest {
        @Test
        @DisplayName("対象データあり")
        fun isNotEmpty() {
            listOf(sizeTypeInfo(1, 10, 20), sizeTypeInfo(2, 11, 21), sizeTypeInfo(3, 12, 22)).let {
                doReturn(it).whenever(sizeTypeInfoDao).selectByPlatformId(any())
            }

            val actual = sut.getExistingSizeTypeInfoMap(PlatformId.pc)

            assertEquals(mapOf(sizeToId(10, 20, 1), sizeToId(11, 21, 2), sizeToId(12, 22, 3)), actual)
            verify(sizeTypeInfoDao, times(1)).selectByPlatformId(PlatformId.pc)
        }

        @Test
        @DisplayName("対象データなし")
        fun isEmpty() {
            doReturn(emptyList<SizeTypeInfo>()).whenever(sizeTypeInfoDao).selectByPlatformId(any())

            val actual = sut.getExistingSizeTypeInfoMap(PlatformId.pc)

            assertTrue(actual.isEmpty())
            verify(sizeTypeInfoDao, times(1)).selectByPlatformId(PlatformId.pc)
        }
    }

    @Nested
    @DisplayName("bulkCreateNewSizeTypeInfosのテスト")
    inner class BulkCreateNewSizeTypeInfosTest {
        @Test
        @DisplayName("登録データあり")
        fun isNotEmpty() {
            doReturn(listOf(1, 2, 3).map { SizeTypeId(it) }).whenever(sizeTypeInfoDao).bulkInsert(any())

            val actual = sut.bulkCreateNewSizeTypeInfos(
                PlatformId.pc, listOf(SizeTypeForm(10, 20), SizeTypeForm(11, 21), SizeTypeForm(12, 22))
            )

            assertEquals(mapOf(sizeToId(10, 20, 1), sizeToId(11, 21, 2), sizeToId(12, 22, 3)), actual)
            verify(sizeTypeInfoDao, times(1)).bulkInsert(
                listOf(sizeTypeInfoInsert(10, 20, 1), sizeTypeInfoInsert(11, 21, 1), sizeTypeInfoInsert(12, 22, 1))
            )
        }

        @Test
        @DisplayName("登録データなし")
        fun isEmpty() {
            val actual = sut.bulkCreateNewSizeTypeInfos(PlatformId.pc, emptyList())

            assertTrue(actual.isEmpty())
            verify(sizeTypeInfoDao, never()).bulkInsert(any())
        }
    }

    private fun sizeTypeInfo(sizeTypeId: Int, width: Int, height: Int): SizeTypeInfo = mock {
        on { this.sizeTypeId } doReturn SizeTypeId(sizeTypeId)
        on { this.width } doReturn width
        on { this.height } doReturn height
    }

    private fun sizeTypeInfoInsert(width: Int, height: Int, platformId: Int) =
        SizeTypeInfoInsert(width, height, PlatformId(platformId))

    private fun sizeToId(width: Int, height: Int, sizeTypeId: Int) = (width to height) to SizeTypeId(sizeTypeId)
}
