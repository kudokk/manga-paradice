package jp.mangaka.ssp.infrastructure.datasource.dao.spotnative

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.spot.ContextSubTypeId
import jp.mangaka.ssp.application.valueobject.spot.ContextTypeId
import jp.mangaka.ssp.application.valueobject.spot.PlacementTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
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
import java.time.LocalTime

@DisplayName("SpotNativeDaoImplのテスト")
private class SpotNativeDaoImplTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId4 = SpotId(4)
        val spotId99 = SpotId(99)
        val nativeTemplateId5 = NativeTemplateId(5)
    }

    @Nested
    @DatabaseSetup("/dataset/SpotNative/setup_persist.xml")
    @DisplayName("insertのテスト")
    inner class InsertTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotNative/expected_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.insert(SpotNativeInsert(spotId4, nativeTemplateId5))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotNative/setup.xml")
    @DisplayName("selectByIdのテスト")
    inner class SelectByIdTest : TestBase() {
        val spotNative1 = spotNative(
            1, 1, ContextTypeId(1), ContextSubTypeId(1), PlacementTypeId(1),
            1, 1, "00:00:01", "00:00:01"
        )
        val spotNative2 = spotNative(
            2, 2, ContextTypeId(2), ContextSubTypeId(2), PlacementTypeId(2),
            2, 2, "00:00:02", "00:00:02"
        )
        val spotNative3 = spotNative(
            3, 3, ContextTypeId(3), ContextSubTypeId(3), PlacementTypeId(3),
            3, 3, "00:00:03", "00:00:03"
        )

        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            assertEquals(spotNative1, sut.selectById(spotNative1.spotId))
            assertEquals(spotNative2, sut.selectById(spotNative2.spotId))
            assertEquals(spotNative3, sut.selectById(spotNative3.spotId))
        }

        @Test
        @DisplayName("対象レコードなし")
        fun isNotFound() {
            assertNull(sut.selectById(spotId99))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotNative/setup_persist.xml")
    @DisplayName("updateのテスト")
    inner class UpdateTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotNative/expected_update.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.update(SpotNativeUpdate(SpotId(1), NativeTemplateId(5)))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/SpotNative/setup_persist.xml")
    @DisplayName("deleteByIdのテスト")
    inner class DeleteByIdTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SpotNative/expected_delete_by_id.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
        )
        @DisplayName("正常")
        fun isCorrect() {
            sut.deleteById(spotId1)
        }
    }

    private fun spotNative(
        spotId: Int,
        nativeTemplateId: Int,
        contextTypeId: ContextTypeId,
        contextSubTypeId: ContextSubTypeId,
        placementTypeId: PlacementTypeId,
        placementCount: Int,
        rotationInterval: Int,
        durationMin: String,
        durationMax: String
    ) = SpotNative(
        SpotId(spotId), NativeTemplateId(nativeTemplateId), contextTypeId, contextSubTypeId, placementTypeId,
        placementCount, rotationInterval, LocalTime.parse(durationMin), LocalTime.parse(durationMax)
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(SpotNativeDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: SpotNativeDaoImpl
    }
}
