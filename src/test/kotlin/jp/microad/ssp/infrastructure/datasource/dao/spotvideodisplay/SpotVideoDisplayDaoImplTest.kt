package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import jp.mangaka.ssp.util.TestUtils.assertEqualsInAnyOrder
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

@DisplayName("SpotVideoDisplayDaoImplのテスト")
private class SpotVideoDisplayDaoImplTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
        val spotId3 = SpotId(3)
        val spotId4 = SpotId(4)
        val aspectRatioId1 = AspectRatioId(1)
        val aspectRatioId2 = AspectRatioId(2)
        val aspectRatioId10 = AspectRatioId(10)
        val aspectRatioId11 = AspectRatioId(11)
        val aspectRatioId12 = AspectRatioId(12)
    }

    val spotVideoDisplay1of10 = SpotVideoDisplay(
        spotId1, aspectRatioId10, 20, 40, 50, 60, 70, true, false, true, 80, 90, "line1",
        "bg1", "frame1", 100
    )
    val spotVideoDisplay1of11 = SpotVideoDisplay(
        spotId1, aspectRatioId11, 21, 41, 51, 61, 71, false, true, false, 81, 91, "line2",
        "bg2", "frame2", 101
    )
    val spotVideoDisplay1of12 = SpotVideoDisplay(
        spotId1, aspectRatioId12, 22, null, null, null, null, true, false, false, null, null,
        null, null, null, null
    )
    val spotVideoDisplay2of10 = SpotVideoDisplay(
        spotId2, aspectRatioId10, 23, null, null, null, null, true, false, false, null, null,
        null, null, null, null
    )
    val spotVideoDisplay2of11 = SpotVideoDisplay(
        spotId2, aspectRatioId11, 24, 44, 54, 64, 74, true, false, false, 84, 94,
        "line4", "bg4", "frame4", 104
    )

    @Nested
    @DatabaseSetup("/dataset/SpotVideoDisplay/setup_persist.xml")
    @DisplayName("insertsのテスト")
    inner class InsertsTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideoDisplay/expected_inserts.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.inserts(
                listOf(
                    SpotVideoDisplayInsert(
                        spotId3, aspectRatioId1, 1920, 1080, 10, null, 45, null, false.toString(), true.toString(),
                        false.toString(), 1, 80, "rgba30", "rgba31", "rgba32", 2
                    ),
                    SpotVideoDisplayInsert(
                        spotId3, aspectRatioId2, 720, 240, null, 60, null, 30, true.toString(), false.toString(),
                        true.toString(), 6, 45, "rgba40", "rgba41", "rgba42", 9
                    ),
                    SpotVideoDisplayInsert(
                        spotId4, aspectRatioId2, 380, 50, 100, null, null, 60, true.toString(), false.toString(),
                        true.toString(), null, null, null, null, null, null
                    ),
                )
            )
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideoDisplay/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("引数が空")
        fun isEmptySpotVideoDisplays() {
            sut.inserts(emptyList())
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotVideoDisplay/setup.xml")
    @DisplayName("selectBySpotIdのテスト")
    inner class SelectBySpotIdTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectBySpotId(spotId1)

            assertEqualsInAnyOrder(
                listOf(spotVideoDisplay1of10, spotVideoDisplay1of11, spotVideoDisplay1of12),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectBySpotId(SpotId(99))

            assertEmpty(actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotVideoDisplay/setup.xml")
    @DisplayName("selectBySpotIdsのテスト")
    inner class SelectBySpotIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectBySpotIds(listOf(spotId1, spotId2))

            assertEqualsInAnyOrder(
                listOf(
                    spotVideoDisplay1of10,
                    spotVideoDisplay1of11,
                    spotVideoDisplay1of12,
                    spotVideoDisplay2of10,
                    spotVideoDisplay2of11
                ),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            assertEmpty(sut.selectBySpotIds(listOf(SpotId(99))))
        }

        @Test
        @DisplayName("引数の広告枠IDリストが空")
        fun isEmptySpotIds() {
            assertEmpty(sut.selectBySpotIds(emptyList()))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotVideoDisplay/setup_persist.xml")
    @DisplayName("updatesのテスト")
    inner class UpdatesTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideoDisplay/expected_update.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            listOf(
                SpotVideoDisplayInsert(
                    spotId1, aspectRatioId1, 500, 600, null, null, null, null, "true", "false",
                    "true", null, null, null, null, null, null
                ),
                SpotVideoDisplayInsert(
                    spotId1, aspectRatioId2, 700, 800, 20, 30, 40, 50, "true", "true", "false",
                    3, 60, "rgba25", "rgba26", "rgba27", 8
                )
            ).let { sut.updates(it) }
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideoDisplay/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("引数が空のとき")
        fun isEmptySpotVideDisplays() {
            sut.updates(emptyList())
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotVideoDisplay/setup_persist.xml")
    @DisplayName("deleteBySpotIdAndAspectRatioIdsのテスト")
    inner class DeleteBySpotIdAndAspectRatioIdsTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideoDisplay/expected_delete_by_id_and_aspect_ratio_ids.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.deleteBySpotIdAndAspectRatioIds(spotId1, listOf(aspectRatioId1, aspectRatioId2))
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideoDisplay/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("引数のアスペクト比IDリストが空のとき")
        fun isEmptyAspectRatioIds() {
            sut.deleteBySpotIdAndAspectRatioIds(spotId1, emptyList())
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(SpotVideoDisplayDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: SpotVideoDisplayDaoImpl
    }
}
