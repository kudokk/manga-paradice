package jp.mangaka.ssp.infrastructure.datasource.dao.compassstruct

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
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

@DisplayName("CompassStructDaoImplのテスト")
private class CompassStructDaoImplTest {
    @Nested
    @DatabaseSetup("/dataset/CompassStruct/setup_persist.xml")
    @DisplayName("updateTimeTargetingIdのテスト")
    inner class UpdateTimeTargetingIdTest : TestBase() {
        val timeTargetingId = TimeTargetingId(12)

        @Test
        @ExpectedDatabase(
            value = "/dataset/CompassStruct/expected_update_time_targeting_id.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("更新成功")
        fun isCorrectAndNotNullTimeTargetingId() {
            sut.updateTimeTargetingId(structIds(1, 2), timeTargetingId)
            sut.updateTimeTargetingId(structIds(3, 4), null)
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/CompassStruct/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("対象なし")
        fun isNotFound() {
            sut.updateTimeTargetingId(structIds(99), timeTargetingId)
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/CompassStruct/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("引数のストラクトIDリストが空")
        fun isEmptyStructIds() {
            sut.updateTimeTargetingId(emptyList(), timeTargetingId)
        }
    }

    private fun structIds(vararg ids: Int) = ids.map { StructId(it) }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(CompassStructDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: CompassStructDaoImpl
    }
}
