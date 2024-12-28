package jp.mangaka.ssp.application.service.coaccount

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster.CoAccountMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.coaccountmaster.CoAccountMasterDao
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("CoAccountGetWithCheckHelperのテスト")
private class CoAccountGetWithCheckHelperTest {
    companion object {
        val coAccountId = CoAccountId(1)
    }

    val coAccountMasterDao: CoAccountMasterDao = mock()

    val sut = spy(CoAccountGetWithCheckHelper(coAccountMasterDao))

    @Nested
    @DisplayName("getCoAccountWithCheckのテスト")
    inner class GetCoAccountWithCheckTest {
        @Test
        @DisplayName("取得成功")
        fun isCorrect() {
            val coAccount: CoAccountMaster = mock<CoAccountMaster>().apply {
                doReturn(this).whenever(coAccountMasterDao).selectByCoAccountId(any())
            }

            val actual = sut.getCoAccountWithCheck(coAccountId)

            assertEquals(coAccount, actual)
            verify(coAccountMasterDao, times(1)).selectByCoAccountId(coAccountId)
        }

        @Test
        @DisplayName("取得失敗")
        fun isNotFound() {
            doReturn(null).whenever(coAccountMasterDao).selectByCoAccountId(any())

            assertThrows<CompassManagerException> { sut.getCoAccountWithCheck(coAccountId) }

            verify(coAccountMasterDao, times(1)).selectByCoAccountId(coAccountId)
        }
    }
}
