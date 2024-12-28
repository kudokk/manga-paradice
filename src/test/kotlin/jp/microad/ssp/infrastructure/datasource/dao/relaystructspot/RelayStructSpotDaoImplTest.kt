package jp.mangaka.ssp.infrastructure.datasource.dao.relaystructspot

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId
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

@DisplayName("RelayStructSpotDaoImplのテスト")
private class RelayStructSpotDaoImplTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
        val spotId99 = SpotId(99)
        val structId1 = StructId(1)
        val structId2 = StructId(2)
        val structId3 = StructId(3)
        val structId4 = StructId(4)
        val structId99 = StructId(99)
    }

    @Nested
    @DatabaseSetup("/dataset/RelayStructSpot/setup.xml")
    @DisplayName("selectBySpotIdのテスト")
    inner class SelectBySpotId : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectBySpotId(spotId1)

            assertEqualsInAnyOrder(
                listOf(
                    RelayStructSpot(structId1, spotId1),
                    RelayStructSpot(structId2, spotId1),
                    RelayStructSpot(structId3, spotId1)
                ),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectBySpotId(spotId99)

            assertEmpty(actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/RelayStructSpot/setup.xml")
    @DisplayName("selectBySpotIds")
    inner class SelectBySpotIdsTest : TestBase() {
        @Test
        @DisplayName("取得成功")
        fun isCorrect() {
            val actual = sut.selectBySpotIds(listOf(spotId1, spotId2))

            assertEqualsInAnyOrder(
                listOf(
                    RelayStructSpot(structId1, spotId1),
                    RelayStructSpot(structId1, spotId2),
                    RelayStructSpot(structId2, spotId1),
                    RelayStructSpot(structId2, spotId2),
                    RelayStructSpot(structId3, spotId1),
                    RelayStructSpot(structId4, spotId2)
                ),
                actual
            )
        }

        @Test
        @DisplayName("取得0件")
        fun isEmptyResult() {
            val actual = sut.selectBySpotIds(listOf(spotId99))

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数の広告枠IDリストが空")
        fun isEmptySpotIds() {
            val actual = sut.selectBySpotIds(emptyList())

            assertEmpty(actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/RelayStructSpot/setup.xml")
    @DisplayName("selectByStructIdsのテスト")
    inner class SelectByStructIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectByStructIds(listOf(structId2, structId3))

            assertEqualsInAnyOrder(
                listOf(
                    RelayStructSpot(structId2, spotId1),
                    RelayStructSpot(structId2, spotId2),
                    RelayStructSpot(structId3, spotId1)
                ),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectByStructIds(listOf(structId99))

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のストラクトIDリストが空")
        fun isEmptyStructIds() {
            val actual = sut.selectByStructIds(emptyList())

            assertEmpty(actual)
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(RelayStructSpotDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: RelayStructSpotDaoImpl
    }
}
