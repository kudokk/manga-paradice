package jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod.DayType
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
import java.time.LocalDateTime
import java.time.LocalTime

@DisplayName("TimeTargetingDayTypePeriodDaoImplのテスト")
private class TimeTargetingDayTypePeriodDaoImplTest {
    val timeTargetingDayTypePeriod1Mon =
        timeTargetingDayTypePeriod(1, DayType.mon, "01:00:00", "10:10:00", "2024-01-01T00:00:00")
    val timeTargetingDayTypePeriod1Tue =
        timeTargetingDayTypePeriod(1, DayType.tue, "02:05:00", "11:15:00", "2024-01-02T00:00:00")
    val timeTargetingDayTypePeriod1Wed =
        timeTargetingDayTypePeriod(1, DayType.wed, "03:10:00", "12:20:00", "2024-01-03T00:00:00")
    val timeTargetingDayTypePeriod2Thu =
        timeTargetingDayTypePeriod(2, DayType.thu, "04:15:00", "13:25:00", "2024-01-04T00:00:00")
    val timeTargetingDayTypePeriod2Fri =
        timeTargetingDayTypePeriod(2, DayType.fri, "05:20:00", "14:30:00", "2024-01-05T00:00:00")
    val timeTargetingDayTypePeriod2Sat =
        timeTargetingDayTypePeriod(2, DayType.sat, "06:25:00", "15:35:00", "2024-01-06T00:00:00")
    val timeTargetingDayTypePeriod3Sun =
        timeTargetingDayTypePeriod(3, DayType.sun, "07:30:00", "16:40:00", "2024-01-07T00:00:00")
    val timeTargetingDayTypePeriod3Hol =
        timeTargetingDayTypePeriod(3, DayType.hol, "08:35:00", "17:45:00", "2024-01-08T00:00:00")

    @Nested
    @DatabaseSetup("/dataset/TimeTargetingDayTypePeriod/setup_persist.xml")
    @DisplayName("insertsのテスト")
    inner class InsertsTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/TimeTargetingDayTypePeriod/expected_inserts.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("登録成功")
        fun isCorrect() {
            sut.inserts(
                listOf(
                    timeTargetingDayTypePeriodInsert(4, DayType.mon, "01:00:00", "10:10:00"),
                    timeTargetingDayTypePeriodInsert(4, DayType.tue, "02:05:00", "11:15:00"),
                    timeTargetingDayTypePeriodInsert(4, DayType.wed, "03:10:00", "12:20:00"),
                    timeTargetingDayTypePeriodInsert(4, DayType.thu, "04:15:00", "13:25:00"),
                    timeTargetingDayTypePeriodInsert(5, DayType.fri, "05:20:00", "14:30:00"),
                    timeTargetingDayTypePeriodInsert(5, DayType.sat, "06:25:00", "15:35:00"),
                    timeTargetingDayTypePeriodInsert(5, DayType.sun, "07:30:00", "16:50:00"),
                    timeTargetingDayTypePeriodInsert(5, DayType.hol, "08:35:00", "17:55:00")
                )
            )
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/TimeTargetingDayTypePeriod/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("引数のInsertオブジェクトリストが空")
        fun isEmptyInsertObjects() {
            sut.inserts(emptyList())
        }
    }

    @Nested
    @DatabaseSetup("/dataset/TimeTargetingDayTypePeriod/setup_select.xml")
    @DisplayName("selectByIdのテスト")
    inner class SelectByIdTest : TestBase() {
        @Test
        @DisplayName("対象あり")
        fun isFound() {
            val actual = sut.selectById(TimeTargetingId(1))

            assertEqualsInAnyOrder(
                listOf(timeTargetingDayTypePeriod1Mon, timeTargetingDayTypePeriod1Tue, timeTargetingDayTypePeriod1Wed),
                actual
            )
        }

        @Test
        @DisplayName("対象なし")
        fun isNotFound() {
            val actual = sut.selectById(TimeTargetingId(99))

            assertEmpty(actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/TimeTargetingDayTypePeriod/setup_select.xml")
    @DisplayName("selectByIdsのテスト")
    inner class SelectByIdsTest : TestBase() {
        @Test
        @DisplayName("対象あり")
        fun isFound() {
            val actual = sut.selectByIds(listOf(2, 3).map { TimeTargetingId(it) })

            assertEqualsInAnyOrder(
                listOf(
                    timeTargetingDayTypePeriod2Thu,
                    timeTargetingDayTypePeriod2Fri,
                    timeTargetingDayTypePeriod2Sat,
                    timeTargetingDayTypePeriod3Sun,
                    timeTargetingDayTypePeriod3Hol
                ),
                actual
            )
        }

        @Test
        @DisplayName("対象なし")
        fun isNotFound() {
            val actual = sut.selectByIds(listOf(TimeTargetingId(99)))

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のIDリストが空")
        fun isEmptyIds() {
            val actual = sut.selectByIds(emptyList())

            assertEmpty(actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/TimeTargetingDayTypePeriod/setup_persist.xml")
    @DisplayName("deletesのテスト")
    inner class DeletesTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/TimeTargetingDayTypePeriod/expected_deletes.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("削除成功")
        fun isCorrect() {
            sut.deletes(
                listOf(
                    timeTargetingDayTypePeriodDelete(1, DayType.tue, "00:02", "01:02"),
                    timeTargetingDayTypePeriodDelete(1, DayType.wed, "00:03", "01:03"),
                    timeTargetingDayTypePeriodDelete(2, DayType.fri, "00:05", "01:05")
                )
            )
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/TimeTargetingDayTypePeriod/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("削除対象なし")
        fun isNotFound() {
            sut.deletes(
                listOf(
                    timeTargetingDayTypePeriodDelete(99, DayType.mon, "00:00", "01:00")
                )
            )
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/TimeTargetingDayTypePeriod/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("引数の条件リストが空")
        fun isEmptyCondition() {
            sut.deletes(emptyList())
        }
    }

    private fun timeTargetingDayTypePeriod(
        timeTargetingId: Int, dayType: DayType, startTime: String, endTime: String, createTime: String
    ) = TimeTargetingDayTypePeriod(
        TimeTargetingId(timeTargetingId), dayType,
        LocalTime.parse(startTime), LocalTime.parse(endTime), LocalDateTime.parse(createTime)
    )

    private fun timeTargetingDayTypePeriodInsert(
        timeTargetingId: Int, dayType: DayType, startTime: String, endTime: String
    ) = TimeTargetingDayTypePeriodInsert(
        TimeTargetingId(timeTargetingId), dayType, LocalTime.parse(startTime), LocalTime.parse(endTime)
    )

    private fun timeTargetingDayTypePeriodDelete(
        timeTargetingId: Int, dayType: DayType, startTime: String, endTime: String
    ) = TimeTargetingDayTypePeriodDelete(
        TimeTargetingId(timeTargetingId), dayType, LocalTime.parse(startTime), LocalTime.parse(endTime)
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(TimeTargetingDayTypePeriodDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: TimeTargetingDayTypePeriodDaoImpl
    }
}
