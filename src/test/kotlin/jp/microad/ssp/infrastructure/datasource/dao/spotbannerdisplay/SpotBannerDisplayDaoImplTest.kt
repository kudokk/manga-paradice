package jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import jp.mangaka.ssp.util.TestUtils.assertEqualsInAnyOrder
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

@DisplayName("SpotBannerDisplayDaoImplのテスト")
private class SpotBannerDisplayDaoImplTest {
    val spotBannerDisplay1 = SpotBannerDisplay(
        SpotId(1), 11, 21, 31, 41, true, false, DecorationId(51), 61, 71, "line1", "bg1", "frame1"
    )
    val spotBannerDisplay2 = SpotBannerDisplay(
        SpotId(2), 12, 22, 32, 42, false, true, DecorationId(52), 62, 72, "line2", "bg2", "frame2"
    )
    val spotBannerDisplay3 = SpotBannerDisplay(
        SpotId(3), null, null, null, null, true, false, null, null, null, null, null, null
    )

    @Nested
    @DatabaseSetup("/dataset/SpotBannerDisplay/setup_persist.xml")
    @DisplayName("insertのテスト")
    inner class InsertTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotBannerDisplay/expected_insert.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            listOf(
                spotBannerDisplayInsert(2, null, true, true, null, null, null, null, null, null),
                spotBannerDisplayInsert(3, 10, false, false, 20, 30, 40, "line1", "bg1", "frame1"),
                spotBannerDisplayInsert(4, 11, false, true, 21, 31, 41, "line2", "bg2", "frame2")
            ).forEach { sut.insert(it) }
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotBannerDisplay/setup.xml")
    @DisplayName("selectByIdのテスト")
    inner class SelectByIdTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = listOf(1, 2, 3).map {
                sut.selectById(SpotId(it))
            }

            assertEquals(
                listOf(spotBannerDisplay1, spotBannerDisplay2, spotBannerDisplay3),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectById(SpotId(99))

            assertNull(actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotBannerDisplay/setup.xml")
    @DisplayName("selectByIdsのテスト")
    inner class SelectByIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectByIds(listOf(1, 2, 3).map { SpotId(it) })

            assertEqualsInAnyOrder(
                listOf(spotBannerDisplay1, spotBannerDisplay2, spotBannerDisplay3),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            assertEmpty(sut.selectByIds(listOf(SpotId(999))))
        }

        @Test
        @DisplayName("引数の広告枠IDリストが空")
        fun isEmptySpotIds() {
            assertEmpty(sut.selectByIds(emptyList()))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotBannerDisplay/setup_persist.xml")
    @DisplayName("updateのテスト")
    inner class UpdateTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotBannerDisplay/expected_update.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            listOf(
                spotBannerDisplayInsert(1, null, false, false, null, null, null, null, null, null),
                spotBannerDisplayInsert(
                    5, 15, true, false, 25, 2, 35, "rgba(10,20,30,0.1)", "rgba(20,30,40,0.5)", "rgba(30,40,50,1.0)"
                )
            ).forEach { sut.update(it) }
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotBannerDisplay/setup_persist.xml")
    @DisplayName("deleteByIdのテスト")
    inner class DeleteByIdTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotBannerDisplay/expected_delete_by_id.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.deleteById(SpotId(5))
        }
    }

    private fun spotBannerDisplayInsert(
        spotId: Int, positionBottom: Int?, isScalable: Boolean, isDisplayScrolling: Boolean, decorationId: Int?,
        closeButtonType: Int?, closeButtonSize: Int?, closeButtonLineColor: String?, closeButtonBgColor: String?,
        closeButtonFrameColor: String?
    ) = SpotBannerDisplayInsert(
        SpotId(spotId), positionBottom, isScalable.toString(), isDisplayScrolling.toString(),
        decorationId?.let { DecorationId(it) }, closeButtonType, closeButtonSize, closeButtonLineColor,
        closeButtonBgColor, closeButtonFrameColor
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(SpotBannerDisplayDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: SpotBannerDisplayDaoImpl
    }
}
