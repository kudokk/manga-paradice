package jp.mangaka.ssp.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("StringUtilsのテスト")
private class StringUtilsTest {
    @Test
    @DisplayName("escapeHtmlのテスト")
    fun testEscapeHtml() {
        val actual = StringUtils.escapeHtml(
            """
                <div class='c1'>
                  <a href="https://example.com"/>
                </div>
            """.trimIndent()
        )

        assertEquals(
            """
                &lt;div class=&#39;c1&#39;&gt;
                  &lt;a href=&quot;https://example.com&quot;/&gt;
                &lt;/div&gt;
            """.trimIndent(),
            actual
        )
    }
}
