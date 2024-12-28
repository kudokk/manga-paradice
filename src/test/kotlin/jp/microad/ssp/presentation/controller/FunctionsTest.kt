package jp.mangaka.ssp.presentation.controller

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletResponse
import java.time.LocalDateTime
import java.util.Locale

@DisplayName("Functionsのテスト")
private class FunctionsTest {
    @Nested
    @DisplayName("HttpServletResponse#setCsvHeader")
    inner class HttpServletResponseSetCsvHeaderTest {
        val response = MockHttpServletResponse()

        @Test
        @DisplayName("言語が日本語のとき")
        fun isJapanese() {
            response.setCsvHeader("test-file", Locale.JAPANESE)

            assertEquals("SHIFT_JIS", response.characterEncoding)
            assertEquals("text/csv;charset=SHIFT_JIS", response.contentType)
            assertEquals("attachment;filename*=utf-8''test-file", response.getHeader("Content-Disposition"))
        }

        @Test
        @DisplayName("言語が日本語以外のとき")
        fun isNotJapanese() {
            response.setCsvHeader("test-file", Locale.ENGLISH)

            assertEquals("UTF-8", response.characterEncoding)
            assertEquals("text/csv;charset=UTF-8", response.contentType)
            assertEquals("attachment;filename*=utf-8''test-file", response.getHeader("Content-Disposition"))
        }
    }

    @Nested
    @DisplayName("createFilenameのテスト")
    inner class CreateFilenameTest {
        val now: LocalDateTime = LocalDateTime.of(2024, 1, 2, 3, 4, 5, 678000000)

        @BeforeEach
        fun beforeEach() {
            mockkStatic(LocalDateTime::class)
            every { LocalDateTime.now() } returns now
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = createFilename("test-file")

            assertEquals("test-file20240102030405678", actual)
        }
    }
}
