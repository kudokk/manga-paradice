package jp.mangaka.ssp.presentation.controller.spot.view

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.fixedfloorcpm.FixedFloorCpm
import jp.mangaka.ssp.infrastructure.datasource.dao.payment.Payment
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

@DisplayName("FixedCpmViewのテスト")
private class FixedCpmViewTest {
    companion object {
        val spotId1 = SpotId(1)
        val date1 = LocalDate.of(2024, 1, 1)
        val date2 = LocalDate.of(2024, 1, 2)
        val date3 = LocalDate.of(2024, 1, 3)
        val date4 = LocalDate.of(2024, 1, 4)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val spot = mockSpot(spotId1, "spot1")
        val payments = listOf(
            mockPayment(BigDecimal("123.456"), date1, date2),
            mockPayment(BigDecimal("234.567"), date2, date3),
            mockPayment(BigDecimal("345.678"), date3, null),
            // fixedFloorCpmsに対象データなし
            mockPayment(BigDecimal("456.789"), date2, date4)
        )
        val fixedFloorCpms = listOf(
            // paymentsと一致するデータ
            mockFixedFloorCpm(BigDecimal("12.3456"), date1, date2),
            mockFixedFloorCpm(BigDecimal("23.4567"), date2, date3),
            mockFixedFloorCpm(BigDecimal("34.5678"), date3, null),
            // paymentsと一致しないデータ
            mockFixedFloorCpm(BigDecimal("45.6789"), date1, null),
            mockFixedFloorCpm(BigDecimal("56.7890"), date1, date3)
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = FixedCpmView.of(spot, payments, fixedFloorCpms)

            assertEquals(
                listOf(
                    FixedCpmView(spotId1, "spot1", "123.456", "12.3456", date1, date2),
                    FixedCpmView(spotId1, "spot1", "234.567", "23.4567", date2, date3),
                    FixedCpmView(spotId1, "spot1", "345.678", "34.5678", date3, null)
                ),
                actual
            )
        }
    }

    private fun mockSpot(spotId: SpotId, spotName: String): Spot = mock {
        on { this.spotId } doReturn spotId
        on { this.spotName } doReturn spotName
    }

    private fun mockPayment(fixedCpm: BigDecimal, startDate: LocalDate, endDate: LocalDate?): Payment = mock {
        on { this.fixedCpm } doReturn fixedCpm
        on { this.startDate } doReturn startDate
        on { this.endDate } doReturn endDate
    }

    private fun mockFixedFloorCpm(
        floorCpm: BigDecimal,
        startDate: LocalDate,
        endDate: LocalDate?
    ): FixedFloorCpm = mock {
        on { this.floorCpm } doReturn floorCpm
        on { this.startDate } doReturn startDate
        on { this.endDate } doReturn endDate
    }
}
