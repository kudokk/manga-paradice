package jp.mangaka.ssp.infrastructure.datasource.dao.relayfixedfloorcpmspot

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.deal.DealId
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

@DisplayName("RelayFixedFloorCpmSpotDaoImplのテスト")
private class RelayFixedFloorCpmSpotDaoImplTest {
    companion object {
        val dealId1 = DealId(1)
        val dealId2 = DealId(2)
        val dealId3 = DealId(3)
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
        val spotId99 = SpotId(99)
    }

    @Nested
    @DatabaseSetup("/dataset/RelayFixedFloorCpmSpot/setup.xml")
    @DisplayName("selectBySpotIdsのテスト")
    inner class SelectBySpotIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectBySpotIds(listOf(spotId1, spotId2))

            assertEqualsInAnyOrder(
                listOf(
                    RelayFixedFloorCpmSpot(dealId1, spotId1),
                    RelayFixedFloorCpmSpot(dealId1, spotId2),
                    RelayFixedFloorCpmSpot(dealId2, spotId1),
                    RelayFixedFloorCpmSpot(dealId2, spotId2),
                    RelayFixedFloorCpmSpot(dealId3, spotId1)
                ),
                actual
            )
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
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

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(RelayFixedFloorCpmSpotDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: RelayFixedFloorCpmSpotDaoImpl
    }
}
