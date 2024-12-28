package jp.mangaka.ssp.presentation.controller.spot.view

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.infrastructure.datasource.dao.decoration.Decoration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("DecorationViewのテスト")
private class DecorationViewTest {
    companion object {
        val decorationId1 = DecorationId(1)
        val decorationId2 = DecorationId(2)
        val decorationId3 = DecorationId(3)
    }

    @Nested
    @DisplayName("ofのテスト")
    inner class OfTest {
        @Test
        @DisplayName("データあり")
        fun isNotEmpty() {
            val actual = DecorationView.of(
                listOf(
                    Decoration(decorationId1, "decoration1", mock(), 100, null, "text1", "#FFFFFF"),
                    Decoration(decorationId2, "decoration2", mock(), 200, "#111111", "text2", "#EEEEEE"),
                    Decoration(decorationId3, "decoration3", mock(), 300, "#222222", "text3", "#DDDDDD"),
                )
            )

            assertEquals(
                listOf(
                    DecorationView(decorationId1, "decoration1", 100, null, "text1", "#FFFFFF"),
                    DecorationView(decorationId2, "decoration2", 200, "#111111", "text2", "#EEEEEE"),
                    DecorationView(decorationId3, "decoration3", 300, "#222222", "text3", "#DDDDDD")
                ),
                actual
            )
        }

        @Test
        @DisplayName("データなし")
        fun isEmpty() {
            val actual = DecorationView.of(emptyList())

            assertTrue(actual.isEmpty())
        }
    }
}
