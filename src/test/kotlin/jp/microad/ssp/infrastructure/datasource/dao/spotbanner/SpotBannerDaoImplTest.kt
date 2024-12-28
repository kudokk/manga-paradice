package jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner

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

@DisplayName("SpotBannerDaoImplのテスト")
private class SpotBannerDaoImplTest {
    @Nested
    @DatabaseSetup("/dataset/SpotBanner/setup_persist.xml")
    @DisplayName("insertのテスト")
    inner class InsertTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotBanner/expected_insert.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.insert(SpotBannerInsert(SpotId(2)))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotBanner/setup.xml")
    @DisplayName("selectByIdのテスト")
    inner class SelectByIdTest : TestBase() {
        val spot1 = spotBanner(1, 1)
        val spot2 = spotBanner(2, 2)
        val spot3 = spotBanner(3, 3)

        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            assertEquals(spot1, sut.selectById(spot1.spotId))
            assertEquals(spot2, sut.selectById(spot2.spotId))
            assertEquals(spot3, sut.selectById(spot3.spotId))
        }

        @Test
        @DisplayName("対象レコードなし")
        fun isNotFound() {
            assertNull(sut.selectById(SpotId(99)))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotBanner/setup_persist.xml")
    @DisplayName("deleteByIdのテスト")
    inner class DeleteByIdTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotBanner/expected_delete_by_id.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("削除成功")
        fun isCorrect() {
            sut.deleteById(SpotId(3))
        }
    }

    private fun spotBanner(
        spotId: Int, rotationInterval: Int
    ) = SpotBanner(
        SpotId(spotId), rotationInterval
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(SpotBannerDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: SpotBannerDaoImpl
    }
}
