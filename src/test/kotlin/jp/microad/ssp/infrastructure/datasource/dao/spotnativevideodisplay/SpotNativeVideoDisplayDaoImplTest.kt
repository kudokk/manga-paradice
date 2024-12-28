package jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
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

@DisplayName("SpotNativeVideoDisplayDaoImplのテスト")
private class SpotNativeVideoDisplayDaoImplTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
        val spotId3 = SpotId(3)
        val spotId4 = SpotId(4)
        val spotId5 = SpotId(5)
        val spotId99 = SpotId(99)
        val aspectRatioId14 = AspectRatioId(14)
        val aspectRatioId15 = AspectRatioId(15)
        val nativeTemplateId11 = NativeTemplateId(11)
        val nativeTemplateId12 = NativeTemplateId(12)
        val nativeTemplateId13 = NativeTemplateId(13)
        val nativeTemplateId24 = NativeTemplateId(24)
        val nativeTemplateId25 = NativeTemplateId(25)
    }

    val spotNativeVideoDisplay1 = SpotNativeVideoDisplay(
        spotId1, nativeTemplateId11, 21, 31, 41, 51, true, 61, 71, "line1", "bg1", "frame1"
    )
    val spotNativeVideoDisplay2 = SpotNativeVideoDisplay(
        spotId2, nativeTemplateId12, 22, 32, 42, 52, false, 62, 72, "line2", "bg2", "frame2"
    )
    val spotNativeVideoDisplay3 = SpotNativeVideoDisplay(
        spotId3, nativeTemplateId13, null, null, null, null, true, null, null, null, null, null
    )

    @Nested
    @DatabaseSetup("/dataset/SpotNativeVideoDisplay/setup_persist.xml")
    @DisplayName("insertのテスト")
    inner class InsertTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotNativeVideoDisplay/expected_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            listOf(
                SpotNativeVideoDisplayInsert(
                    spotId4, aspectRatioId14, nativeTemplateId24, 34, 44, null, null, null, true.toString(),
                    null, null, null, null, null
                ),
                SpotNativeVideoDisplayInsert(
                    spotId5, aspectRatioId15, nativeTemplateId25, 35, 45, 55, 65, 75, false.toString(), 85, 95,
                    "#111111", "#222222", "#333333"
                ),
            ).forEach { sut.insert(it) }
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotNativeVideoDisplay/setup.xml")
    @DisplayName("selectBySpotIdのテスト")
    inner class SelectBySpotIdTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = listOf(spotId1, spotId2, spotId3).map {
                sut.selectBySpotId(it)
            }

            assertEquals(
                listOf(spotNativeVideoDisplay1, spotNativeVideoDisplay2, spotNativeVideoDisplay3),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectBySpotId(spotId99)

            Assertions.assertNull(actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotNativeVideoDisplay/setup.xml")
    @DisplayName("selectBySpotIdsのテスト")
    inner class SelectBySpotIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectBySpotIds(listOf(spotId1, spotId2, spotId3))

            assertEquals(
                listOf(spotNativeVideoDisplay1, spotNativeVideoDisplay2, spotNativeVideoDisplay3),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            assertEmpty(sut.selectBySpotIds(listOf(spotId99)))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotNativeVideoDisplay/setup_persist.xml")
    @DisplayName("updateのテスト")
    inner class UpdateTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotNativeVideoDisplay/expected_update.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            listOf(
                SpotNativeVideoDisplayInsert(
                    spotId1, mock(), nativeTemplateId24, 15, 25, null, null, null, true.toString(),
                    null, null, null, null, null
                ),
                SpotNativeVideoDisplayInsert(
                    spotId2, mock(), nativeTemplateId25, 25, 35, 45, 55, 65, false.toString(), 75, 85,
                    "#123456", "#234567", "#345678"
                ),
            ).forEach { sut.update(it) }
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotNativeVideoDisplay/setup_persist.xml")
    @DisplayName("deleteByIdのテスト")
    inner class DeleteByIdTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotNativeVideoDisplay/expected_delete_by_id.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.deleteById(spotId1)
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(SpotNativeVideoDisplayDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: SpotNativeVideoDisplayDaoImpl
    }
}
