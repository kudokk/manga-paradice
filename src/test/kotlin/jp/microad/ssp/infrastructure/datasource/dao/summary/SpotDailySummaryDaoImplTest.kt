package jp.mangaka.ssp.infrastructure.datasource.dao.summary

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassSummaryDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.condition.SpotSummaryCondition
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.SpotRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.TotalRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.SpotSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.TotalSummaryCo
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import jp.mangaka.ssp.util.TestUtils.assertEqualsInAnyOrder
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
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@DisplayName("SpotDailySummaryDaoImplのテスト")
private class SpotDailySummaryDaoImplTest {
    companion object {
        val coAccountId10 = CoAccountId(10)
        val spotId20 = SpotId(20)
        val spotId21 = SpotId(21)
        val spotId29 = SpotId(29)
        val structId30 = StructId(30)
        val structId31 = StructId(31)
        val date20241001 = LocalDate.of(2024, 10, 1)!!
        val date20241030 = LocalDate.of(2024, 10, 30)!!
    }

    @Nested
    @DatabaseSetup("/dataset/Summary/SpotDailySummary/setup.xml")
    @DisplayName("selectTotalRtbSummaryByConditionのテスト")
    inner class SelectTotalRtbSpotSummaryByConditionTest : TestBase() {
        @Test
        @DisplayName("取得成功")
        fun isCorrect() {
            val actual = sut.selectTotalRtbSpotSummaryByCondition(
                spotSummaryCondition(listOf(spotId20, spotId21, spotId29), date20241001, date20241030)
            )

            assertEquals(TotalSummaryCo(250, 240, BigDecimal("233.33333331")), actual)
        }

        @Test
        @DisplayName("対象0件")
        fun isEmptyResult() {
            val actual = sut.selectTotalRtbSpotSummaryByCondition(
                spotSummaryCondition(listOf(spotId29), date20241001, date20241030)
            )

            assertEquals(TotalSummaryCo.zero, actual)
        }

        // 共通関数のテストなので以降の関数のテストでは実施しない
        @Test
        @DisplayName("引数の広告枠IDリストが空")
        fun isEmptySpotIds() {
            val actual = sut.selectTotalRtbSpotSummaryByCondition(
                spotSummaryCondition(emptyList(), date20241001, date20241030)
            )

            assertEquals(TotalSummaryCo.zero, actual)
        }

        private fun spotSummaryCondition(
            spotIds: Collection<SpotId>, startDate: LocalDate, endDate: LocalDate
        ) = SpotSummaryCondition(
            coAccountId10, spotIds, emptyList(), emptyList(), emptyList(), startDate, endDate
        )
    }

    @Nested
    @DatabaseSetup("/dataset/Summary/SpotDailySummary/setup.xml")
    @DisplayName("selectSpotRtbSummaryByConditionのテスト")
    inner class SelectSpotRtbSpotSummaryByConditionTest : TestBase() {
        @Test
        @DisplayName("取得成功")
        fun isCorrect() {
            val actual = sut.selectSpotRtbSpotSummaryByCondition(
                spotSummaryCondition(listOf(spotId20, spotId21, spotId29), date20241001, date20241030)
            )

            assertEqualsInAnyOrder(
                listOf(
                    SpotSummaryCo(spotId20, 90, 130, BigDecimal("133.33333332")),
                    SpotSummaryCo(spotId21, 160, 110, BigDecimal("99.99999999"))
                ),
                actual
            )
        }

        @Test
        @DisplayName("対象0件")
        fun isEmptyResult() {
            val actual = sut.selectSpotRtbSpotSummaryByCondition(
                spotSummaryCondition(listOf(spotId29), date20241001, date20241030)
            )

            assertEmpty(actual)
        }

        // 共通関数のテストなので以降の関数のテストでは実施しない
        @Test
        @DisplayName("引数の広告枠IDリストが空")
        fun isEmptySpotIds() {
            val actual = sut.selectSpotRtbSpotSummaryByCondition(
                spotSummaryCondition(emptyList(), date20241001, date20241030)
            )

            assertEmpty(actual)
        }

        private fun spotSummaryCondition(
            spotIds: Collection<SpotId>, startDate: LocalDate, endDate: LocalDate
        ) = SpotSummaryCondition(
            coAccountId10, spotIds, emptyList(), emptyList(), emptyList(), startDate, endDate
        )
    }

    @Nested
    @DatabaseSetup("/dataset/Summary/SpotDailySummary/setup.xml")
    @DisplayName("selectTotalRtbAndStructSpotSummaryByConditionのテスト")
    inner class SelectTotalRtbAndStructSpotSummaryByConditionTest : TestBase() {
        @Test
        @DisplayName("取得成功")
        fun isCorrect() {
            val actual = sut.selectTotalRtbAndStructSpotSummaryByCondition(
                spotSummaryCondition(
                    listOf(spotId20, spotId21, spotId29), listOf(structId30, structId31),
                    date20241001, date20241030
                )
            )

            assertEquals(TotalSummaryCo(1260, 1140, BigDecimal("1455.55555541")), actual)
        }

        @Test
        @DisplayName("対象0件")
        fun isEmptyResult() {
            val actual = sut.selectTotalRtbAndStructSpotSummaryByCondition(
                spotSummaryCondition(
                    listOf(spotId29), listOf(structId30, structId31), date20241001, date20241030
                )
            )

            assertEquals(TotalSummaryCo.zero, actual)
        }

        // 共通関数のテストなので以降の関数のテストでは実施しない
        @Test
        @DisplayName("引数の広告枠IDリストが空")
        fun isEmptySpotIds() {
            val actual = sut.selectTotalRtbAndStructSpotSummaryByCondition(
                spotSummaryCondition(
                    emptyList(), listOf(structId30, structId31), date20241001, date20241030
                )
            )

            assertEquals(TotalSummaryCo.zero, actual)
        }

        private fun spotSummaryCondition(
            spotIds: Collection<SpotId>,
            notResellerStructIds: Collection<StructId>,
            startDate: LocalDate,
            endDate: LocalDate
        ) = SpotSummaryCondition(
            coAccountId10, spotIds, emptyList(), notResellerStructIds, emptyList(),
            startDate, endDate
        )
    }

    @Nested
    @DatabaseSetup("/dataset/Summary/SpotDailySummary/setup.xml")
    @DisplayName("selectSpotRtbAndStructSpotSummaryByConditionのテスト")
    inner class SelectSpotRtbAndStructSpotSummaryByConditionTest : TestBase() {
        @Test
        @DisplayName("取得成功")
        fun isCorrect() {
            val actual = sut.selectSpotRtbAndStructSpotSummaryByCondition(
                spotSummaryCondition(
                    listOf(spotId20, spotId21, spotId29), listOf(structId30, structId31),
                    date20241001, date20241030
                )
            )

            assertEquals(
                listOf(
                    SpotSummaryCo(spotId20, 550, 620, BigDecimal("799.99999992")),
                    SpotSummaryCo(spotId21, 710, 520, BigDecimal("655.55555549"))
                ),
                actual
            )
        }

        @Test
        @DisplayName("対象0件")
        fun isEmptyResult() {
            val actual = sut.selectSpotRtbAndStructSpotSummaryByCondition(
                spotSummaryCondition(
                    listOf(spotId29), listOf(structId30, structId31), date20241001, date20241030
                )
            )

            assertEmpty(actual)
        }

        // 共通関数のテストなので以降の関数のテストでは実施しない
        @Test
        @DisplayName("引数の広告枠IDリストが空")
        fun isEmptySpotIds() {
            val actual = sut.selectSpotRtbAndStructSpotSummaryByCondition(
                spotSummaryCondition(
                    emptyList(), listOf(structId30, structId31), date20241001, date20241030
                )
            )

            assertEmpty(actual)
        }

        private fun spotSummaryCondition(
            spotIds: Collection<SpotId>,
            notResellerStructIds: Collection<StructId>,
            startDate: LocalDate,
            endDate: LocalDate
        ) = SpotSummaryCondition(
            coAccountId10, spotIds, emptyList(), notResellerStructIds, emptyList(),
            startDate, endDate
        )
    }

    @Nested
    @DatabaseSetup("/dataset/Summary/SpotDailySummary/setup.xml")
    @DisplayName("selectTotalSpotRequestSummaryByConditionのテスト")
    inner class SelectTotalSpotRequestSummaryByConditionTest : TestBase() {
        @Test
        @DisplayName("広告枠-ストラクト紐づきなし")
        fun isEmptyRelaySpotStructIds() {
            val actual = sut.selectTotalSpotRequestSummaryByCondition(
                spotSummaryCondition(listOf(spotId20, spotId21, spotId29), emptyList(), date20241001, date20241030)
            )

            assertEquals(TotalRequestSummaryCo(1430), actual)
        }

        @Test
        @DisplayName("広告枠-ストラクト紐づきあり")
        fun isNotRelaySpotStructIds() {
            val actual = sut.selectTotalSpotRequestSummaryByCondition(
                spotSummaryCondition(
                    listOf(spotId20, spotId21, spotId29), listOf(structId30, structId31), date20241001, date20241030
                )
            )

            assertEquals(TotalRequestSummaryCo(790), actual)
        }

        @Test
        @DisplayName("対象0件")
        fun isEmptyResult() {
            val actual = sut.selectTotalSpotRequestSummaryByCondition(
                spotSummaryCondition(listOf(spotId29), listOf(structId30, structId31), date20241001, date20241030)
            )

            assertEquals(TotalRequestSummaryCo.zero, actual)
        }

        // 共通関数のテストなので以降の関数のテストでは実施しない
        @Test
        @DisplayName("引数の広告枠IDリストが空")
        fun isEmptySpotIds() {
            val actual = sut.selectTotalSpotRequestSummaryByCondition(
                spotSummaryCondition(listOf(spotId29), listOf(structId30, structId31), date20241001, date20241030)
            )

            assertEquals(TotalRequestSummaryCo.zero, actual)
        }

        private fun spotSummaryCondition(
            spotIds: Collection<SpotId>,
            relaySpotStructIds: Collection<StructId>,
            startDate: LocalDate,
            endDate: LocalDate
        ) = SpotSummaryCondition(
            coAccountId10, spotIds, emptyList(), emptyList(), relaySpotStructIds, startDate, endDate
        )
    }

    @Nested
    @DatabaseSetup("/dataset/Summary/SpotDailySummary/setup.xml")
    @DisplayName("selectSpotRequestSummaryByConditionのテスト")
    inner class SelectSpotRequestSummaryByConditionTest : TestBase() {
        @Test
        @DisplayName("広告枠-ストラクト紐づきなし")
        fun isEmptyRelaySpotStructIds() {
            val actual = sut.selectSpotRequestSummaryByCondition(
                spotSummaryCondition(listOf(spotId20, spotId21, spotId29), emptyList(), date20241001, date20241030)
            )

            assertEquals(
                listOf(
                    SpotRequestSummaryCo(spotId20, 550),
                    SpotRequestSummaryCo(spotId21, 880)
                ),
                actual
            )
        }

        @Test
        @DisplayName("広告枠-ストラクト紐づきあり")
        fun isNotRelaySpotStructIds() {
            val actual = sut.selectSpotRequestSummaryByCondition(
                spotSummaryCondition(
                    listOf(spotId20, spotId21, spotId29), listOf(structId30, structId31), date20241001, date20241030
                )
            )

            assertEqualsInAnyOrder(
                listOf(
                    SpotRequestSummaryCo(spotId20, 320),
                    SpotRequestSummaryCo(spotId21, 470)
                ),
                actual
            )
        }

        @Test
        @DisplayName("対象0件")
        fun isEmptyResult() {
            val actual = sut.selectSpotRequestSummaryByCondition(
                spotSummaryCondition(listOf(spotId29), listOf(structId30, structId31), date20241001, date20241030)
            )

            assertEmpty(actual)
        }

        // 共通関数のテストなので以降の関数のテストでは実施しない
        @Test
        @DisplayName("引数の広告枠IDリストが空")
        fun isEmptySpotIds() {
            val actual = sut.selectTotalSpotRequestSummaryByCondition(
                spotSummaryCondition(listOf(spotId29), listOf(structId30, structId31), date20241001, date20241030)
            )

            assertEquals(TotalRequestSummaryCo.zero, actual)
        }

        private fun spotSummaryCondition(
            spotIds: Collection<SpotId>,
            relaySpotStructIds: Collection<StructId>,
            startDate: LocalDate,
            endDate: LocalDate
        ) = SpotSummaryCondition(
            coAccountId10, spotIds, emptyList(), emptyList(), relaySpotStructIds, startDate, endDate
        )
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassSummaryDbConfig::class])
    @Import(SpotDailySummaryDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @Transactional("CompassSummaryTX")
    @DbUnitConfiguration(databaseConnection = ["CompassSummaryDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: SpotDailySummaryDaoImpl
    }
}
