package jp.mangaka.ssp.presentation.controller.spot.view

import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster.CurrencyMaster
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("CurrencyViewのテスト")
private class CurrencyViewTest {
    companion object {
        val currencyId1 = CurrencyId(1)
        val currencyId2 = CurrencyId(2)
        val currencyId3 = CurrencyId(3)
    }

    @Nested
    @DisplayName("ofのテスト")
    inner class OfTest {
        @Test
        @DisplayName("データあり")
        fun isNotEmpty() {
            val actual = CurrencyView.of(
                listOf(
                    CurrencyMaster(currencyId1, "JPY"),
                    CurrencyMaster(currencyId2, "USD"),
                    CurrencyMaster(currencyId3, "CNY")
                )
            )

            assertEquals(
                listOf(
                    CurrencyView(currencyId1, "JPY"),
                    CurrencyView(currencyId2, "USD"),
                    CurrencyView(currencyId3, "CNY")
                ),
                actual
            )
        }

        @Test
        @DisplayName("データなし")
        fun isEmpty() {
            val actual = CurrencyView.of(emptyList())

            assertTrue(actual.isEmpty())
        }
    }
}
