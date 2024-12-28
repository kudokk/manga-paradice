package jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp.RelayDefaultCoAccountDsp
import jp.mangaka.ssp.presentation.controller.spot.form.DspForm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("RelaySpotDspInsertのテスト")
private class RelaySpotDspInsertTest {
    companion object {
        val spotId = SpotId(1)
        val dspId1 = DspId(1)
        val dspId2 = DspId(2)
        val dspId3 = DspId(3)
    }

    @Nested
    @DisplayName("Formから生成を行うファクトリ関数のテスト")
    inner class OfFormsTest {
        val forms = listOf(
            DspForm(dspId1, 10.toBigDecimal(), 20.toBigDecimal()),
            DspForm(dspId2, 11.toBigDecimal(), 21.toBigDecimal()),
            DspForm(dspId3, 12.toBigDecimal(), null)
        )
        val defaultDsps = listOf(
            RelayDefaultCoAccountDsp(mock(), dspId1, mock(), 30),
            RelayDefaultCoAccountDsp(mock(), dspId2, mock(), 31)
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = RelaySpotDspInsert.of(spotId, forms, defaultDsps)

            assertEquals(
                listOf(
                    RelaySpotDspInsert(spotId, dspId1, 10.toBigDecimal(), 30, 20.toBigDecimal()),
                    RelaySpotDspInsert(spotId, dspId2, 11.toBigDecimal(), 31, 21.toBigDecimal()),
                    RelaySpotDspInsert(spotId, dspId3, 12.toBigDecimal(), 1, null),
                ),
                actual
            )
        }
    }

    @Nested
    @DisplayName("デフォルトDSPから生成を行うファクトリ関数のテスト")
    inner class OfDefaultDspTest {
        val defaultDsps = listOf(
            RelayDefaultCoAccountDsp(mock(), dspId1, 10.toBigDecimal(), 20),
            RelayDefaultCoAccountDsp(mock(), dspId2, 11.toBigDecimal(), 21),
            RelayDefaultCoAccountDsp(mock(), dspId3, 12.toBigDecimal(), 22)
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = RelaySpotDspInsert.of(spotId, defaultDsps)

            assertEquals(
                listOf(
                    RelaySpotDspInsert(spotId, dspId1, 10.toBigDecimal(), 20, null),
                    RelaySpotDspInsert(spotId, dspId2, 11.toBigDecimal(), 21, null),
                    RelaySpotDspInsert(spotId, dspId3, 12.toBigDecimal(), 22, null)
                ),
                actual
            )
        }
    }
}
