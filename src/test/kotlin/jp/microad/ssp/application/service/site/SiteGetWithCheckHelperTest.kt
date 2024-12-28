package jp.mangaka.ssp.application.service.site

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.site.SiteDao
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("SiteGetWithCheckHelper")
private class SiteGetWithCheckHelperTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val siteId = SiteId(2)
    }

    val siteDao: SiteDao = mock()

    val sut = spy(SiteGetWithCheckHelper(siteDao))

    @Nested
    @DisplayName("getSiteWithCheckのテスト")
    inner class GetSiteWithCheckTest {
        val statuses = listOf(SiteStatus.active, SiteStatus.requested)
        val site: Site = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(coAccountId).whenever(site).coAccountId
            doReturn(site).whenever(siteDao).selectByIdAndStatuses(siteId, statuses)

            assertEquals(site, sut.getSiteWithCheck(coAccountId, siteId, statuses))

            verify(siteDao, times(1)).selectByIdAndStatuses(siteId, statuses)
        }

        @Test
        @DisplayName("DBに存在しない")
        fun isNotExistingInDb() {
            doReturn(null).whenever(siteDao).selectByIdAndStatuses(any(), any())

            assertThrows<CompassManagerException> { sut.getSiteWithCheck(coAccountId, siteId, statuses) }

            verify(siteDao, times(1)).selectByIdAndStatuses(siteId, statuses)
        }

        @Test
        @DisplayName("CoアカウントIDが一致しない")
        fun isInvalidCoAccountId() {
            doReturn(CoAccountId(99)).whenever(site).coAccountId
            doReturn(site).whenever(siteDao).selectByIdAndStatuses(any(), any())

            assertThrows<CompassManagerException> { sut.getSiteWithCheck(coAccountId, siteId, statuses) }

            verify(siteDao, times(1)).selectByIdAndStatuses(siteId, statuses)
        }
    }
}
