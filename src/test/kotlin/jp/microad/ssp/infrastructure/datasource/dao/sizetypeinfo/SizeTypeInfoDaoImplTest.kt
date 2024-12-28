package jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseSetups
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.annotation.ExpectedDatabase
import com.github.springtestdbunit.assertion.DatabaseAssertionMode
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.config.CoreMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.dao.AbstractDaoTest
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo.DefinitionType
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import jp.mangaka.ssp.util.TestUtils.assertEqualsInAnyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
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
import org.springframework.transaction.annotation.Transactional

@DisplayName("SizeTypeInfoDaoImplのテスト")
private class SizeTypeInfoDaoImplTest {
    companion object {
        val coAccountId1 = CoAccountId(1)
        val coAccountId99 = CoAccountId(99)
    }

    val sizeTypeInfo10 = sizeTypeInfo(10, 100, 110, 1, DefinitionType.standard)
    val sizeTypeInfo11 = sizeTypeInfo(11, 101, 111, 1, DefinitionType.standard)
    val sizeTypeInfo12 = sizeTypeInfo(12, 102, 112, 2, DefinitionType.standard)
    val sizeTypeInfo20 = sizeTypeInfo(20, 200, 210, 1, DefinitionType.userdefined)
    val sizeTypeInfo21 = sizeTypeInfo(21, 201, 211, 1, DefinitionType.userdefined)
    val sizeTypeInfo22 = sizeTypeInfo(22, 202, 212, 2, DefinitionType.userdefined)
    val sizeTypeInfo23 = sizeTypeInfo(23, 203, 213, 2, DefinitionType.userdefined)

    @Nested
    @DatabaseSetup(value = ["/dataset/SizeTypeInfo/setup_core_master.xml"], connection = "CoreMasterDS")
    @DisplayName("selectStandardsのテスト")
    inner class SelectStandardsTest : TestBase() {
        @Test
        @DisplayName("取得成功")
        fun isFound() {
            val actual = sut.selectStandards()

            assertEquals(setOf(sizeTypeInfo10, sizeTypeInfo11, sizeTypeInfo12), actual.toSet())
        }
    }

    @Nested
    @DatabaseSetup(value = ["/dataset/SizeTypeInfo/setup_core_master.xml"], connection = "CoreMasterDS")
    @DisplayName("selectUserDefinedsByCoAccountIdのテスト")
    inner class SelectUserDefinedsByCoAccountIdTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            doReturn(listOf(10, 20, 21)).whenever(spyOfSut).selectSizeTypeIdsUsingCreativeByCoAccountId(any())
            doReturn(listOf(22, 29)).whenever(spyOfSut).selectSizeTypeIdsUsingSpotByCoAccountId(any())

            val actual = spyOfSut.selectUserDefinedsByCoAccountId(coAccountId1)

            assertEquals(setOf(sizeTypeInfo20, sizeTypeInfo21, sizeTypeInfo22), actual.toSet())

            verify(spyOfSut, times(1)).selectSizeTypeIdsUsingCreativeByCoAccountId(coAccountId1)
            verify(spyOfSut, times(1)).selectSizeTypeIdsUsingSpotByCoAccountId(coAccountId1)
        }

        @Test
        @DisplayName("Coアカウントに紐づくユーザー定義のサイズが存在しない")
        fun isNotFoundCoAccountUserDefinedSize() {
            doReturn(listOf(10)).whenever(spyOfSut).selectSizeTypeIdsUsingCreativeByCoAccountId(any())
            doReturn(listOf(11)).whenever(spyOfSut).selectSizeTypeIdsUsingSpotByCoAccountId(any())

            val actual = spyOfSut.selectUserDefinedsByCoAccountId(coAccountId1)

            assertTrue(actual.isEmpty())

            verify(spyOfSut, times(1)).selectSizeTypeIdsUsingCreativeByCoAccountId(coAccountId1)
            verify(spyOfSut, times(1)).selectSizeTypeIdsUsingSpotByCoAccountId(coAccountId1)
        }

        @Test
        @DisplayName("Coアカウントに紐づくサイズが存在しない")
        fun isNotFoundCoAccountSize() {
            doReturn(emptyList<Int>()).whenever(spyOfSut).selectSizeTypeIdsUsingCreativeByCoAccountId(any())
            doReturn(emptyList<Int>()).whenever(spyOfSut).selectSizeTypeIdsUsingSpotByCoAccountId(any())

            val actual = spyOfSut.selectUserDefinedsByCoAccountId(coAccountId1)

            assertTrue(actual.isEmpty())

            verify(spyOfSut, times(1)).selectSizeTypeIdsUsingCreativeByCoAccountId(coAccountId1)
            verify(spyOfSut, times(1)).selectSizeTypeIdsUsingSpotByCoAccountId(coAccountId1)
        }
    }

    @Nested
    @DatabaseSetup(value = ["/dataset/SizeTypeInfo/setup_core_master.xml"], connection = "CoreMasterDS")
    @DisplayName("selectSizeTypeIdsUsingCreativeByCoAccountIdのテスト")
    inner class SelectSizeTypeIdsUsingCreativeByCoAccountIdTest : TestBase() {
        @Test
        @DisplayName("対象レコードあり")
        fun isFound() {
            val actual = sut.selectSizeTypeIdsUsingCreativeByCoAccountId(coAccountId1)

            assertEquals(setOf(20, 21, 22), actual.toSet())
        }

        @Test
        @DisplayName("対象レコードなし")
        fun isNotFound() {
            val actual = sut.selectSizeTypeIdsUsingCreativeByCoAccountId(coAccountId99)

            assertTrue(actual.isEmpty())
        }
    }

    @Nested
    @DatabaseSetup(value = ["/dataset/SizeTypeInfo/setup_compass_master.xml"], connection = "CompassMasterDS")
    @DisplayName("selectSizeTypeIdsUsingSpotByCoAccountIdのテスト")
    inner class SelectSizeTypeIdsUsingSpotByCoAccountIdTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectSizeTypeIdsUsingSpotByCoAccountId(coAccountId1)

            assertEquals(setOf(20, 21, 22, 23), actual.toSet())
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectSizeTypeIdsUsingSpotByCoAccountId(coAccountId99)

            assertTrue(actual.isEmpty())
        }
    }

    @Nested
    @DatabaseSetups(
        DatabaseSetup(value = ["/dataset/SizeTypeInfo/setup_core_master.xml"], connection = "CoreMasterDS"),
        DatabaseSetup(value = ["/dataset/SizeTypeInfo/setup_compass_master.xml"], connection = "CompassMasterDS")
    )
    @DisplayName("selectBySpotIdのテスト")
    inner class SelectBySpotIdTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectBySpotId(SpotId(42))

            assertEquals(setOf(sizeTypeInfo22, sizeTypeInfo23), actual.toSet())
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectBySpotId(SpotId(999))

            assertTrue(actual.isEmpty())
        }
    }

    @Nested
    @DatabaseSetup(value = ["/dataset/SizeTypeInfo/setup_core_master.xml"], connection = "CoreMasterDS")
    @DisplayName("selectBySpotIdのテスト")
    inner class SelectByIdsTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectByIds(listOf(10, 20, 19, 99).map { SizeTypeId(it) })

            assertEqualsInAnyOrder(listOf(sizeTypeInfo10, sizeTypeInfo20), actual)
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectByIds(listOf(SizeTypeId(999)))

            assertEmpty(actual)
        }
    }

    @Nested
    @DatabaseSetup(value = ["/dataset/SizeTypeInfo/setup_core_master.xml"], connection = "CoreMasterDS")
    @DisplayName("selectByPlatformIdのテスト")
    inner class SelectByPlatformIdTest : TestBase() {
        @Test
        @DisplayName("対象データあり")
        fun isFound() {
            val actual = sut.selectByPlatformId(PlatformId.pc)

            assertEquals(setOf(sizeTypeInfo10, sizeTypeInfo11, sizeTypeInfo20, sizeTypeInfo21), actual.toSet())
        }

        @Test
        @DisplayName("対象データなし")
        fun isNotFound() {
            val actual = sut.selectByPlatformId(PlatformId(999))

            assertTrue(actual.isEmpty())
        }
    }

    @Nested
    @DatabaseSetup(value = ["/dataset/SizeTypeInfo/setup_persist.xml"], connection = "CoreMasterDS")
    @DisplayName("bulkInsertのテスト")
    inner class BulkInsertTest : TestBase() {
        @Test
        @ExpectedDatabase(
            value = "/dataset/SizeTypeInfo/expected_bulk_insert.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED,
            table = "size_type_info",
            connection = "CoreMasterDS"
        )
        @DisplayName("登録あり")
        fun isNotEmpty() {
            val actual = sut.bulkInsert(
                listOf(
                    SizeTypeInfoInsert(300, 301, PlatformId.pc),
                    SizeTypeInfoInsert(400, 401, PlatformId.pc),
                    SizeTypeInfoInsert(500, 501, PlatformId.smartPhone)
                )
            )

            // 自動採番の値はテスト実行のたびに変わるため件数のみチェック
            assertEquals(3, actual.size)
        }

        @Test
        @ExpectedDatabase(
            value = "/dataset/SizeTypeInfo/setup_persist.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED,
            table = "size_type_info",
            connection = "CoreMasterDS"
        )
        @DisplayName("登録なし")
        fun isEmpty() {
            val actual = sut.bulkInsert(emptyList())

            assertTrue(actual.isEmpty())
        }
    }

    private fun sizeTypeInfo(
        sizeTypeId: Int,
        height: Int,
        width: Int,
        platformId: Int,
        definitionType: DefinitionType
    ) = SizeTypeInfo(SizeTypeId(sizeTypeId), height, width, PlatformId(platformId), definitionType)

    @ExtendWith(SpringExtension::class)
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @ContextConfiguration(
        classes = [
            CoreMasterDbConfig::class,
            CompassMasterDbConfig::class
        ]
    )
    @Import(SizeTypeInfoDaoImpl::class)
    @TestExecutionListeners(
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionDbUnitTestExecutionListener::class
    )
    @Transactional("CoreMasterTX")
    @DbUnitConfiguration(databaseConnection = ["CoreMasterDS", "CompassMasterDS"])
    abstract class TestBase : AbstractDaoTest() {
        @Autowired
        protected lateinit var sut: SizeTypeInfoDaoImpl
        protected lateinit var spyOfSut: SizeTypeInfoDaoImpl

        @BeforeEach
        fun before() {
            spyOfSut = spy(sut)
        }
    }
}
