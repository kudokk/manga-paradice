package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio16to9
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio32to5
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SpotVideoFloorCpmInsertのテスト")
private class SpotVideoFloorCpmInsertTest {
    companion object {
        val spotId = SpotId(1)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val details = listOf(
                VideoDetailForm(aspectRatio32to5, true, null, null, null, true, true, 123.456.toBigDecimal(), null),
                VideoDetailForm(aspectRatio16to9, true, null, null, null, true, true, 987.654.toBigDecimal(), null),
                VideoDetailForm(aspectRatio32to5, true, null, null, null, true, true, null, null),
            )
            val form = VideoSettingForm(null, true, null, details)

            val actual = SpotVideoFloorCpmInsert.of(spotId, form)

            assertEquals(
                listOf(
                    SpotVideoFloorCpmInsert(spotId, aspectRatio32to5, 123.456.toBigDecimal()),
                    SpotVideoFloorCpmInsert(spotId, aspectRatio16to9, 987.654.toBigDecimal())
                ),
                actual
            )
        }
    }
}
