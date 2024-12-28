package jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import jp.mangaka.ssp.application.valueobject.dsp.DspId
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
import java.math.BigDecimal

@DisplayName("RelaySpotDspDaoImplのテスト")
private class RelaySpotDspDaoImplTest {
    @Nested
    @DatabaseSetup("/dataset/RelaySpotDsp/setup_persist.xml")
    @DisplayName("bulkInsertのテスト")
    inner class BulkInsertTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/RelaySpotDsp/expected_bulk_insert.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("対象データあり")
        fun isNotEmpty() {
            sut.bulkInsert(
                listOf(
                    relaySpotDspInsert(11, 20, BigDecimal("1.1111111"), 1, BigDecimal("2.2222222")),
                    relaySpotDspInsert(11, 21, BigDecimal("999.9999999"), 255, BigDecimal("9999999999.99999999")),
                    relaySpotDspInsert(12, 21, BigDecimal("123.456"), 3, null)
                )
            )
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/RelaySpotDsp/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("対象データなし")
        fun isEmpty() {
            sut.bulkInsert(emptyList())
        }
    }

    @Nested
    @DatabaseSetup("/dataset/RelaySpotDsp/setup.xml")
    @DisplayName("selectBySpotIdのテスト")
    inner class SelectBySpotId : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val spotId = SpotId(1)

            val actual = sut.selectBySpotId(spotId)

            assertEqualsInAnyOrder(
                listOf(
                    RelaySpotDsp(spotId, DspId(1), BigDecimal("0.0000000"), BigDecimal("9999999999.99999999")),
                    RelaySpotDsp(spotId, DspId(2), BigDecimal("999.9990000"), BigDecimal("0.00000000")),
                    RelaySpotDsp(spotId, DspId(3), BigDecimal("123.4560000"), null)
                ),
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
    @DatabaseSetup("/dataset/RelaySpotDsp/setup_persist.xml")
    @DisplayName("bulkUpdateのテスト")
    inner class BulkUpdateTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/RelaySpotDsp/expected_bulk_update.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("更新成功")
        fun isCorrect() {
            sut.bulkUpdate(
                listOf(
                    relaySpotDspInsert(11, 22, BigDecimal("123.4567890"), 10, BigDecimal("234.5678901")),
                    relaySpotDspInsert(11, 23, BigDecimal("345.6789012"), 20, null)
                )
            )
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/RelaySpotDsp/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("引数のUpdateオブジェクトが空")
        fun isEmptyDsps() {
            sut.bulkUpdate(emptyList())
        }
    }

    @Nested
    @DatabaseSetup("/dataset/RelaySpotDsp/setup_persist.xml")
    @DisplayName("deleteBySpotIdAndDspIdsのテスト")
    inner class DeleteBySpotIdAndDspIdsTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/RelaySpotDsp/expected_delete_by_spot_id_and_dsp_ids.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("削除成功")
        fun isCorrect() {
            sut.deleteBySpotIdAndDspIds(SpotId(11), listOf(22, 23).map { DspId(it) })
        }

        @Test
        @DisplayName("引数のDSPIDリストが空")
        fun isEmptyDspIds() {
            sut.deleteBySpotIdAndDspIds(SpotId(11), emptyList())
        }
    }

    private fun relaySpotDspInsert(
        spotId: Int, dspId: Int, bidAdjust: BigDecimal, priority: Int, floorCpm: BigDecimal?
    ) = RelaySpotDspInsert(SpotId(spotId), DspId(dspId), bidAdjust, priority, floorCpm)

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(RelaySpotDspDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: RelaySpotDspDaoImpl
    }
}
