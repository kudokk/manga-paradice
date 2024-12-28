package jp.mangaka.ssp.application.service.targeting.time

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargetingDao
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("TimeTargetingGetWithCheckHelperのテスト")
private class TimeTargetingGetWithCheckHelperTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val timeTargetingId = TimeTargetingId(2)
    }

    val timeTargetingDao: TimeTargetingDao = mock()

    val sut = spy(TimeTargetingGetWithCheckHelper(timeTargetingDao))

    @Nested
    @DisplayName("getTimeTargetingWithCheckのテスト")
    inner class GetTimeTargetingWithCheckTest {
        val statuses = listOf(TimeTargetingStatus.active, TimeTargetingStatus.archive)

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val timeTargeting = timeTargeting(coAccountId)
            doReturn(timeTargeting).whenever(timeTargetingDao).selectByIdAndStatuses(any(), any())

            val actual = sut.getTimeTargetingWithCheck(coAccountId, timeTargetingId, statuses)

            assertEquals(timeTargeting, actual)
            verify(timeTargetingDao, times(1)).selectByIdAndStatuses(timeTargetingId, statuses)
        }

        @Test
        @DisplayName("DBに存在しないとき")
        fun isNotFound() {
            doReturn(null).whenever(timeTargetingDao).selectByIdAndStatuses(any(), any())

            assertThrows<CompassManagerException> {
                sut.getTimeTargetingWithCheck(coAccountId, timeTargetingId, statuses)
            }
        }

        @Test
        @DisplayName("CoアカウントIDが一致しないとき")
        fun isMismatchCoAccount() {
            doReturn(timeTargeting(CoAccountId(99))).whenever(timeTargetingDao).selectByIdAndStatuses(any(), any())

            assertThrows<CompassManagerException> {
                sut.getTimeTargetingWithCheck(coAccountId, timeTargetingId, statuses)
            }
        }

        private fun timeTargeting(coAccountId: CoAccountId): TimeTargeting = mock {
            on { this.coAccountId } doReturn coAccountId
        }
    }
}
