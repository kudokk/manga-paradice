package jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
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

@DisplayName("DspMasterDaoImplのテスト")
private class DspMasterDaoImplTest {
    val dsp1 = DspMaster(DspId(1), "DSP1")
    val dsp2 = DspMaster(DspId(2), "DSP2")
    val dsp3 = DspMaster(DspId(3), "DSP3")

    @Nested
    @DatabaseSetup("/dataset/DspMaster/setup.xml")
    @DisplayName("selectAllのテスト")
    inner class SelectAllTest : TestBase() {
        @Test
        @DisplayName("正常")
        fun isFound() {
            val actual = sut.selectAll()

            assertEquals(setOf(dsp1, dsp2, dsp3), actual.toSet())
        }
    }

    @Nested
    @DatabaseSetup("/dataset/DspMaster/setup.xml")
    @DisplayName("selectByIdsのテスト")
    inner class SelectByIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectByIds(listOf(dsp1.dspId, dsp2.dspId, DspId(98), DspId(99)))

            assertEquals(setOf(dsp1, dsp2), actual.toSet())
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectByIds(listOf(DspId(99999)))

            assertTrue(actual.isEmpty())
        }

        @Test
        @DisplayName("引数のDSPIDリストが空")
        fun isEmptyDspIds() {
            val actual = sut.selectByIds(emptyList())

            assertTrue(actual.isEmpty())
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(DspMasterDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: DspMasterDaoImpl
    }
}
