package jp.mangaka.ssp.presentation.controller.common.view

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("CountrySelectElementViewのテスト")
private class CountrySelectElementViewTest {
    companion object {
        val countryId1 = CountryId(1)
        val countryId2 = CountryId(2)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class OfTest {
        @Test
        @DisplayName("データあり")
        fun isNotEmpty() {
            val actual = CountrySelectElementView.of(
                listOf(
                    CountryMaster(countryId1, "nameJa1", "nameEn1", "nameKr1", mock(), 0),
                    CountryMaster(countryId2, "nameJa2", "nameEn2", "nameKr2", mock(), 1),
                )
            )

            assertEquals(
                listOf(
                    CountrySelectElementView(countryId1, "nameJa1", "nameEn1", "nameKr1"),
                    CountrySelectElementView(countryId2, "nameJa2", "nameEn2", "nameKr2")
                ),
                actual
            )
        }

        @DisplayName("データなし")
        fun isEmpty() {
            val actual = CountrySelectElementView.of(emptyList())

            assertTrue(actual.isEmpty())
        }
    }
}
