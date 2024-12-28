package jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.reqgroup.ReqgroupId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.Reqgroup.ReqgroupStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

@DisplayName("ReqgroupDaoImplのテスト")
private class ReqgroupDaoImplTest {
    @Nested
    @DatabaseSetup("/dataset/Reqgroup/setup.xml")
    @DisplayName("selectReqgroupDspCountryCosByDspIdsAndStatusesのテスト")
    inner class SelectReqgroupDspCountryCosByDspIdsAndStatusesTest : TestBase() {
        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            val actual = sut.selectReqgroupDspCountryCosByDspIdsAndStatuses(
                listOf(10, 11, 12, 13).map { DspId(it) },
                listOf(ReqgroupStatus.active)
            )

            assertEquals(
                setOf(
                    reqgroupDspCountryCo(1, 10, 20),
                    reqgroupDspCountryCo(2, 11, 20),
                    reqgroupDspCountryCo(2, 11, 21),
                    reqgroupDspCountryCo(2, 11, 22)
                ),
                actual.toSet()
            )
        }

        @Test
        @DisplayName("対象レコードなし")
        fun isNotFound() {
            val actual = sut.selectReqgroupDspCountryCosByDspIdsAndStatuses(
                listOf(DspId(999)), ReqgroupStatus.entries
            )

            assertTrue(actual.isEmpty())
        }

        @Test
        @DisplayName("引数のDSPIDリストが空")
        fun isEmptyDspIds() {
            val actual = sut.selectReqgroupDspCountryCosByDspIdsAndStatuses(emptyList(), ReqgroupStatus.entries)

            assertTrue(actual.isEmpty())
        }

        @Test
        @DisplayName("引数のリクエストグループステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectReqgroupDspCountryCosByDspIdsAndStatuses(listOf(DspId(10)), emptyList())

            assertTrue(actual.isEmpty())
        }
    }

    private fun reqgroupDspCountryCo(reqgroupId: Int, dspId: Int, countryId: Int) = ReqgroupDspCountryCo(
        ReqgroupId(reqgroupId), DspId(dspId), CountryId(countryId)
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(ReqgroupDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: ReqgroupDaoImpl
    }
}
