package jp.mangaka.ssp.application.service.struct

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructDao
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("StructGetWithCheckHelperテスト")
private class StructGetWithCheckHelperTest {
    val structDao: StructDao = mock()

    val sut = spy(StructGetWithCheckHelper(structDao))

    @Nested
    @DisplayName("getStructsWithCheckのテスト")
    inner class GetStructsWithCheckTest {
        val structs = listOf(1, 2, 3, 4).map { struct(it) }
        val statuses = listOf(StructStatus.active, StructStatus.stop)

        @BeforeEach
        fun beforeEach() {
            doReturn(structs).whenever(structDao).selectByIdsAndStatuses(any(), any())
        }

        @Test
        @DisplayName("すべてのストラクトが条件に合致するとき")
        fun isExistAll() {
            val structIds = listOf(1, 2, 3, 4).map { StructId(it) }

            val actual = sut.getStructsWithCheck(structIds, statuses)

            assertEquals(structs, actual)
            verify(structDao, times(1)).selectByIdsAndStatuses(structIds, statuses)
        }

        @Test
        @DisplayName("条件に合致しないストラクトが存在するとき")
        fun isNotExistAll() {
            val structIds = listOf(1, 2, 3, 4, 5, 6).map { StructId(it) }

            assertThrows<CompassManagerException> { sut.getStructsWithCheck(structIds, statuses) }

            verify(structDao, times(1)).selectByIdsAndStatuses(structIds, statuses)
        }
    }

    private fun struct(structId: Int): StructCo = mock {
        on { this.structId } doReturn StructId(structId)
    }
}
