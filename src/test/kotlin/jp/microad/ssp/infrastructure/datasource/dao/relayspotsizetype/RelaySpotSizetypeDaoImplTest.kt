package jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
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

@DisplayName("RelaySpotSizetypeDaoImplのテスト")
private class RelaySpotSizetypeDaoImplTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
        val spotId3 = SpotId(3)
        val spotId12 = SpotId(12)
        val spotId99 = SpotId(99)
        val sizeTypeId1 = SizeTypeId(1)
        val sizeTypeId2 = SizeTypeId(2)
        val sizeTypeId3 = SizeTypeId(3)
    }

    @Nested
    @DatabaseSetup("/dataset/RelaySpotSizetype/setup_persist.xml")
    @DisplayName("bulkInsertのテスト")
    inner class BulkInsertTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/RelaySpotSizetype/expected_bulk_insert.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("登録レコードあり")
        fun isNotEmpty() {
            sut.bulkInsert(
                listOf(
                    relaySpotSizetypeInsert(10, 21),
                    relaySpotSizetypeInsert(11, 20),
                    relaySpotSizetypeInsert(11, 21)
                )
            )
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/RelaySpotSizetype/setup_persist.xml",
            table = "relay_spot_sizetype",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("登録レコードなし")
        fun isEmpty() {
            sut.bulkInsert(emptyList())
        }
    }

    @Nested
    @DatabaseSetup("/dataset/RelaySpotSizetype/setup.xml")
    @DisplayName("selectBySpotIdのテスト")
    inner class SelectBySpotIdTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectBySpotId(spotId1)

            assertEqualsInAnyOrder(
                listOf(
                    RelaySpotSizetype(spotId1, sizeTypeId1),
                    RelaySpotSizetype(spotId1, sizeTypeId2)
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
    @DatabaseSetup("/dataset/RelaySpotSizetype/setup.xml")
    @DisplayName("selectBySpotIdsのテスト")
    inner class SelectBySpotIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectBySpotIds(listOf(spotId2, spotId3))

            assertEqualsInAnyOrder(
                listOf(
                    RelaySpotSizetype(spotId2, sizeTypeId2),
                    RelaySpotSizetype(spotId3, sizeTypeId1),
                    RelaySpotSizetype(spotId3, sizeTypeId3)
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

    @Nested
    @DatabaseSetup("/dataset/RelaySpotSizetype/setup_persist.xml")
    @DisplayName("deleteBySpotIdAndSizeTypeIdsのテスト")
    inner class DeleteBySpotIdAndSizeTypeIdsTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/RelaySpotSizetype/expected_delete_by_spo_id_and_size_type_ids.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("削除成功")
        fun isCorrect() {
            sut.deleteBySpotIdAndSizeTypeIds(spotId12, listOf(20, 21, 22).map { SizeTypeId(it) })
        }

        @Test
        @DisplayName("引数のサイズ種別IDリストが空")
        fun isEmptySizeTypeIds() {
            sut.deleteBySpotIdAndSizeTypeIds(spotId12, emptyList())
        }
    }

    private fun relaySpotSizetypeInsert(spotId: Int, sizeTypeId: Int) =
        RelaySpotSizetypeInsert(SpotId(spotId), SizeTypeId(sizeTypeId))

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(RelaySpotSizetypeDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: RelaySpotSizetypeDaoImpl
    }
}
