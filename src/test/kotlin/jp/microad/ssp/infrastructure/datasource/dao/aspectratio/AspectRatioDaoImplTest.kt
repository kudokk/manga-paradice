package jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio.AspectRatioStatus
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

@DisplayName("AspectRatioDaoImplのテスト")
private class AspectRatioDaoImplTest {
    val aspectRatio1 = AspectRatio(AspectRatioId(1), 16, 9, AspectRatioStatus.active)
    val aspectRatio2 = AspectRatio(AspectRatioId(2), 9, 16, AspectRatioStatus.active)
    val aspectRatio3 = AspectRatio(AspectRatioId(3), 16, 5, AspectRatioStatus.active)
    val aspectRatio4 = AspectRatio(AspectRatioId(4), 32, 5, AspectRatioStatus.stop)
    val aspectRatio5 = AspectRatio(AspectRatioId(5), 1, 1, AspectRatioStatus.stop)

    @Nested
    @DatabaseSetup("/dataset/AspectRatio/setup.xml")
    @DisplayName("selectByStatusesのテスト")
    inner class SelectByStatusesTest : TestBase() {
        @Test
        @DisplayName("ステータスが１つ")
        fun isSingleStatus() {
            val actual = sut.selectByStatuses(listOf(AspectRatioStatus.stop))

            assertEqualsInAnyOrder(listOf(aspectRatio4, aspectRatio5), actual)
        }

        @Test
        @DisplayName("ステータスが複数")
        fun isMultipleStatus() {
            val actual = sut.selectByStatuses(listOf(AspectRatioStatus.active, AspectRatioStatus.stop))

            assertEqualsInAnyOrder(
                listOf(aspectRatio1, aspectRatio2, aspectRatio3, aspectRatio4, aspectRatio5),
                actual
            )
        }

        @Test
        @DisplayName("ステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectByStatuses(emptyList())

            assertEmpty(actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/AspectRatio/setup.xml")
    @DisplayName("selectByAspectRatioIdsAndStatusesのテスト")
    inner class SelectByAspectRatioIdsAndStatusesTest : TestBase() {
        @Test
        @DisplayName("正常 - IDによる絞り込み")
        fun isCorrectAndFilterById() {
            val actual = sut.selectByAspectRatioIdsAndStatuses(
                listOf(aspectRatio1, aspectRatio2, aspectRatio4).map { it.aspectRatioId },
                listOf(AspectRatioStatus.active, AspectRatioStatus.stop)
            )

            assertEqualsInAnyOrder(
                listOf(aspectRatio1, aspectRatio2, aspectRatio4),
                actual
            )
        }

        @Test
        @DisplayName("正常 - ステータスによる絞り込み")
        fun isCorrectAndFilterByStatus() {
            val actual = sut.selectByAspectRatioIdsAndStatuses(
                listOf(aspectRatio1, aspectRatio2, aspectRatio3, aspectRatio4, aspectRatio5).map { it.aspectRatioId },
                listOf(AspectRatioStatus.active)
            )

            assertEqualsInAnyOrder(
                listOf(aspectRatio1, aspectRatio2, aspectRatio3),
                actual
            )
        }

        @Test
        @DisplayName("IDリストが空")
        fun isEmptyAspectRatioIds() {
            val actual = sut.selectByAspectRatioIdsAndStatuses(emptyList(), listOf(AspectRatioStatus.active))

            assertEmpty(actual)
        }

        @Test
        @DisplayName("ステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectByAspectRatioIdsAndStatuses(listOf(aspectRatio1.aspectRatioId), emptyList())

            assertEmpty(actual)
        }
    }

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(AspectRatioDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: AspectRatioDaoImpl
    }
}
