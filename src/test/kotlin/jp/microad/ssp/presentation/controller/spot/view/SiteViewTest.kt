package jp.mangaka.ssp.presentation.controller.spot.view

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SiteViewのテスト")
private class SiteViewTest {
    companion object {
        val siteId1 = SiteId(1)
        val siteId2 = SiteId(2)
        val siteId3 = SiteId(3)
        val platformId1 = PlatformId(1)
        val platformId2 = PlatformId(2)
        val platformId3 = PlatformId(3)
    }

    @Nested
    @DisplayName("ofのテスト")
    inner class OfTest {
        @Test
        @DisplayName("データあり")
        fun isNotEmpty() {
            val actual = SiteView.of(
                listOf(
                    Site(siteId1, mock(), "site1", mock(), true, platformId1, Site.SiteType.pc_web, mock(), mock()),
                    Site(siteId2, mock(), "site2", mock(), true, platformId2, Site.SiteType.i_app, mock(), mock()),
                    Site(siteId3, mock(), "site3", mock(), true, platformId3, Site.SiteType.a_app, mock(), mock())
                )
            )

            assertEquals(
                listOf(
                    SiteView(siteId1, "site1", Site.SiteType.pc_web, platformId1),
                    SiteView(siteId2, "site2", Site.SiteType.i_app, platformId2),
                    SiteView(siteId3, "site3", Site.SiteType.a_app, platformId3)
                ),
                actual
            )
        }

        @Test
        @DisplayName("データなし")
        fun isEmpty() {
            val actual = SiteView.of(emptyList())

            assertTrue(actual.isEmpty())
        }
    }
}
