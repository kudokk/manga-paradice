package jp.mangaka.ssp.infrastructure.datasource.dao.spot

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.proprietydsp.ProprietyDspId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.Anonymous
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DeliveryMethod
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import java.math.BigDecimal
import java.time.LocalDateTime

@DisplayName("SpotDaoImplのテスト")
private class SpotDaoImplTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
    }

    // spotTypeカラムをどうする
    val spot1 = spot(
        1, 1, "spot1", SpotStatus.active, 1, DisplayType.inline, UpstreamType.none, DeliveryMethod.js,
        101, 201, 21, true, 1, Anonymous.on, "0.0100", "desc1", "http://test.com/1", "2024-02-01T00:00:00"
    )
    val spot2 = spot(
        2, 1, "spot2", SpotStatus.standby, 2, DisplayType.inline, UpstreamType.prebidjs, DeliveryMethod.js,
        102, 202, 22, false, 1, Anonymous.on, "0.0200", "desc2", "http://test.com/2", "2024-02-02T00:00:00"
    )
    val spot3 = spot(
        3, 1, "spot3", SpotStatus.archive, 1, DisplayType.overlay, UpstreamType.none, DeliveryMethod.js,
        null, null, 23, true, 2, Anonymous.on, "0.0300", null, "http://test.com/3", "2024-02-03T00:00:00"
    )
    val spot4 = spot(
        4, 2, "spot4", SpotStatus.active, 2, DisplayType.overlay, UpstreamType.prebidjs, DeliveryMethod.sdk,
        null, null, 24, false, 2, Anonymous.off, "0.0400", null, null, "2024-02-04T00:00:00"
    )
    val spot5 = spot(
        5, 2, "spot5", SpotStatus.standby, 1, DisplayType.interstitial, UpstreamType.none, DeliveryMethod.sdk,
        null, null, 25, true, 1, Anonymous.off, "0.0500", null, null, "2024-02-05T00:00:00"
    )
    val spot6 = spot(
        6, 2, "spot6", SpotStatus.archive, 2, DisplayType.interstitial, UpstreamType.prebidjs, DeliveryMethod.sdk,
        106, 206, 26, false, 1, Anonymous.off, "0.0600", "desc6", "http://test.com/6", "2024-02-06T00:00:00"
    )
    val spot7 = spot(
        7, 3, "spot7", SpotStatus.active, 1, DisplayType.inline, UpstreamType.none, DeliveryMethod.js,
        107, 207, 27, true, 2, Anonymous.on, "0.0700", "desc7", "http://test.com/7", "2024-02-07T00:00:00"
    )
    val spot8 = spot(
        8, 3, "spot8", SpotStatus.standby, 2, DisplayType.inline, UpstreamType.prebidjs, DeliveryMethod.js,
        108, 208, 28, false, 2, Anonymous.on, "0.0800", null, null, "2024-02-08T00:00:00"
    )
    val spot9 = spot(
        9, 3, "spot9", SpotStatus.archive, 1, DisplayType.overlay, UpstreamType.none, DeliveryMethod.js,
        null, null, 29, true, 1, Anonymous.on, "0.0900", null, null, "2024-02-09T00:00:00"
    )
    val spot10 = spot(
        10, 3, "spot10", SpotStatus.active, 2, DisplayType.overlay, UpstreamType.prebidjs, DeliveryMethod.sdk,
        null, null, 30, false, 1, Anonymous.off, "0.1000", null, "http://test.com/10", "2024-02-10T00:00:00"
    )

    @Nested
    @DatabaseSetup("/dataset/Spot/setup_persist.xml")
    @DisplayName("insertのテスト")
    inner class InsertTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/Spot/expected_insert.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            listOf(
                spotInsert(
                    10, "spot1", SpotStatus.active, 20, DisplayType.inline, UpstreamType.none,
                    DeliveryMethod.js, null, null, 30, true, 40, null, null
                ),
                spotInsert(
                    11, "spot2", SpotStatus.standby, 21, DisplayType.overlay, UpstreamType.prebidjs,
                    DeliveryMethod.sdk, 100, 200, 31, false, 41, "descriptions2", "https://test.com/2/"
                ),
                spotInsert(
                    11, "spot3", SpotStatus.archive, 21, DisplayType.interstitial, UpstreamType.prebidjs,
                    DeliveryMethod.sdk, 101, 201, 31, false, 41, "descriptions3", "https://test.com/3/"
                )
            ).forEach { sut.insert(it) }
        }
    }

    // データ入ったxmlを作成
    @Nested
    @DatabaseSetup("/dataset/Spot/setup.xml")
    @DisplayName("selectByIdのテスト")
    inner class SelectByIdTest : TestBase() {
        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            assertEquals(spot1, sut.selectByIdAndStatus(spot1.spotId, SpotStatus.entries))
            assertEquals(spot2, sut.selectByIdAndStatus(spot2.spotId, SpotStatus.entries))
            assertEquals(spot3, sut.selectByIdAndStatus(spot3.spotId, SpotStatus.entries))
        }

        @Test
        @DisplayName("対象レコードなし")
        fun isNotFound() {
            assertNull(sut.selectByIdAndStatus(SpotId(99), SpotStatus.entries))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/Spot/setup.xml")
    @DisplayName("selectBySiteIdsAndStatusesのテスト")
    inner class SelectBySiteIdsAndStatusesTest : TestBase() {
        @Test
        @DisplayName("取得成功 - 取得数制限なし")
        fun isCorrectAndUnlimited() {
            val actual = sut.selectBySiteIdsAndStatuses(
                listOf(2, 3).map { SiteId(it) },
                listOf(SpotStatus.active, SpotStatus.standby)
            )

            assertEquals(listOf(spot4, spot5, spot7, spot8, spot10), actual)
        }

        @Test
        @DisplayName("取得成功 - 取得数制限あり")
        fun isCorrectAndLimited() {
            val actual = sut.selectBySiteIdsAndStatuses(
                listOf(2, 3).map { SiteId(it) },
                listOf(SpotStatus.active, SpotStatus.standby),
                3,
                1
            )

            assertEquals(listOf(spot5, spot7, spot8), actual)
        }

        @Test
        @DisplayName("対象0件")
        fun isEmptyResult() {
            assertEmpty(sut.selectBySiteIdsAndStatuses(listOf(SiteId(99)), SpotStatus.entries))
        }

        @Test
        @DisplayName("引数のサイトIDリストが空のとき")
        fun isEmptySiteIds() {
            assertEmpty(sut.selectBySiteIdsAndStatuses(emptyList(), SpotStatus.entries))
        }

        @Test
        @DisplayName("引数のステータスリストが空のとき")
        fun isEmptyStatuses() {
            assertEmpty(sut.selectBySiteIdsAndStatuses(listOf(1, 2, 3).map { SiteId(it) }, emptyList()))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/Spot/setup_persist.xml")
    @DisplayName("updateのテスト")
    inner class UpdateTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/Spot/expected_update.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            listOf(
                SpotUpdate(spotId1, "spot1", null, null, null, null),
                SpotUpdate(spotId2, "spot2", 900, 901, "descriptionsUpdate2", "https://test.com/update/2/"),
            ).forEach { sut.update(it) }
        }
    }

    @Nested
    @DatabaseSetup("/dataset/Spot/setup_persist.xml")
    @DisplayName("updateRotationMaxByIdのテスト")
    inner class UpdateRotationMaxByIdTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/Spot/expected_update_rotation_max_by_id.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.updateRotationMaxById(spotId1, 30)
        }
    }

    // spotにキャストしてくれる関数、形を綺麗にする
    private fun spot(
        spotId: Int, siteId: Int, spotName: String, spotStatus: SpotStatus, platformId: Int, displayType: DisplayType,
        upstreamType: UpstreamType, deliveryMethod: DeliveryMethod, width: Int?, height: Int?, rotationMax: Int,
        isAmp: Boolean, proprietyDspId: Int, anonymous: Anonymous, winningBidWeight: String, descriptions: String?,
        pageUrl: String?, updateTime: String
    ) = Spot(
        SpotId(spotId), SiteId(siteId), spotName, spotStatus, PlatformId(platformId), displayType, upstreamType,
        deliveryMethod, width, height, rotationMax, isAmp, ProprietyDspId(proprietyDspId), anonymous,
        BigDecimal(winningBidWeight), descriptions, pageUrl, LocalDateTime.parse(updateTime)
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(SpotDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: SpotDaoImpl
    }

    private fun spotInsert(
        siteId: Int, spotName: String, spotStatus: SpotStatus, platformId: Int, displayType: DisplayType,
        upstreamType: UpstreamType, deliveryMethod: DeliveryMethod, width: Int?, height: Int?, rotationMax: Int,
        isAmp: Boolean, proprietyDspId: Int, descriptions: String?, pageUrl: String?
    ) = SpotInsert(
        SiteId(siteId), spotName, spotStatus, PlatformId(platformId), displayType, upstreamType, deliveryMethod,
        width, height, rotationMax, isAmp.toString(), ProprietyDspId(proprietyDspId), descriptions, pageUrl
    )
}
