package jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeElementId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElement.RequiredFlag
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElement.TrimType
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElement.ViewType
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
import java.time.LocalDateTime

@DisplayName("NativeTemplateElementDaoImplのテスト")
private class NativeTemplateElementDaoImplTest {
    val nativeTemplateElement11 = nativeTemplateElement(
        1, 1, 10, 20, 30, 40, "文字色1_1", TrimType.dot, ViewType.minify, RequiredFlag.required,
        "2024-02-01T00:00:00", "2024-01-01T00:00:00"
    )
    val nativeTemplateElement12 = nativeTemplateElement(
        1, 2, null, null, null, null, null, TrimType.chunk, ViewType.minify, RequiredFlag.required,
        "2024-02-02T00:00:00", "2024-01-02T00:00:00"
    )
    val nativeTemplateElement13 = nativeTemplateElement(
        1, 3, null, null, 32, 42, "文字色1_3", TrimType.dot, ViewType.cut, RequiredFlag.required,
        "2024-02-03T00:00:00", "2024-01-03T00:00:00"
    )
    val nativeTemplateElement31 = nativeTemplateElement(
        3, 1, 16, 26, 36, 46, "文字色2_3", TrimType.chunk, ViewType.minify, RequiredFlag.optional,
        "2024-02-06T00:00:00", "2024-01-06T00:00:00"
    )

    @Nested
    @DatabaseSetup("/dataset/NativeTemplateElement/setup.xml")
    @DisplayName("selectByNativeTemplateIdsのテスト")
    inner class SelectByNativeTemplateIdsTest : TestBase() {
        @Test
        @DisplayName("取得成功")
        fun isCorrect() {
            val actual = sut.selectByNativeTemplateIds(listOf(1, 3).map { NativeTemplateId(it) })

            assertEqualsInAnyOrder(
                setOf(
                    nativeTemplateElement11,
                    nativeTemplateElement12,
                    nativeTemplateElement13,
                    nativeTemplateElement31
                ),
                actual
            )
        }

        @Test
        @DisplayName("対象データ0件")
        fun isEmptyResult() {
            val actual = sut.selectByNativeTemplateIds(listOf(NativeTemplateId(99)))

            assertEmpty(actual)
        }

        @Test
        @DisplayName("引数のネイティブテンプレートIDリストが空")
        fun isEmptyNativeTemplateIds() {
            val actual = sut.selectByNativeTemplateIds(emptyList())

            assertEmpty(actual)
        }
    }

    private fun nativeTemplateElement(
        nativeTemplateId: Int, nativeElementId: Int, maxLength: Int?, width: Int?, height: Int?, fontSize: Int?,
        fontColor: String?, trimType: TrimType, viewType: ViewType, requiredFlag: RequiredFlag,
        updateTime: String, createTime: String
    ) = NativeTemplateElement(
        NativeTemplateId(nativeTemplateId), NativeElementId(nativeElementId), maxLength, width, height, fontSize,
        fontColor, trimType, viewType, requiredFlag, LocalDateTime.parse(updateTime), LocalDateTime.parse(createTime)
    )

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(classes = [CompassMasterDbConfig::class])
    @Import(NativeTemplateElementDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @DbUnitConfiguration(databaseConnection = ["CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: NativeTemplateElementDaoImpl
    }
}
