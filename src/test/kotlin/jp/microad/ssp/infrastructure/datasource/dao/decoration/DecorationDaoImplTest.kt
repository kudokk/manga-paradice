package jp.mangaka.ssp.infrastructure.datasource.dao.decoration

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
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

@DisplayName("DecorationDaoImplのテスト")
private class DecorationDaoImplTest {
    val decoration1 = decoration(1, "デコレーション1", 0, 10, "#FFFFFF", "表示文字列1", "#000000")
    val decoration2 = decoration(2, "デコレーション2", 1, 20, "#EEEEEE", "表示文字列2", "#111111")
    val decoration3 = decoration(3, "デコレーション3", 1, 30, "#DDDDDD", "表示文字列3", "#222222")

    @Nested
    @DatabaseSetup("/dataset/Decoration/setup.xml")
    @DisplayName("selectByIdのテスト")
    inner class SelectByIdTest : TestBase() {
        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            assertEquals(decoration2, sut.selectById(decoration2.decorationId))
        }

        @Test
        @DisplayName("対象レコードなし")
        fun isNotFound() {
            assertNull(sut.selectById(DecorationId(99)))
        }
    }

    @Nested
    @DatabaseSetup("/dataset/Decoration/setup.xml")
    @DisplayName("selectByCoAccountIdsのテスト")
    inner class SelectByCoAccountIdsTest : TestBase() {
        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            val actual = sut.selectByCoAccountIds(listOf(0, 1).map { CoAccountId(it) })

            assertEquals(setOf(decoration1, decoration2, decoration3), actual.toSet())
        }

        @Test
        @DisplayName("対象レコードなし")
        fun isNotFound() {
            val actual = sut.selectByCoAccountIds(listOf(CoAccountId(99)))

            assertTrue(actual.isEmpty())
        }

        @Test
        @DisplayName("引数のCoアカウントIDリストが空")
        fun isEmptyCoAccountIds() {
            val actual = sut.selectByCoAccountIds(emptyList())

            assertTrue(actual.isEmpty())
        }
    }

    private fun decoration(
        decorationId: Int, decorationName: String, coAccountId: Int, bandHeight: Int, bandBgcolor: String?,
        bandString: String, bandFontColor: String
    ) = Decoration(
        DecorationId(decorationId), decorationName, CoAccountId(coAccountId), bandHeight, bandBgcolor, bandString,
        bandFontColor
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(DecorationDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: DecorationDaoImpl
    }
}
