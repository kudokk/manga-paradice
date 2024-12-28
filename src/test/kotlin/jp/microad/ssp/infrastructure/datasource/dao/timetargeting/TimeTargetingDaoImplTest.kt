package jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import jp.mangaka.ssp.util.TestUtils.assertEqualsInAnyOrder
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
import java.time.LocalDateTime

@DisplayName("TimeTargetingDaoImplのテスト")
private class TimeTargetingDaoImplTest {
    val timeTargeting1 = timeTargeting(
        1, 10, "timeTargeting1", TimeTargetingStatus.active, 20, true, "desc1", "2024-02-01T00:00:00"
    )
    val timeTargeting2 = timeTargeting(
        2, 10, "timeTargeting2", TimeTargetingStatus.active, 20, true, null, "2024-02-02T00:00:00"
    )
    val timeTargeting3 = timeTargeting(
        3, 10, "timeTargeting3", TimeTargetingStatus.active, 20, false, "desc3", "2024-02-03T00:00:00"
    )
    val timeTargeting4 = timeTargeting(
        4, 10, "timeTargeting4", TimeTargetingStatus.active, 21, false, null, "2024-02-04T00:00:00"
    )
    val timeTargeting5 = timeTargeting(
        5, 10, "timeTargeting5", TimeTargetingStatus.archive, 21, true, "desc5", "2024-02-05T00:00:00"
    )
    val timeTargeting6 = timeTargeting(
        6, 10, "timeTargeting6", TimeTargetingStatus.archive, 21, true, null, "2024-02-06T00:00:00"
    )
    val timeTargeting7 = timeTargeting(
        7, 10, "timeTargeting7", TimeTargetingStatus.archive, 20, false, "desc7", "2024-02-07T00:00:00"
    )

    @Nested
    @DatabaseSetup("/dataset/TimeTargeting/setup_persist.xml")
    @DisplayName("insertのテスト")
    inner class Insert : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/TimeTargeting/expected_insert.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.insert(timeTargetingInsert(10, "timeTargeting3", 20, true, "desc3"))
            sut.insert(timeTargetingInsert(11, "timeTargeting4", 21, false, null))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/TimeTargeting/setup_select.xml")
    @DisplayName("selectByIdAndStatusesのテスト")
    inner class SelectByIdAndStatusesTest : TestBase() {
        @Test
        @DisplayName("対象あり")
        fun isFound() {
            val actual = sut.selectByIdAndStatuses(
                timeTargeting1.timeTargetingId,
                listOf(TimeTargetingStatus.active, TimeTargetingStatus.archive)
            )

            assertEquals(timeTargeting1, actual)
        }

        @Test
        @DisplayName("ステータス不一致")
        fun isMismatchStatus() {
            val actual = sut.selectByIdAndStatuses(
                timeTargeting1.timeTargetingId,
                listOf(TimeTargetingStatus.archive, TimeTargetingStatus.deleted)
            )

            assertNull(actual)
        }

        @Test
        @DisplayName("対象なし")
        fun isNotFound() {
            val actual = sut.selectByIdAndStatuses(TimeTargetingId(99), TimeTargetingStatus.entries)

            assertNull(actual)
        }

        @Test
        @DisplayName("引数のタイムターゲティングステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectByIdAndStatuses(timeTargeting1.timeTargetingId, emptyList())

            assertNull(actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/TimeTargeting/setup_select.xml")
    @DisplayName("selectByCoAccountIdAndStatusesのテスト")
    inner class SelectByCoAccountIdAndStatusesTest : TestBase() {
        val coAccountId = CoAccountId(10)
        val statuses = listOf(TimeTargetingStatus.active, TimeTargetingStatus.archive)

        @Test
        @DisplayName("対象あり、LIMIT指定なし")
        fun isFoundAndUnlimited() {
            val actual = sut.selectByCoAccountIdAndStatuses(coAccountId, statuses)

            assertEqualsInAnyOrder(
                listOf(
                    timeTargeting1, timeTargeting2, timeTargeting3, timeTargeting4,
                    timeTargeting5, timeTargeting6, timeTargeting7
                ),
                actual
            )
        }

        @Test
        @DisplayName("対象あり、LIMIT指定あり")
        fun isFoundAndLimited() {
            val actual = sut.selectByCoAccountIdAndStatuses(coAccountId, statuses, 4, 2)

            assertEqualsInAnyOrder(
                listOf(timeTargeting3, timeTargeting4, timeTargeting5, timeTargeting6),
                actual
            )
        }

        @Test
        @DisplayName("対象なし")
        fun isNotFound() {
            val actual = sut.selectByCoAccountIdAndStatuses(CoAccountId(99), TimeTargetingStatus.entries)

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のタイムターゲティングステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectByCoAccountIdAndStatuses(coAccountId, emptyList())

            assertEmpty(actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/TimeTargeting/setup_persist.xml")
    @DisplayName("updateのテスト")
    inner class UpdateTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/TimeTargeting/expected_update.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("更新成功")
        fun isCorrect() {
            sut.update(
                timeTargetingUpdate(1, "timeTargeting1Updated", TimeTargetingStatus.archive, 21, false, null)
            )
            sut.update(
                timeTargetingUpdate(2, "timeTargeting2Updated", TimeTargetingStatus.active, 20, true, "desc2")
            )
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/TimeTargeting/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("更新対象なし")
        fun isNotFound() {
            sut.update(
                timeTargetingUpdate(99, "NotFound", TimeTargetingStatus.active, 20, true, "NotFound")
            )
        }
    }

    @Nested
    @DatabaseSetup("/dataset/TimeTargeting/setup_persist.xml")
    @DisplayName("updateStatusのテスト")
    inner class UpdateStatusTet : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/TimeTargeting/expected_update_status.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("更新成功")
        fun isCorrect() {
            sut.updateStatus(timeTargeting1.timeTargetingId, TimeTargetingStatus.deleted)
            sut.updateStatus(timeTargeting2.timeTargetingId, TimeTargetingStatus.active)
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/TimeTargeting/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("更新対象なし")
        fun isNotFound() {
            sut.updateStatus(TimeTargetingId(99), TimeTargetingStatus.deleted)
        }
    }

    private fun timeTargeting(
        timeTargetingId: Int, coAccountId: Int, timeTargetingName: String, timeTargetingStatus: TimeTargetingStatus,
        countryId: Int, isActiveHoliday: Boolean, description: String?, updateTime: String
    ) = TimeTargeting(
        TimeTargetingId(timeTargetingId), CoAccountId(coAccountId), timeTargetingName,
        timeTargetingStatus, CountryId(countryId), isActiveHoliday, description,
        LocalDateTime.parse(updateTime)
    )

    private fun timeTargetingInsert(
        coAccountId: Int, timeTargetingName: String, countryId: Int, isActiveHoliday: Boolean, description: String?
    ) = TimeTargetingInsert(
        CoAccountId(coAccountId), timeTargetingName, CountryId(countryId), isActiveHoliday, description
    )

    private fun timeTargetingUpdate(
        timeTargetingId: Int, timeTargetingName: String, timeTargetingStatus: TimeTargetingStatus,
        countryId: Int, isActiveHoliday: Boolean, description: String?
    ) = TimeTargetingUpdate(
        TimeTargetingId(timeTargetingId), timeTargetingName, timeTargetingStatus,
        CountryId(countryId), isActiveHoliday, description
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(TimeTargetingDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: TimeTargetingDaoImpl
    }
}
