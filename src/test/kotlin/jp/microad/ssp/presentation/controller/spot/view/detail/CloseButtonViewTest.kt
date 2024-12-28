package jp.mangaka.ssp.presentation.controller.spot.view.detail

import jp.mangaka.ssp.presentation.controller.spot.view.detail.CloseButtonView.ColorView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("CloseButtonViewのテスト")
private class CloseButtonViewTest {
    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        @Test
        @DisplayName("全項目入力")
        fun isFull() {
            val actual = CloseButtonView.of(
                1, 200, "rgba(1,12,123,0.1)", "rgba(987, 65, 4, 1.0 )", "rgba(45,678,9,0.5)"
            )

            assertEquals(
                CloseButtonView(
                    1, 200, ColorView(1, 12, 123, 0.1), ColorView(987, 65, 4, 1.0), ColorView(45, 678, 9, 0.5)
                ),
                actual
            )
        }

        @Test
        @DisplayName("必須のみ")
        fun isNotFull() {
            val actual = CloseButtonView.of(1, null, null, null, null)

            assertEquals(CloseButtonView(1, null, null, null, null), actual)
        }
    }
}
