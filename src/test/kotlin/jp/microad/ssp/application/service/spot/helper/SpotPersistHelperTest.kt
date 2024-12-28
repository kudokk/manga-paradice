package jp.mangaka.ssp.application.service.spot.helper

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
import jp.mangaka.ssp.application.service.coaccount.CoAccountGetWithCheckHelper
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio
import jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster.CoAccountMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp.RelayDefaultCoAccountDsp
import jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp.RelayDefaultCoAccountDspDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp.RelaySpotDsp
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp.RelaySpotDspDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp.RelaySpotDspInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetypeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetypeInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.SpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.SpotInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.SpotUpdate
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBannerDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBannerInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplayInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNativeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNativeInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNativeUpdate
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplayInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplayInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotupstreamcurrency.SpotUpstreamCurrencyDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotupstreamcurrency.SpotUpstreamCurrencyInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideoInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideoUpdate
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplayInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpm
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpmDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpmInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpmUpdate
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.BannerSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.DspForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeStandardForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeVideoForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotMaxSizeForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import io.mockk.verify as verifyK

@DisplayName("SpotPersistHelperのテスト")
private class SpotPersistHelperTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val spotId = SpotId(1)
        val currencyId1 = CurrencyId(1)
        val currencyId2 = CurrencyId(2)
        val dspId1 = DspId(1)
        val dspId2 = DspId(2)
        val dspId3 = DspId(3)
        val dspId4 = DspId(4)
        val dspId5 = DspId(5)
        val aspectRatioId1 = AspectRatioId(1)
        val aspectRatioId2 = AspectRatioId(2)
        val aspectRatioId3 = AspectRatioId(3)
        val aspectRatioId4 = AspectRatioId(4)
        val aspectRatioId5 = AspectRatioId(5)
        val aspectRatioId6 = AspectRatioId(6)
    }

    val relayDefaultCoAccountDspDao: RelayDefaultCoAccountDspDao = mock()
    val relaySpotDspDao: RelaySpotDspDao = mock()
    val relaySpotSizetypeDao: RelaySpotSizetypeDao = mock()
    val spotDao: SpotDao = mock()
    val spotBannerDao: SpotBannerDao = mock()
    val spotBannerDisplayDao: SpotBannerDisplayDao = mock()
    val spotNativeDao: SpotNativeDao = mock()
    val spotNativeDisplayDao: SpotNativeDisplayDao = mock()
    val spotNativeVideoDisplayDao: SpotNativeVideoDisplayDao = mock()
    val spotUpstreamCurrencyDao: SpotUpstreamCurrencyDao = mock()
    val spotVideoDao: SpotVideoDao = mock()
    val spotVideoDisplayDao: SpotVideoDisplayDao = mock()
    val spotVideoFloorCpmDao: SpotVideoFloorCpmDao = mock()
    val coAccountGetWithCheckHelper: CoAccountGetWithCheckHelper = mock()

    val sizeTypeIds = listOf(1, 2, 3).map { SizeTypeId(it) }

    val sut = spy(
        SpotPersistHelper(
            relayDefaultCoAccountDspDao, relaySpotDspDao, relaySpotSizetypeDao, spotDao, spotBannerDao,
            spotBannerDisplayDao, spotNativeDao, spotNativeDisplayDao, spotNativeVideoDisplayDao,
            spotUpstreamCurrencyDao, spotVideoDao, spotVideoDisplayDao, spotVideoFloorCpmDao,
            coAccountGetWithCheckHelper
        )
    )

    @BeforeEach
    fun before() {
        clearInvocations(
            relayDefaultCoAccountDspDao, relaySpotDspDao, relaySpotSizetypeDao, spotDao, spotBannerDao,
            spotBannerDisplayDao, spotUpstreamCurrencyDao, coAccountGetWithCheckHelper, sut
        )
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("createのテスト")
    inner class CreateTest {
        val form = SpotCreateForm(mock(), mock(), mock(), mock(), mock())
        val site: Site = mock()
        val aspectRatios: Collection<AspectRatio> = mock()
        val spotInsert: SpotInsert = mock()

        @BeforeAll
        fun before() {
            mockkObject(SpotInsert)
            every { SpotInsert.of(any(), any()) } returns spotInsert
        }

        @AfterAll
        fun after() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(spotId).whenever(spotDao).insert(any())
            doNothing().whenever(sut).insertDsps(any(), any(), any(), any())
            doNothing().whenever(sut).insertUpstreamCurrencyIfNeeded(any(), any(), any())
            doNothing().whenever(sut).insertBannerIfNeeded(any(), any())
            doNothing().whenever(sut).insertNativeIfNeeded(any(), any())
            doNothing().whenever(sut).insertVideoIfNeeded(any(), any(), any())

            sut.create(coAccountId, form, site, sizeTypeIds, UserType.ma_staff, aspectRatios)

            verifyK { SpotInsert.of(form, site) }
            verify(spotDao, times(1)).insert(spotInsert)
            verify(sut, times(1)).insertDsps(coAccountId, spotId, form.dsps, UserType.ma_staff)
            verify(sut, times(1)).insertUpstreamCurrencyIfNeeded(coAccountId, spotId, form.basic)
            verify(relaySpotSizetypeDao, times(1)).bulkInsert(
                listOf(
                    RelaySpotSizetypeInsert(spotId, sizeTypeIds[0]),
                    RelaySpotSizetypeInsert(spotId, sizeTypeIds[1]),
                    RelaySpotSizetypeInsert(spotId, sizeTypeIds[2])
                )
            )
            verify(sut, times(1)).insertBannerIfNeeded(spotId, form)
            verify(sut, times(1)).insertNativeIfNeeded(spotId, form)
            verify(sut, times(1)).insertVideoIfNeeded(spotId, form, aspectRatios)
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("insertDspsのテスト")
    inner class InsertDspsTest {
        val forms: List<DspForm> = mock()
        val defaultDsps: List<RelayDefaultCoAccountDsp> = mock()
        val inserts: List<RelaySpotDspInsert> = mock()

        @BeforeAll
        fun before() {
            mockkObject(RelaySpotDspInsert)
            every { RelaySpotDspInsert.of(any(), any()) } returns inserts
            every { RelaySpotDspInsert.of(any(), any(), any()) } returns inserts

            doReturn(defaultDsps).whenever(relayDefaultCoAccountDspDao).selectByCoAccountId(any())
        }

        @AfterAll
        fun after() {
            unmockkAll()
        }

        @Test
        @DisplayName("マイクロアド社員")
        fun isMaStaff() {
            sut.insertDsps(coAccountId, spotId, forms, UserType.ma_staff)

            verify(relayDefaultCoAccountDspDao, times(1)).selectByCoAccountId(coAccountId)
            verifyK { RelaySpotDspInsert.of(spotId, forms, defaultDsps) }
            verify(relaySpotDspDao, times(1)).bulkInsert(inserts)
        }

        @ParameterizedTest
        @EnumSource(value = UserType::class, names = ["ma_staff"], mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("マイクロアド社員以外")
        fun isNotMaStaff(userType: UserType) {
            sut.insertDsps(coAccountId, spotId, forms, userType)

            verify(relayDefaultCoAccountDspDao, times(1)).selectByCoAccountId(coAccountId)
            verifyK { RelaySpotDspInsert.of(spotId, defaultDsps) }
            verify(relaySpotDspDao, times(1)).bulkInsert(inserts)
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("insertUpstreamCurrencyIfNeededのテスト")
    inner class InsertUpstreamCurrencyIfNeededTest {
        val form: BasicSettingCreateForm = mock()
        val coAccount: CoAccountMaster = mock {
            on { this.currencyId } doReturn currencyId1
        }

        @BeforeEach
        fun before() {
            doReturn(coAccount).whenever(coAccountGetWithCheckHelper).getCoAccountWithCheck(any())
        }

        @Test
        @DisplayName("登録あり")
        fun isInsert() {
            doReturn(UpstreamType.prebidjs).whenever(form).upstreamType
            doReturn(currencyId2).whenever(form).currencyId

            sut.insertUpstreamCurrencyIfNeeded(coAccountId, spotId, form)

            verify(coAccountGetWithCheckHelper, times(1)).getCoAccountWithCheck(coAccountId)
            verify(spotUpstreamCurrencyDao, times(1)).insert(
                SpotUpstreamCurrencyInsert(spotId, currencyId2)
            )
        }

        @ParameterizedTest
        @MethodSource("notInsertParams")
        @DisplayName("登録なし")
        fun isNotInsert(upstreamType: UpstreamType, currencyId: CurrencyId) {
            doReturn(upstreamType).whenever(form).upstreamType
            doReturn(currencyId).whenever(form).currencyId

            sut.insertUpstreamCurrencyIfNeeded(coAccountId, spotId, form)

            verify(coAccountGetWithCheckHelper, times(1)).getCoAccountWithCheck(coAccountId)
            verify(spotUpstreamCurrencyDao, never()).insert(any())
        }

        private fun notInsertParams() = listOf(
            Arguments.of(UpstreamType.none, currencyId2),
            Arguments.of(UpstreamType.prebidjs, currencyId1)
        )
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("insertBannerIfNeededのテスト")
    inner class InsertBannerIfNeededTest {
        val form: SpotCreateForm = mock()
        val spotBannerDisplayInsert: SpotBannerDisplayInsert = mock()

        @BeforeAll
        fun beforeAll() {
            mockkObject(SpotBannerDisplayInsert)
            every { SpotBannerDisplayInsert.of(any(), any()) } returns spotBannerDisplayInsert
        }

        @AfterAll
        fun afterAll() {
            unmockkAll()
        }

        @Test
        @DisplayName("登録あり")
        fun isInsert() {
            doReturn(mock<BannerSettingForm>()).whenever(form).banner

            sut.insertBannerIfNeeded(spotId, form)

            verify(spotBannerDao, times(1)).insert(SpotBannerInsert(spotId))
            verify(spotBannerDisplayDao, times(1)).insert(spotBannerDisplayInsert)
        }

        @Test
        @DisplayName("登録なし")
        fun isNotInsert() {
            doReturn(null).whenever(form).banner

            sut.insertBannerIfNeeded(spotId, form)

            verify(spotBannerDao, never()).insert(any())
            verify(spotBannerDisplayDao, never()).insert(any())
        }
    }

    @Nested
    @DisplayName("insertNativeIfNeededのテスト")
    inner class InsertNativeIfNeededTest {
        val basic: BasicSettingCreateForm = mock {
            on { this.isDisplayControl } doReturn false
        }
        val form: SpotCreateForm = mock {
            on { this.basic } doReturn basic
        }
        val standardForm: NativeStandardForm = mock()
        val videoForm: NativeVideoForm = mock()
        val spotNativeInsert: SpotNativeInsert = mock()
        val spotNativeDisplayInsert: SpotNativeDisplayInsert = mock()
        val spotNativeVideoDisplayInsert: SpotNativeVideoDisplayInsert = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotNativeInsert, SpotNativeDisplayInsert, SpotNativeVideoDisplayInsert)
            every { SpotNativeInsert.of(any(), any()) } returns spotNativeInsert
            every { SpotNativeDisplayInsert.of(any(), any()) } returns spotNativeDisplayInsert
            every { SpotNativeVideoDisplayInsert.of(any(), any(), any()) } returns spotNativeVideoDisplayInsert
        }

        @Test
        @DisplayName("ネイティブ設定全入力")
        fun isFullSetting() {
            doReturn(NativeSettingForm(standardForm, videoForm)).whenever(form).native

            sut.insertNativeIfNeeded(spotId, form)

            verify(spotNativeDao, times(1)).insert(spotNativeInsert)
            verify(spotNativeDisplayDao, times(1)).insert(spotNativeDisplayInsert)
            verify(spotNativeVideoDisplayDao, times(1)).insert(spotNativeVideoDisplayInsert)
        }

        @Test
        @DisplayName("ネイティブ設定が空")
        fun isEmptySetting() {
            // 実際には最低１つのデザインが設定されるが、分岐の確認のため両方にnullを設定している.
            doReturn(NativeSettingForm(null, null)).whenever(form).native

            sut.insertNativeIfNeeded(spotId, form)

            verify(spotNativeDao, times(1)).insert(spotNativeInsert)
            verify(spotNativeDisplayDao, never()).insert(any())
            verify(spotNativeVideoDisplayDao, never()).insert(any())
        }

        @Test
        @DisplayName("ネイティブ設定なし")
        fun isNoSetting() {
            doReturn(null).whenever(form).native

            sut.insertNativeIfNeeded(spotId, form)

            verify(spotNativeDao, never()).insert(any())
            verify(spotNativeDisplayDao, never()).insert(any())
            verify(spotNativeVideoDisplayDao, never()).insert(any())
        }
    }

    @Nested
    @DisplayName("insertVideoIfNeededのテスト")
    inner class InsertVideoIfNeededTest {
        val form: SpotCreateForm = mock()
        val aspectRatios: Collection<AspectRatio> = mock()
        val spotVideoInsert: SpotVideoInsert = mock()
        val spotVideoDisplayInserts: List<SpotVideoDisplayInsert> = mock()
        val spotVideoFloorCpmInserts: List<SpotVideoFloorCpmInsert> = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotVideoInsert, SpotVideoDisplayInsert, SpotVideoFloorCpmInsert)

            every { SpotVideoInsert.of(any(), any()) } returns spotVideoInsert
            every { SpotVideoDisplayInsert.of(any(), any(), any()) } returns spotVideoDisplayInserts
            every { SpotVideoFloorCpmInsert.of(any(), any<VideoSettingForm>()) } returns spotVideoFloorCpmInserts
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("ビデオ設定あり")
        fun isPersistVideo() {
            val video: VideoSettingForm = mock()
            doReturn(video).whenever(form).video

            sut.insertVideoIfNeeded(spotId, form, aspectRatios)

            verify(spotVideoDao, times(1)).insert(spotVideoInsert)
            verify(spotVideoDisplayDao, times(1)).inserts(spotVideoDisplayInserts)
            verify(spotVideoFloorCpmDao, times(1)).inserts(spotVideoFloorCpmInserts)
        }

        @Test
        @DisplayName("ビデオ設定なし")
        fun isNotPersistVideo() {
            doReturn(null).whenever(form).video

            sut.insertVideoIfNeeded(spotId, form, aspectRatios)

            verify(spotVideoDao, never()).insert(any())
            verify(spotVideoDisplayDao, never()).inserts(any())
            verify(spotVideoFloorCpmDao, never()).inserts(any())
        }
    }

    @Nested
    @DisplayName("editBasicのテスト")
    inner class EditBasicTest {
        val form = BasicSettingEditForm("spot", SpotMaxSizeForm(100, 200), "desc", "pageUrl")
        val update = SpotUpdate(spotId, "spot", 100, 200, "desc", "pageUrl")

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doNothing().whenever(spotDao).update(any())

            sut.editBasic(spotId, form)

            verify(spotDao, times(1)).update(update)
        }
    }

    @Nested
    @DisplayName("editDspsのテスト")
    inner class EditDspsTest {
        val forms = listOf(
            DspForm(dspId1, mock(), mock()),
            DspForm(dspId2, mock(), mock()),
            DspForm(dspId3, mock(), mock()),
            DspForm(dspId4, mock(), mock())
        )
        val inserts = listOf(
            RelaySpotDspInsert(spotId, dspId1, mock(), 0, mock()),
            RelaySpotDspInsert(spotId, dspId2, mock(), 0, mock()),
            RelaySpotDspInsert(spotId, dspId3, mock(), 0, mock()),
            RelaySpotDspInsert(spotId, dspId4, mock(), 0, mock())
        )
        val defaultDsps: List<RelayDefaultCoAccountDsp> = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(RelaySpotDspInsert)

            doNothing().whenever(relaySpotDspDao).bulkInsert(any())
            doNothing().whenever(relaySpotDspDao).bulkUpdate(any())
            doNothing().whenever(relaySpotDspDao).deleteBySpotIdAndDspIds(any(), any())
            doReturn(defaultDsps).whenever(relayDefaultCoAccountDspDao).selectByCoAccountId(coAccountId)
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("設定追加")
        fun isNewSetting() {
            doReturn(emptyList<RelaySpotDsp>()).whenever(relaySpotDspDao).selectBySpotId(any())
            every { RelaySpotDspInsert.of(any(), any(), any()) } returns inserts

            sut.editDsps(coAccountId, spotId, forms)

            verify(relaySpotDspDao, times(1)).selectBySpotId(spotId)
            verify(relayDefaultCoAccountDspDao, times(1)).selectByCoAccountId(coAccountId)
            verify(relaySpotDspDao, times(1)).bulkInsert(inserts)
            verify(relaySpotDspDao, times(1)).bulkUpdate(emptyList())
            verify(relaySpotDspDao, times(1)).deleteBySpotIdAndDspIds(spotId, emptyList())
        }

        @Test
        @DisplayName("設定変更")
        fun isEditSetting() {
            doReturn(listOf(dspId3, dspId4, dspId5).map { RelaySpotDsp(spotId, it, mock(), mock()) })
                .whenever(relaySpotDspDao)
                .selectBySpotId(any())
            every { RelaySpotDspInsert.of(any(), any(), any()) } returns inserts

            sut.editDsps(coAccountId, spotId, forms)

            verify(relaySpotDspDao, times(1)).selectBySpotId(spotId)
            verify(relayDefaultCoAccountDspDao, times(1)).selectByCoAccountId(coAccountId)
            verify(relaySpotDspDao, times(1)).bulkInsert(listOf(inserts[0], inserts[1]))
            verify(relaySpotDspDao, times(1)).bulkUpdate(listOf(inserts[2], inserts[3]))
            verify(relaySpotDspDao, times(1)).deleteBySpotIdAndDspIds(spotId, listOf(dspId5))
        }

        @Test
        @DisplayName("設定削除")
        fun isDeleteSetting() {
            doReturn(listOf(dspId1, dspId2, dspId3).map { RelaySpotDsp(spotId, it, mock(), mock()) })
                .whenever(relaySpotDspDao)
                .selectBySpotId(any())
            every { RelaySpotDspInsert.of(any(), any(), any()) } returns emptyList()

            sut.editDsps(coAccountId, spotId, emptyList())

            verify(relaySpotDspDao, times(1)).selectBySpotId(spotId)
            verify(relayDefaultCoAccountDspDao, times(1)).selectByCoAccountId(coAccountId)
            verify(relaySpotDspDao, times(1)).bulkInsert(emptyList())
            verify(relaySpotDspDao, times(1)).bulkUpdate(emptyList())
            verify(relaySpotDspDao, times(1)).deleteBySpotIdAndDspIds(spotId, listOf(dspId1, dspId2, dspId3))
        }
    }

    @Nested
    @DisplayName("editBannerのテスト")
    inner class EditBannerTest {
        val form: BannerSettingForm = mock()
        val currentSizeTypeIds = listOf(1, 2, 3, 4).map { SizeTypeId(it) }
        val spotBannerDisplayInsert: SpotBannerDisplayInsert = mock()
        val relaySpotSizeTypeInserts = sizeTypeIds.map { RelaySpotSizetypeInsert(spotId, it) }

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotBannerDisplayInsert)
            every { SpotBannerDisplayInsert.of(any(), any(), any()) } returns spotBannerDisplayInsert
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("新規")
        fun isNew() {
            sut.editBanner(spotId, form, true, false, emptyList(), sizeTypeIds)

            verify(spotBannerDao, times(1)).insert(SpotBannerInsert(spotId))
            verify(spotBannerDisplayDao, times(1)).insert(spotBannerDisplayInsert)
            verify(relaySpotSizetypeDao, times(1)).bulkInsert(relaySpotSizeTypeInserts)
        }

        @Test
        @DisplayName("更新")
        fun isModify() {
            sut.editBanner(spotId, form, true, true, currentSizeTypeIds, sizeTypeIds)

            verify(relaySpotSizetypeDao, times(1)).deleteBySpotIdAndSizeTypeIds(spotId, currentSizeTypeIds)
            verify(spotBannerDisplayDao, times(1)).update(spotBannerDisplayInsert)
            verify(relaySpotSizetypeDao, times(1)).bulkInsert(relaySpotSizeTypeInserts)
        }

        @Test
        @DisplayName("削除")
        fun isDelete() {
            sut.editBanner(spotId, null, true, true, currentSizeTypeIds, emptyList())

            verify(relaySpotSizetypeDao, times(1)).deleteBySpotIdAndSizeTypeIds(spotId, currentSizeTypeIds)
            verify(spotBannerDao, times(1)).deleteById(spotId)
            verify(spotBannerDisplayDao, times(1)).deleteById(spotId)
        }
    }

    @Nested
    @DisplayName("editNativeのテスト")
    inner class EditNativeTest {
        val form: NativeSettingForm = mock()
        val standardForm: NativeStandardForm = mock()
        val videoForm: NativeVideoForm = mock()
        val currentSizeTypeIds = listOf(1, 2, 3, 4).map { SizeTypeId(it) }
        val spotNativeInsert: SpotNativeInsert = mock()
        val spotNativeUpdate: SpotNativeUpdate = mock()
        val spotNativeDisplayInsert: SpotNativeDisplayInsert = mock()
        val spotNativeVideoDisplayInsert: SpotNativeVideoDisplayInsert = mock()
        val relaySpotSizeTypeInserts = sizeTypeIds.map { RelaySpotSizetypeInsert(spotId, it) }

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotNativeInsert, SpotNativeUpdate, SpotNativeDisplayInsert, SpotNativeVideoDisplayInsert)
            every { SpotNativeInsert.of(any(), any()) } returns spotNativeInsert
            every { SpotNativeUpdate.of(any(), any()) } returns spotNativeUpdate
            every { SpotNativeDisplayInsert.of(any(), any()) } returns spotNativeDisplayInsert
            every { SpotNativeVideoDisplayInsert.of(any(), any(), any()) } returns spotNativeVideoDisplayInsert
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Nested
        @DisplayName("新規")
        inner class NewTest {
            @Test
            @DisplayName("設定あり")
            fun isNotEmpty() {
                doReturn(standardForm).whenever(form).standard
                doReturn(videoForm).whenever(form).video

                sut.editNative(spotId, form, true, false, false, emptyList(), sizeTypeIds)

                verify(relaySpotSizetypeDao, times(1)).deleteBySpotIdAndSizeTypeIds(spotId, emptyList())
                verify(spotNativeDao, times(1)).insert(spotNativeInsert)
                verify(spotNativeDisplayDao, times(1)).insert(spotNativeDisplayInsert)
                verify(spotNativeVideoDisplayDao, times(1)).insert(spotNativeVideoDisplayInsert)
                verify(relaySpotSizetypeDao, times(1)).bulkInsert(relaySpotSizeTypeInserts)
            }

            @Test
            @DisplayName("設定なし")
            fun isEmpty() {
                // 本来はあり得ない設定だがテストを簡略化するため両方 null を設定している
                doReturn(null).whenever(form).standard
                doReturn(null).whenever(form).video

                sut.editNative(spotId, form, true, false, false, emptyList(), sizeTypeIds)

                verify(relaySpotSizetypeDao, times(1)).deleteBySpotIdAndSizeTypeIds(spotId, emptyList())
                verify(spotNativeDao, times(1)).insert(spotNativeInsert)
                verify(spotNativeDisplayDao, never()).insert(spotNativeDisplayInsert)
                verify(spotNativeVideoDisplayDao, never()).insert(spotNativeVideoDisplayInsert)
                verify(relaySpotSizetypeDao, times(1)).bulkInsert(relaySpotSizeTypeInserts)
            }
        }

        @Nested
        @DisplayName("更新")
        inner class ModifyTest {
            @Test
            @DisplayName("通常・動画ともに更新")
            fun isUpdateBoth() {
                doReturn(standardForm).whenever(form).standard
                doReturn(videoForm).whenever(form).video

                sut.editNative(spotId, form, true, true, true, currentSizeTypeIds, sizeTypeIds)

                verify(relaySpotSizetypeDao, times(1)).deleteBySpotIdAndSizeTypeIds(spotId, currentSizeTypeIds)
                verify(spotNativeDao, times(1)).update(spotNativeUpdate)
                verify(spotNativeDisplayDao, times(1)).update(spotNativeDisplayInsert)
                verify(spotNativeVideoDisplayDao, times(1)).update(spotNativeVideoDisplayInsert)
                verify(relaySpotSizetypeDao, times(1)).bulkInsert(relaySpotSizeTypeInserts)
            }

            @Test
            @DisplayName("通常・動画ともに削除")
            fun isBothDelete() {
                // 本来はあり得ない設定だがテストを簡略化するため両方 null を設定している
                doReturn(null).whenever(form).standard
                doReturn(null).whenever(form).video

                sut.editNative(spotId, form, true, true, true, currentSizeTypeIds, sizeTypeIds)

                verify(relaySpotSizetypeDao, times(1)).deleteBySpotIdAndSizeTypeIds(spotId, currentSizeTypeIds)
                verify(spotNativeDao, times(1)).update(spotNativeUpdate)
                verify(spotNativeDisplayDao, times(1)).deleteById(spotId)
                verify(spotNativeVideoDisplayDao, times(1)).deleteById(spotId)
                verify(relaySpotSizetypeDao, times(1)).bulkInsert(relaySpotSizeTypeInserts)
            }

            @Test
            @DisplayName("通常新規・動画更新")
            fun isNewStandardAndUpdateVideo() {
                doReturn(standardForm).whenever(form).standard
                doReturn(videoForm).whenever(form).video

                sut.editNative(spotId, form, true, false, true, currentSizeTypeIds, sizeTypeIds)

                verify(relaySpotSizetypeDao, times(1)).deleteBySpotIdAndSizeTypeIds(spotId, currentSizeTypeIds)
                verify(spotNativeDao, times(1)).update(spotNativeUpdate)
                verify(spotNativeDisplayDao, times(1)).insert(spotNativeDisplayInsert)
                verify(spotNativeVideoDisplayDao, times(1)).update(spotNativeVideoDisplayInsert)
                verify(relaySpotSizetypeDao, times(1)).bulkInsert(relaySpotSizeTypeInserts)
            }

            @Test
            @DisplayName("通常更新・動画新規")
            fun isUpdateStandardAndNewVideo() {
                doReturn(standardForm).whenever(form).standard
                doReturn(videoForm).whenever(form).video

                sut.editNative(spotId, form, true, true, false, currentSizeTypeIds, sizeTypeIds)

                verify(relaySpotSizetypeDao, times(1)).deleteBySpotIdAndSizeTypeIds(spotId, currentSizeTypeIds)
                verify(spotNativeDao, times(1)).update(spotNativeUpdate)
                verify(spotNativeDisplayDao, times(1)).update(spotNativeDisplayInsert)
                verify(spotNativeVideoDisplayDao, times(1)).insert(spotNativeVideoDisplayInsert)
                verify(relaySpotSizetypeDao, times(1)).bulkInsert(relaySpotSizeTypeInserts)
            }
        }

        @Test
        @DisplayName("削除")
        fun isDelete() {
            sut.editNative(spotId, null, true, true, true, currentSizeTypeIds, emptyList())

            verify(relaySpotSizetypeDao, times(1)).deleteBySpotIdAndSizeTypeIds(spotId, currentSizeTypeIds)
            verify(spotNativeDao, times(1)).deleteById(spotId)
            verify(spotNativeDisplayDao, times(1)).deleteById(spotId)
            verify(spotNativeDisplayDao, times(1)).deleteById(spotId)
        }
    }

    @Nested
    @DisplayName("editVideoのテスト")
    inner class EditVideoTest {
        @Nested
        @DisplayName("登録のテスト")
        inner class CreateTest {
            val spotVideoInsert: SpotVideoInsert = mock()
            val spotVideoDisplayInserts: List<SpotVideoDisplayInsert> = mock()
            val spotVideoFloorCpmInserts: List<SpotVideoFloorCpmInsert> = mock()
            val aspectRatios: List<AspectRatio> = mock()

            @BeforeEach
            fun beforeEach() {
                mockkObject(SpotVideoInsert, SpotVideoDisplayInsert, SpotVideoFloorCpmInsert)
                every { SpotVideoInsert.of(any(), any()) } returns spotVideoInsert
                every {
                    SpotVideoDisplayInsert.of(any(), any<VideoSettingForm>(), any())
                } returns spotVideoDisplayInserts
                every {
                    SpotVideoFloorCpmInsert.of(any(), any<VideoSettingForm>())
                } returns spotVideoFloorCpmInserts
            }

            @AfterEach
            fun afterEach() {
                unmockkAll()
            }

            @Test
            @DisplayName("正常")
            fun isCorrect() {
                val form = VideoSettingForm(20, true, 30, mock())

                sut.editVideo(spotId, form, true, false, emptyList(), emptyList(), aspectRatios)

                verify(spotDao, times(1)).updateRotationMaxById(spotId, 20)
                verify(spotVideoDao, times(1)).insert(spotVideoInsert)
                verify(spotVideoDisplayDao, times(1)).inserts(spotVideoDisplayInserts)
                verify(spotVideoFloorCpmDao, times(1)).inserts(spotVideoFloorCpmInserts)
            }
        }

        @Nested
        @DisplayName("更新のテスト")
        inner class ModifyTest {
            val details: List<VideoDetailForm> = mock()
            val currentSpotDisplays: List<SpotVideoDisplay> = mock()
            val currentSpotVideoFloorCpms: List<SpotVideoFloorCpm> = mock()
            val spotVideoUpdate: SpotVideoUpdate = mock()
            val aspectRatios: List<AspectRatio> = mock()

            @BeforeEach
            fun beforeEach() {
                mockkObject(SpotVideoUpdate)
                every { SpotVideoUpdate.of(any(), any()) } returns spotVideoUpdate

                doNothing().whenever(sut).editSpotVideoDisplay(any(), any(), anyOrNull(), any(), any())
                doNothing().whenever(sut).editSpotVideoFloorCpm(any(), any(), any())
            }

            @AfterEach
            fun afterEach() {
                unmockkAll()
            }

            @Test
            @DisplayName("正常")
            fun isCorrect() {
                val form = VideoSettingForm(20, true, 30, details)

                sut.editVideo(spotId, form, true, true, currentSpotDisplays, currentSpotVideoFloorCpms, aspectRatios)

                verify(spotDao, times(1)).updateRotationMaxById(spotId, 20)
                verify(spotVideoDao, times(1)).update(spotVideoUpdate)
                verify(sut, times(1)).editSpotVideoDisplay(spotId, details, 30, currentSpotDisplays, aspectRatios)
                verify(sut, times(1)).editSpotVideoFloorCpm(spotId, details, currentSpotVideoFloorCpms)
            }
        }

        @Nested
        @DisplayName("削除のテスト")
        inner class DeleteTest {
            val aspectRatioIds = listOf(aspectRatioId1, aspectRatioId2, aspectRatioId3)
            val currentSpotVideoDisplays = aspectRatioIds.map { mockSpotVideoDisplay(it) }
            val currentSpotVideoFloorCpms = aspectRatioIds.map { mockSpotVideoFloorCpm(it) }

            @Test
            @DisplayName("正常")
            fun isCorrect() {
                sut.editVideo(spotId, null, true, true, currentSpotVideoDisplays, currentSpotVideoFloorCpms, mock())

                verify(spotDao, times(1)).updateRotationMaxById(spotId, Spot.rotationMaxDefaultValue)
                verify(spotVideoDao, times(1)).deleteById(spotId)
                verify(spotVideoDisplayDao, times(1)).deleteBySpotIdAndAspectRatioIds(spotId, aspectRatioIds)
                verify(spotVideoFloorCpmDao, times(1)).deleteBySpotIdAndAspectRatioIds(spotId, aspectRatioIds)
            }
        }
    }

    @Nested
    @DisplayName("editSpotVideoDisplayのテスト")
    inner class EditSpotVideoDisplayTest {
        val prLabelType = 10
        val forms = listOf(aspectRatioId1, aspectRatioId2, aspectRatioId3, aspectRatioId4).map {
            mockVideoDetailForm(it)
        }
        val currentSpotVideoDisplays = listOf(aspectRatioId3, aspectRatioId4, aspectRatioId5, aspectRatioId6).map {
            mockSpotVideoDisplay(it)
        }
        val aspectRatios: List<AspectRatio> = mock()
        val inserts: List<SpotVideoDisplayInsert> = mock()
        val updates: List<SpotVideoDisplayInsert> = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotVideoDisplayInsert)
            every {
                SpotVideoDisplayInsert.of(spotId, listOf(forms[0], forms[1]), prLabelType, aspectRatios)
            } returns inserts
            every {
                SpotVideoDisplayInsert.of(spotId, listOf(forms[2], forms[3]), prLabelType, aspectRatios)
            } returns updates
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            sut.editSpotVideoDisplay(spotId, forms, 10, currentSpotVideoDisplays, aspectRatios)

            verify(spotVideoDisplayDao, times(1)).inserts(inserts)
            verify(spotVideoDisplayDao, times(1)).updates(updates)
            verify(spotVideoDisplayDao, times(1)).deleteBySpotIdAndAspectRatioIds(
                spotId, listOf(aspectRatioId5, aspectRatioId6)
            )
        }
    }

    @Nested
    @DisplayName("editSpotVideoFloorCpmのテスト")
    inner class EditSpotVideoFloorCpmTest {
        val date = LocalDate.of(2024, 1, 1)
        val forms = listOf(
            // 新規
            mockVideoDetailForm(aspectRatioId1, null),
            mockVideoDetailForm(aspectRatioId2, null),
            // 更新
            mockVideoDetailForm(aspectRatioId3, date),
            mockVideoDetailForm(aspectRatioId4, date),
            // 削除
            mockVideoDetailForm(aspectRatioId5, date),
        )
        val currentSpotVideoFloorCpms = listOf(
            // Formとは異なる日付
            SpotVideoFloorCpm(mock(), aspectRatioId1, date.plusDays(1), mock(), mock()),
            // 更新
            SpotVideoFloorCpm(mock(), aspectRatioId3, date, mock(), mock()),
            SpotVideoFloorCpm(mock(), aspectRatioId3, date.plusDays(1), mock(), mock()),
            SpotVideoFloorCpm(mock(), aspectRatioId4, date, mock(), mock()),
            // 削除
            SpotVideoFloorCpm(mock(), aspectRatioId5, date, mock(), mock()),
            SpotVideoFloorCpm(mock(), aspectRatioId6, date, mock(), mock())
        )
        val inserts: List<SpotVideoFloorCpmInsert> = listOf(aspectRatioId1, aspectRatioId2).map {
            mockSpotVideoFloorCpmInsert(it)
        }
        val updates: List<SpotVideoFloorCpmUpdate> = listOf(aspectRatioId3, aspectRatioId4).map {
            mockSpotVideoFloorCpmUpdate(it)
        }

        @BeforeEach
        fun beforeEach() {
            mockkObject(SpotVideoFloorCpmInsert, SpotVideoFloorCpmUpdate)
            every { SpotVideoFloorCpmInsert.of(any(), listOf(forms[0], forms[1])) } returns inserts
            every { SpotVideoFloorCpmUpdate.of(spotId, listOf(forms[2], forms[3], forms[4])) } returns updates
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            sut.editSpotVideoFloorCpm(spotId, forms, currentSpotVideoFloorCpms)

            verify(spotVideoFloorCpmDao, times(1)).inserts(inserts)
            verify(spotVideoFloorCpmDao, times(1)).updates(updates)
            verify(spotVideoFloorCpmDao, times(1)).deleteBySpotIdAndAspectRatioIds(
                spotId, listOf(aspectRatioId5, aspectRatioId6)
            )
        }
    }

    private fun mockVideoDetailForm(
        aspectRatioId: AspectRatioId,
        startDate: LocalDate? = mock()
    ): VideoDetailForm = mock {
        on { this.aspectRatioId } doReturn aspectRatioId
        on { this.floorCpmStartDate } doReturn startDate
    }

    private fun mockSpotVideoDisplay(aspectRatioId: AspectRatioId): SpotVideoDisplay = mock {
        on { this.aspectRatioId } doReturn aspectRatioId
    }

    private fun mockSpotVideoFloorCpm(
        aspectRatioId: AspectRatioId,
        startDate: LocalDate = mock()
    ): SpotVideoFloorCpm = mock {
        on { this.aspectRatioId } doReturn aspectRatioId
        on { this.startDate } doReturn startDate
    }

    private fun mockSpotVideoFloorCpmInsert(aspectRatioId: AspectRatioId): SpotVideoFloorCpmInsert = mock {
        on { this.aspectRatioId } doReturn aspectRatioId
    }

    private fun mockSpotVideoFloorCpmUpdate(aspectRatioId: AspectRatioId): SpotVideoFloorCpmUpdate = mock {
        on { this.aspectRatioId } doReturn aspectRatioId
    }
}
