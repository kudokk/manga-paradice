package jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate.AdAttrPosition
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate.NativeTemplateStatus
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import jp.mangaka.ssp.util.TestUtils.assertEqualsInAnyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import java.time.LocalDateTime

@DisplayName("DecorationDaoImplのテスト")
private class NativeTemplateDaoImplTest {
    val nativeTemplate10 = nativeTemplate(
        10, null, "テンプレート10", NativeTemplateStatus.active, 1, "フォントファミリー10", "背景色10", "広告表記10",
        "文字色10", AdAttrPosition.top_left, "ボタン背景色10", "ボタン縁色10", "HTMLコード10", "CSSコード10",
        "2024-02-01T00:00:00", "2024-01-01T00:00:00"
    )
    val nativeTemplate11 = nativeTemplate(
        11, null, "テンプレート11", NativeTemplateStatus.active, 1, "フォントファミリー11", null, "広告表記11",
        "文字色11", AdAttrPosition.top_right, "ボタン背景色11", "ボタン縁色11", "HTMLコード11", "CSSコード11",
        "2024-02-02T00:00:00", "2024-01-02T00:00:00"
    )
    val nativeTemplate12 = nativeTemplate(
        12, null, "テンプレート12", NativeTemplateStatus.active, 2, "フォントファミリー12", "背景色12", "広告表記12",
        "文字色12", AdAttrPosition.bottom_left, "ボタン背景色12", "ボタン縁色12", "HTMLコード12", "CSSコード12",
        "2024-02-03T00:00:00", "2024-01-03T00:00:00"
    )
    val nativeTemplate13 = nativeTemplate(
        13, null, "テンプレート13", NativeTemplateStatus.archive, 2, "フォントファミリー13", "背景色13", "広告表記13",
        "文字色13", AdAttrPosition.bottom_right, "ボタン背景色13", "ボタン縁色13", "HTMLコード13", "CSSコード13",
        "2024-02-04T00:00:00", "2024-01-04T00:00:00"
    )
    val nativeTemplate20 = nativeTemplate(
        20, 1, "テンプレート20", NativeTemplateStatus.active, 1, "フォントファミリー20", "背景色20", "広告表記20",
        "文字色20", AdAttrPosition.bottom_right, "ボタン背景色20", "ボタン縁色20", "HTMLコード20", "CSSコード20",
        "2024-02-01T00:00:00", "2024-01-01T00:00:00"
    )
    val nativeTemplate21 = nativeTemplate(
        21, 1, "テンプレート21", NativeTemplateStatus.active, 2, "フォントファミリー21", null, "広告表記21",
        "文字色21", AdAttrPosition.top_left, "ボタン背景色21", "ボタン縁色21", "HTMLコード21", "CSSコード21",
        "2024-02-02T00:00:00", "2024-01-02T00:00:00"
    )
    val nativeTemplate22 = nativeTemplate(
        22, 1, "テンプレート22", NativeTemplateStatus.active, 2, "フォントファミリー22", "背景色22", "広告表記22",
        "文字色22", AdAttrPosition.top_right, "ボタン背景色22", "ボタン縁色22", "HTMLコード22", "CSSコード22",
        "2024-02-03T00:00:00", "2024-01-03T00:00:00"
    )
    val nativeTemplate23 = nativeTemplate(
        23, 1, "テンプレート23", NativeTemplateStatus.archive, 1, "フォントファミリー23", "背景色23", "広告表記23",
        "文字色23", AdAttrPosition.top_right, "ボタン背景色23", "ボタン縁色23", "HTMLコード23", "CSSコード23",
        "2024-02-04T00:00:00", "2024-01-04T00:00:00"
    )

    @Nested
    @DatabaseSetup("/dataset/NativeTemplate/setup.xml")
    @DisplayName("selectCommonsByStatusesのテスト")
    inner class SelectCommonsByStatusesTest : TestBase() {
        @Test
        @DisplayName("取得成功 - activeのみ")
        fun isCorrectAndStatusActive() {
            val actual = sut.selectCommonsByStatuses(listOf(NativeTemplateStatus.active))

            assertEqualsInAnyOrder(listOf(nativeTemplate10, nativeTemplate11, nativeTemplate12), actual)
        }

        @Test
        @DisplayName("取得成功 - active、archive")
        fun isCorrectAndStatusActiveAndArchive() {
            val actual = sut.selectCommonsByStatuses(setOf(NativeTemplateStatus.active, NativeTemplateStatus.archive))

            assertEqualsInAnyOrder(
                setOf(nativeTemplate10, nativeTemplate11, nativeTemplate12, nativeTemplate13),
                actual
            )
        }

        @Test
        @DisplayName("引数のステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectCommonsByStatuses(emptyList())

            assertEmpty(actual)
        }
    }

    @Nested
    @DatabaseSetup("/dataset/NativeTemplate/setup.xml")
    @DisplayName("selectPersonalsByCoAccountIdAndStatusesのテスト")
    inner class SelectPersonalsByCoAccountIdAndStatusesTest : TestBase() {
        val coAccountId = CoAccountId(1)

        @Test
        @DisplayName("取得成功 - activeのみ")
        fun isCorrectAndStatusActive() {
            val actual = sut.selectPersonalsByCoAccountIdAndStatuses(
                coAccountId,
                listOf(NativeTemplateStatus.active)
            )

            assertEqualsInAnyOrder(listOf(nativeTemplate20, nativeTemplate21, nativeTemplate22), actual)
        }

        @Test
        @DisplayName("取得成功 - active、archive")
        fun isCorrectAndStatusActiveAndArchive() {
            val actual = sut.selectPersonalsByCoAccountIdAndStatuses(
                coAccountId,
                setOf(NativeTemplateStatus.active, NativeTemplateStatus.archive)
            )

            assertEqualsInAnyOrder(
                listOf(nativeTemplate20, nativeTemplate21, nativeTemplate22, nativeTemplate23),
                actual
            )
        }

        @Test
        @DisplayName("対象データ0件")
        fun isEmptyResult() {
            val actual = sut.selectPersonalsByCoAccountIdAndStatuses(
                CoAccountId(99),
                listOf(NativeTemplateStatus.active)
            )

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectPersonalsByCoAccountIdAndStatuses(coAccountId, emptyList())

            assertEmpty(actual)
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DatabaseSetup("/dataset/NativeTemplate/setup.xml")
    @DisplayName("selectByIdAndStatuesのテスト")
    inner class SelectByIdAndStatuesTest : TestBase() {
        @Test
        @DisplayName("取得対象あり - 共通定義テンプレート")
        fun isCorrectAndCommon() {
            val actual = sut.selectByIdAndStatues(
                nativeTemplate10.nativeTemplateId,
                NativeTemplateStatus.entries
            )

            assertEquals(nativeTemplate10, actual)
        }

        @Test
        @DisplayName("取得対象あり - ユーザー定義テンプレート")
        fun isCorrectAndPersonal() {
            val actual = sut.selectByIdAndStatues(
                nativeTemplate20.nativeTemplateId,
                NativeTemplateStatus.entries
            )

            assertEquals(nativeTemplate20, actual)
        }

        @ParameterizedTest
        @MethodSource("notFoundParams")
        @DisplayName("取得対象なし")
        fun isNotFound(nativeTemplateId: NativeTemplateId, statuses: Collection<NativeTemplateStatus>) {
            val actual = sut.selectByIdAndStatues(nativeTemplateId, statuses)

            assertNull(actual)
        }

        private fun notFoundParams() = listOf(
            // 存在しないID
            Arguments.of(NativeTemplateId(99), NativeTemplateStatus.entries),
            // ステータスが不一致
            Arguments.of(nativeTemplate10.nativeTemplateId, setOf(NativeTemplateStatus.archive))
        )

        @Test
        @DisplayName("引数のステータスリストが空")
        fun isEmptyStatuses() {
            val actual = sut.selectByIdAndStatues(nativeTemplate10.nativeTemplateId, emptyList())

            assertNull(actual)
        }
    }

    private fun nativeTemplate(
        nativeTemplateId: Int, coAccountId: Int?, nativeTemplateName: String,
        nativeTemplateStatus: NativeTemplateStatus, platformId: Int, fontFamily: String, bgColor: String?,
        adAttrText: String, adAttrFontColor: String, adAttrPosition: AdAttrPosition, ctaButtonBgColor: String,
        ctaButtonEdgeColor: String, htmlCode: String, cssCode: String, updateTime: String, createTime: String
    ) = NativeTemplate(
        NativeTemplateId(nativeTemplateId), coAccountId?.let { CoAccountId(coAccountId) }, nativeTemplateName,
        nativeTemplateStatus, PlatformId(platformId), fontFamily, bgColor, adAttrText, adAttrFontColor, adAttrPosition,
        ctaButtonBgColor, ctaButtonEdgeColor, htmlCode, cssCode, LocalDateTime.parse(updateTime),
        LocalDateTime.parse(createTime)
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(NativeTemplateDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: NativeTemplateDaoImpl
    }
}
