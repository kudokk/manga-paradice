package jp.mangaka.ssp.util.localfile

import com.nhaarman.mockito_kotlin.spy
import jp.mangaka.ssp.util.exception.CompassManagerException
import jp.mangaka.ssp.util.localfile.valueobject.LocalFileType
import jp.mangaka.ssp.util.localfile.valueobject.config.CommonConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

@DisplayName("LocalFileUtilsのテスト")
private class LocalFileUtilsTest {
    @TempDir
    lateinit var rootDir: Path

    val sut: LocalFileUtils by lazy { spy(LocalFileUtils(rootDir.absolutePathString())) }

    @Nested
    @DisplayName("readのテスト")
    inner class ReadTest {
        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val file = rootDir.resolve("test.txt")
            val textBytes = "test".toByteArray()

            Files.createFile(file)
            Files.write(file, textBytes)

            val actual = sut.read(file.fileName)

            assertArrayEquals(textBytes, actual)
        }

        @Test
        @DisplayName("読み込み失敗")
        fun isReadFailure() {
            assertThrows<IOException> { sut.read(Paths.get("hoge.txt")) }
        }
    }

    @Nested
    @DisplayName("loadConfigのテスト")
    inner class LoadConfigTest {
        val file: Path by lazy {
            val path = rootDir.resolve(LocalFileType.CommonConfig.filePath)
            Files.createDirectories(path.parent)
            path
        }

        @Nested
        @DisplayName("common-config.jsonの読み込みのテスト")
        inner class CommonConfigTest {
            @AfterEach
            fun afterEach() {
                Files.deleteIfExists(file)
            }

            @Test
            @DisplayName("ファイルの読み込みに失敗したとき")
            fun isReadFileFailure() {
                assertThrows<CompassManagerException> { sut.loadConfig(LocalFileType.CommonConfig) }
            }

            @Test
            @DisplayName("JSON形式不正")
            fun isInvalidJson() {
                Files.createFile(file)
                Files.writeString(file, "hoge")

                assertThrows<CompassManagerException> { sut.loadConfig(LocalFileType.CommonConfig) }
            }

            @Test
            @DisplayName("読み込み成功")
            fun isCorrect() {
                val configJson = """
                    {
                      "taxRate": 1.1,
                      "unknown": "unknown property"
                    }
                """.trimIndent()

                Files.createFile(file)
                Files.writeString(file, configJson)

                val actual = sut.loadConfig(LocalFileType.CommonConfig)

                assertEquals(CommonConfig(1.1.toBigDecimal()), actual)
            }
        }
    }
}
