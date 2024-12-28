package jp.mangaka.ssp.presentation.controller.spot.view

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio16to5
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio16to9
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId.Companion.aspectRatio32to5
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio
import jp.mangaka.ssp.presentation.controller.spot.view.AspectRatioView.VideoType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("AspectRatioViewのテスト")
private class AspectRatioViewTest {
    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val aspectRatio1to1 = AspectRatioId(99)
        val entities = listOf(
            AspectRatio(aspectRatio16to9, 16, 9, mock()),
            AspectRatio(aspectRatio32to5, 32, 5, mock()),
            AspectRatio(aspectRatio16to5, 16, 5, mock()),
            AspectRatio(aspectRatio1to1, 1, 1, mock()),
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = AspectRatioView.of(entities)

            assertEquals(
                listOf(
                    AspectRatioView(aspectRatio16to9, 16, 9, VideoType.Wipe),
                    AspectRatioView(aspectRatio32to5, 32, 5, VideoType.FullWide),
                    AspectRatioView(aspectRatio16to5, 16, 5, VideoType.FullWide),
                    AspectRatioView(aspectRatio1to1, 1, 1, VideoType.Inline),
                ),
                actual
            )
        }
    }
}
