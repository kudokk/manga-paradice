package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("SpotVideoFloorCpmUpdateのテスト")
private class SpotVideoFloorCpmUpdateTest {
    companion object {
        val spotId = SpotId(1)
        val date1 = LocalDate.of(2024, 1, 1)
        val date2 = LocalDate.of(2024, 1, 2)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val details = listOf(
                VideoSettingForm.VideoDetailForm(
                    AspectRatioId.aspectRatio32to5,
                    true,
                    null,
                    null,
                    null,
                    true,
                    true,
                    123.456.toBigDecimal(),
                    date1
                ),
                VideoSettingForm.VideoDetailForm(
                    AspectRatioId.aspectRatio16to9,
                    true,
                    null,
                    null,
                    null,
                    true,
                    true,
                    987.654.toBigDecimal(),
                    date2
                )
            )
            val form = VideoSettingForm(null, true, null, details)

            val actual = SpotVideoFloorCpmUpdate.of(spotId, form)

            Assertions.assertEquals(
                listOf(
                    SpotVideoFloorCpmUpdate(spotId, AspectRatioId.aspectRatio32to5, date1, 123.456.toBigDecimal()),
                    SpotVideoFloorCpmUpdate(spotId, AspectRatioId.aspectRatio16to9, date2, 987.654.toBigDecimal())
                ),
                actual
            )
        }
    }
}
