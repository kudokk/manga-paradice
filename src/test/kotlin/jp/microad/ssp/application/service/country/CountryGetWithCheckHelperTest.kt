package jp.mangaka.ssp.application.service.country

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMasterDao
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("CountryGetWithCheckHelperのテスト")
private class CountryGetWithCheckHelperTest {
    val countryMasterDao: CountryMasterDao = mock()

    val sut = spy(CountryGetWithCheckHelper(countryMasterDao))

    @Nested
    @DisplayName("getCountiesWithCheckのテスト")
    inner class GetCountiesWithCheckTest {
        val countries = listOf(1, 2, 3, 4).map { country(it) }

        @BeforeEach
        fun beforeEach() {
            doReturn(countries).whenever(countryMasterDao).selectByIds(any())
        }

        @Test
        @DisplayName("すべての国が存在するとき")
        fun isExistAll() {
            val countryIds = listOf(1, 2, 3, 4).map { CountryId(it) }

            val actual = sut.getCountiesWithCheck(countryIds)

            assertEquals(countries, actual)
            verify(countryMasterDao, times(1)).selectByIds(countryIds)
        }

        @Test
        @DisplayName("存在しない国が含まれているとき")
        fun isNotExistAll() {
            val countryIds = listOf(1, 2, 3, 4, 5, 6).map { CountryId(it) }

            assertThrows<CompassManagerException> { sut.getCountiesWithCheck(countryIds) }
            verify(countryMasterDao, times(1)).selectByIds(countryIds)
        }
    }

    @Nested
    @DisplayName("getCountryWithCheckのテスト")
    inner class GetCountryWithCheckTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val country = country(1)
            doReturn(country).whenever(countryMasterDao).selectById(any())

            val actual = sut.getCountryWithCheck(country.countryId)

            assertEquals(country, actual)
            verify(countryMasterDao, times(1)).selectById(country.countryId)
        }

        @Test
        @DisplayName("国が存在しないとき")
        fun isNotFound() {
            val countryId = CountryId(99)

            doReturn(null).whenever(countryMasterDao).selectById(any())

            assertThrows<CompassManagerException> { sut.getCountryWithCheck(countryId) }

            verify(countryMasterDao, times(1)).selectById(countryId)
        }
    }

    private fun country(countryId: Int): CountryMaster = mock {
        on { this.countryId } doReturn CountryId(countryId)
    }
}
