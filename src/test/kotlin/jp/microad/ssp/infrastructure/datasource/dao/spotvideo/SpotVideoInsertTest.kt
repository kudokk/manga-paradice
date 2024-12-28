package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SpotVideoInsertのテスト")
private class SpotVideoInsertTest {
    companion object {
        val spotId = SpotId(1)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = listOf(
                VideoSettingForm(null, true, null, mock()),
                VideoSettingForm(null, false, null, mock())
            ).map { SpotVideoInsert.of(spotId, it) }

            assertEquals(
                listOf(
                    SpotVideoInsert(spotId, true.toString()),
                    SpotVideoInsert(spotId, false.toString())
                ),
                actual
            )
        }
    }
}
