package jp.mangaka.ssp.presentation.controller.spot.view.detail

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster.DspMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp.RelaySpotDsp
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.ReqgroupDspCountryCo
import jp.mangaka.ssp.presentation.controller.spot.view.detail.DspSettingListItemView.DspView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.DspSettingListItemView.DspView.CountryView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("DspSettingListItemViewのテスト")
private class DspSettingListItemViewTest {
    companion object {
        val spotId = SpotId(1)
        val dspId1 = DspId(1)
        val dspId2 = DspId(2)
        val dspId3 = DspId(3)
        val dspId4 = DspId(4)
        val countryId1 = CountryId(1)
        val countryId2 = CountryId(2)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val relaySpotDsps = listOf(
            RelaySpotDsp(spotId, dspId1, BigDecimal("123.456"), BigDecimal("234.567")),
            RelaySpotDsp(spotId, dspId2, BigDecimal("345.678"), BigDecimal("456.789")),
            RelaySpotDsp(spotId, dspId3, BigDecimal("567.890"), null),
            RelaySpotDsp(spotId, dspId4, BigDecimal("678.901"), null),
        )
        val reqgroupDspCountryCos = listOf(
            ReqgroupDspCountryCo(mock(), dspId1, countryId1),
            ReqgroupDspCountryCo(mock(), dspId1, countryId2),
            ReqgroupDspCountryCo(mock(), dspId2, countryId1),
            ReqgroupDspCountryCo(mock(), dspId3, CountryId.zero),
            ReqgroupDspCountryCo(mock(), dspId3, countryId1),
            ReqgroupDspCountryCo(mock(), dspId3, countryId2)
        )
        val dsps = listOf(
            DspMaster(dspId1, "dsp1"),
            DspMaster(dspId2, "dsp2"),
            DspMaster(dspId3, "dsp3"),
            DspMaster(dspId4, "dsp4"),
        )
        val countries = listOf(
            CountryMaster(countryId1, "country1", "countryEn1", "countryKr1", mock(), 0),
            CountryMaster(countryId2, "country2", "countryEn2", "countryKr2", mock(), 1)
        )
        val countryView1 = CountryView(countryId1, "country1", "countryEn1", "countryKr1")
        val countryView2 = CountryView(countryId2, "country2", "countryEn2", "countryKr2")

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = DspSettingListItemView.of(relaySpotDsps, reqgroupDspCountryCos, dsps, countries)

            assertEquals(
                listOf(
                    DspSettingListItemView(
                        DspView(dspId1, "dsp1", listOf(countryView1, countryView2)),
                        "123.456",
                        "234.567"
                    ),
                    DspSettingListItemView(
                        DspView(dspId2, "dsp2", listOf(countryView1)),
                        "345.678",
                        "456.789"
                    ),
                    DspSettingListItemView(
                        DspView(dspId3, "dsp3", emptyList()),
                        "567.890",
                        null
                    ),
                    DspSettingListItemView(
                        DspView(dspId4, "dsp4", emptyList()),
                        "678.901",
                        null
                    )
                ),
                actual
            )
        }
    }
}
