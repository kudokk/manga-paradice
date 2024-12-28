package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
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
import java.time.LocalTime

@DisplayName("SpotVideoDaoImplのテスト")
private class SpotVideoDaoImplTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
        val spotId4 = SpotId(4)
        val spotId5 = SpotId(5)
    }

    @Nested
    @DatabaseSetup("/dataset/SpotVideo/setup_persist.xml")
    @DisplayName("insertのテスト")
    inner class InsertTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideo/expected_insert.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.insert(SpotVideoInsert(spotId4, true.toString()))
            sut.insert(SpotVideoInsert(spotId5, false.toString()))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotVideo/setup.xml")
    @DisplayName("selectByIdのテスト")
    inner class SelectByIdTest : TestBase() {
        val spotVideo1 = spotVideo(1, "00:00:01", "00:00:01", true)
        val spotVideo2 = spotVideo(2, "00:00:02", "00:00:02", true)
        val spotVideo3 = spotVideo(3, "00:00:03", "00:00:03", true)

        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            assertEquals(spotVideo1, sut.selectById(spotVideo1.spotId))
            assertEquals(spotVideo2, sut.selectById(spotVideo2.spotId))
            assertEquals(spotVideo3, sut.selectById(spotVideo3.spotId))
        }

        @Test
        @DisplayName("対象レコードなし")
        fun isNotFound() {
            assertNull(sut.selectById(SpotId(99)))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotVideo/setup_persist.xml")
    @DisplayName("updateのテスト")
    inner class UpdateTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideo/expected_update.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.update(SpotVideoUpdate(spotId1, false.toString()))
            sut.update(SpotVideoUpdate(spotId2, true.toString()))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotVideo/setup_persist.xml")
    @DisplayName("deleteByIdのテスト")
    inner class DeleteByIdTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideo/expected_delete_by_id.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.deleteById(spotId1)
        }
    }

    private fun spotVideo(
        spotId: Int, durationMin: String, durationMax: String, isFixedRotationAspectRatio: Boolean
    ) = SpotVideo(
        SpotId(spotId), LocalTime.parse(durationMin), LocalTime.parse(durationMax), isFixedRotationAspectRatio
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(SpotVideoDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: SpotVideoDaoImpl
    }
}
