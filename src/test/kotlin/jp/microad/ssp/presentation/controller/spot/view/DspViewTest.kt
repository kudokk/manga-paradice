package jp.mangaka.ssp.presentation.controller.spot.view

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster.DspMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp.RelayDefaultCoAccountDsp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("DspView")
private class DspViewTest {
    companion object {
        val dspId1 = DspId(1)
        val dspId2 = DspId(2)
        val countryId1 = CountryId(1)
        val countryId2 = CountryId(2)
        val countryId3 = CountryId(3)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class OfTest {
        val dspCountryIds = mapOf(dspId1 to listOf(countryId1, countryId2), dspId2 to listOf(countryId3))
        val defaultDsps = mapOf(dspId1 to RelayDefaultCoAccountDsp(mock(), dspId1, 10.toBigDecimal(), 1))

        @Test
        @DisplayName("データあり")
        fun isNotEmpty() {
            val actual = DspView.of(
                listOf(DspMaster(dspId1, "dsp1"), DspMaster(dspId2, "dsp2")),
                dspCountryIds,
                defaultDsps
            )

            assertEquals(
                listOf(
                    DspView(dspId1, "dsp1", listOf(countryId1, countryId2), "10"),
                    DspView(dspId2, "dsp2", listOf(countryId3), null),
                ),
                actual
            )
        }

        @Test
        @DisplayName("データなし")
        fun isEmpty() {
            val actual = DspView.of(emptyList(), dspCountryIds, defaultDsps)

            assertTrue(actual.isEmpty())
        }
    }
}
