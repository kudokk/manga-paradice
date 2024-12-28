package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import jp.mangaka.ssp.util.TestUtils.assertEqualsInAnyOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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
import java.time.LocalDate

@DisplayName("SpotVideoFloorCpmDaoImplのテスト")
private class SpotVideoFloorCpmDaoImplTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId3 = SpotId(3)
        val spotId4 = SpotId(4)
        val spotId99 = SpotId(99)
        val aspectRatioId1 = AspectRatioId(1)
        val aspectRatioId2 = AspectRatioId(2)
        val aspectRatioId3 = AspectRatioId(3)
    }

    @Nested
    @DatabaseSetup("/dataset/SpotVideoFloorCpm/setup_persist.xml")
    @DisplayName("insertsのテスト")
    inner class InsertsTest : TestBase() {
        val now = LocalDate.of(2024, 8, 20)

        @BeforeEach
        fun beforeEach() {
            mockkStatic(LocalDate::class)
            every { LocalDate.now() } returns now
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideoFloorCpm/expected_inserts.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.inserts(
                listOf(
                    SpotVideoFloorCpmInsert(spotId3, aspectRatioId1, BigDecimal("0.00000000")),
                    SpotVideoFloorCpmInsert(spotId3, aspectRatioId2, BigDecimal("9999999999.99999999")),
                    SpotVideoFloorCpmInsert(spotId4, aspectRatioId2, BigDecimal("12345.6789"))
                )
            )
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideoFloorCpm/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("引数が空")
        fun isEmptySpotVideoFloorCpms() {
            sut.inserts(emptyList())
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotVideoFloorCpm/setup.xml")
    @DisplayName("selectBySpotIdのテスト")
    inner class SelectBySpotIdTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectBySpotId(spotId1)

            assertEqualsInAnyOrder(
                listOf(
                    SpotVideoFloorCpm(
                        spotId1,
                        aspectRatioId1,
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 11, 1),
                        BigDecimal("0.00000000")
                    ),
                    SpotVideoFloorCpm(
                        spotId1,
                        aspectRatioId2,
                        LocalDate.of(2024, 2, 15),
                        LocalDate.of(2024, 12, 25),
                        BigDecimal("9999999999.99999999")
                    ),
                    SpotVideoFloorCpm(
                        spotId1,
                        aspectRatioId3,
                        LocalDate.of(2024, 3, 30),
                        null,
                        BigDecimal("123.45678000")
                    )
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
    @DatabaseSetup("/dataset/SpotVideoFloorCpm/setup_persist.xml")
    @DisplayName("updatesのテスト")
    inner class UpdatesTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideoFloorCpm/expected_updates.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            listOf(
                SpotVideoFloorCpmUpdate(spotId1, aspectRatioId1, LocalDate.of(2024, 1, 1), BigDecimal("987.654")),
                SpotVideoFloorCpmUpdate(spotId1, aspectRatioId2, LocalDate.of(2024, 2, 1), BigDecimal("876.543")),
            ).let { sut.updates(it) }
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideoFloorCpm/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("引数が空")
        fun isEmptySpotVideoFloorCpms() {
            sut.updates(emptyList())
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotVideoFloorCpm/setup_persist.xml")
    @DisplayName("deleteBySpotIdAndAspectRatioIdsのテスト")
    inner class DeleteBySpotIdAndAspectRatioIdsTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideoFloorCpm/expected_delete_by_spot_id_and_aspect_ratio_ids.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.deleteBySpotIdAndAspectRatioIds(spotId1, listOf(aspectRatioId1, aspectRatioId2))
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotVideoFloorCpm/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("引数のアスペクト比IDリストが空")
        fun isEmptyAspectRatioIds() {
            sut.deleteBySpotIdAndAspectRatioIds(spotId1, emptyList())
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(SpotVideoFloorCpmDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: SpotVideoFloorCpmDaoImpl
    }
}
