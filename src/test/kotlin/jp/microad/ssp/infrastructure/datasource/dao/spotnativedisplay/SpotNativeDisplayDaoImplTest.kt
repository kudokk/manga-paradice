package jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
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

@DisplayName("SpotNativeDisplayDaoImplのテスト")
private class SpotNativeDisplayDaoImplTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
        val spotId3 = SpotId(3)
        val spotId4 = SpotId(4)
        val spotId5 = SpotId(5)
        val spotId99 = SpotId(99)
        val nativeTemplateId11 = NativeTemplateId(11)
        val nativeTemplateId12 = NativeTemplateId(12)
        val nativeTemplateId13 = NativeTemplateId(13)
        val nativeTemplateId14 = NativeTemplateId(14)
        val nativeTemplateId15 = NativeTemplateId(15)
    }

    val spotNativeDisplay1 = SpotNativeDisplay(
        spotId1, nativeTemplateId11, 21, 31, 41, 51, 61, 71, "line1", "bg1", "frame1"
    )
    val spotNativeDisplay2 = SpotNativeDisplay(
        spotId2, nativeTemplateId12, 22, 32, 42, 52, 62, 72, "line2", "bg2", "frame2"
    )
    val spotNativeDisplay3 = SpotNativeDisplay(
        spotId3, nativeTemplateId13, null, null, null, null, null, null, null, null, null
    )

    @Nested
    @DatabaseSetup("/dataset/SpotNativeDisplay/setup_persist.xml")
    @DisplayName("insertのテスト")
    inner class InsertTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotNativeDisplay/expected_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            listOf(
                SpotNativeDisplayInsert(spotId4, nativeTemplateId14, null, null, null, null, null),
                SpotNativeDisplayInsert(spotId5, nativeTemplateId15, 20, 30, "#444444", "#555555", "#666666")
            ).forEach { sut.insert(it) }
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotNativeDisplay/setup.xml")
    @DisplayName("selectByIdのテスト")
    inner class SelectByIdTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = listOf(1, 2, 3).map {
                sut.selectById(SpotId(it))
            }

            assertEquals(
                listOf(spotNativeDisplay1, spotNativeDisplay2, spotNativeDisplay3),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectById(spotId99)

            assertNull(actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotNativeDisplay/setup.xml")
    @DisplayName("selectByIdsのテスト")
    inner class SelectByIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectByIds(listOf(1, 2, 3).map { SpotId(it) })

            assertEquals(
                listOf(spotNativeDisplay1, spotNativeDisplay2, spotNativeDisplay3),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            assertEmpty(sut.selectByIds(listOf(spotId99)))
        }

        @Test
        @DisplayName("引数の広告枠IDリストが空")
        fun isEmptySpotIds() {
            assertEmpty(sut.selectByIds(emptyList()))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotNativeDisplay/setup_persist.xml")
    @DisplayName("updateのテスト")
    inner class UpdateTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotNativeDisplay/expected_update.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            listOf(
                SpotNativeDisplayInsert(spotId1, nativeTemplateId14, null, null, null, null, null),
                SpotNativeDisplayInsert(spotId2, nativeTemplateId15, 25, 35, "#123456", "#234567", "#345678")
            ).forEach { sut.update(it) }
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotNativeDisplay/setup_persist.xml")
    @DisplayName("deleteByIdのテスト")
    inner class DeleteByIdTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotNativeDisplay/expected_delete_by_id.xml",
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
    @Import(SpotNativeDisplayDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: SpotNativeDisplayDaoImpl
    }
}
