package jp.mangaka.ssp.infrastructure.datasource.dao.site

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import java.time.LocalDateTime
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.proprietydsp.ProprietyDspId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener

@DisplayName("SiteDaoImplのテスト")
private class SiteDaoImplTest {
    val site1 = site(1, 1, "サイト1", SiteStatus.active, true, 1, SiteType.pc_web, 1, "2023-01-01T00:00:00")
    val site2 = site(2, 2, "サイト2", SiteStatus.requested, true, 1, SiteType.sp_web, 2, "2023-01-02T00:00:00")
    val site3 = site(3, 2, "サイト3", SiteStatus.ng, false, 2, SiteType.i_app, 3, "2023-01-03T00:00:00")
    val site4 = site(4, 2, "サイト4", SiteStatus.archive, false, 2, SiteType.a_app, 1, "2023-01-04T00:00:00")

    @Nested
    @DatabaseSetup("/dataset/Site/setup.xml")
    @DisplayName("selectByIdのテスト")
    inner class SelectByIdTest : TestBase() {
        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            assertEquals(site1, sut.selectByIdAndStatuses(site1.siteId, SiteStatus.entries))
            assertEquals(site2, sut.selectByIdAndStatuses(site2.siteId, SiteStatus.entries))
            assertEquals(site3, sut.selectByIdAndStatuses(site3.siteId, SiteStatus.entries))
            assertEquals(site4, sut.selectByIdAndStatuses(site4.siteId, SiteStatus.entries))
        }

        @Test
        @DisplayName("対象レコードなし")
        fun isNotFound() {
            assertNull(sut.selectByIdAndStatuses(SiteId(99), SiteStatus.entries))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/Site/setup.xml")
    @DisplayName("selectByIdAndStatusesのテスト")
    inner class SelectByIdAndStatusesTest : TestBase() {
        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            assertEquals(site1, sut.selectByIdAndStatuses(site1.siteId, listOf(SiteStatus.active, SiteStatus.ng)))
            assertEquals(site3, sut.selectByIdAndStatuses(site3.siteId, listOf(SiteStatus.active, SiteStatus.ng)))
        }

        @Test
        @DisplayName("対象レコードなし")
        fun isNotFound() {
            assertNull(sut.selectByIdAndStatuses(site1.siteId, listOf(SiteStatus.requested)))
            assertNull(sut.selectByIdAndStatuses(SiteId(99), SiteStatus.entries))
        }

        @Test
        @DisplayName("引数のステータスリストが空のとき")
        fun isEmptyStatuses() {
            assertNull(sut.selectByIdAndStatuses(site1.siteId, emptyList()))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/Site/setup.xml")
    @DisplayName("selectByCoAccountIdAndStatusesのテスト")
    inner class SelectByCoAccountIdAndStatusesTest : TestBase() {
        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            // enum値のパターンを網羅するため2回に分けて検証している
            val actual1 = sut.selectByCoAccountIdAndStatuses(
                CoAccountId(1), listOf(SiteStatus.active, SiteStatus.requested)
            )
            val actual2 = sut.selectByCoAccountIdAndStatuses(
                CoAccountId(2), listOf(SiteStatus.requested, SiteStatus.ng, SiteStatus.archive)
            )

            assertEquals(setOf(site1), actual1.toSet())
            assertEquals(setOf(site2, site3, site4), actual2.toSet())
        }

        @Test
        @DisplayName("対象レコードなし")
        fun isNotFound() {
            val actual = sut.selectByCoAccountIdAndStatuses(CoAccountId(99), listOf(SiteStatus.active))

            assertTrue(actual.isEmpty())
        }

        @Test
        @DisplayName("引数のステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectByCoAccountIdAndStatuses(CoAccountId(1), emptyList())

            assertTrue(actual.isEmpty())
        }
    }

    private fun site(
        siteId: Int, coAccountId: Int, siteName: String, siteStatus: SiteStatus, isAllowedMacroUrl: Boolean, platformId: Int,
        siteType: SiteType, defaultProprietyDspId: Int, updateTime: String
    ) = Site(
        SiteId(siteId), CoAccountId(coAccountId), siteName, siteStatus, isAllowedMacroUrl, PlatformId(platformId), siteType,
        ProprietyDspId(defaultProprietyDspId), LocalDateTime.parse(updateTime)
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(SiteDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: SiteDaoImpl
    }
}
