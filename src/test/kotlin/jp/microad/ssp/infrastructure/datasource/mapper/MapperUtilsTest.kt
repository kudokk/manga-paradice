package jp.mangaka.ssp.infrastructure.datasource.mapper

import jp.mangaka.ssp.application.valueobject.IdValueObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.LocalDateTime

@DisplayName("MapperUtilsのテスト")
private class MapperUtilsTest {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("mapToValueのテスト")
    inner class MapToValueTest {
        @Test
        @DisplayName("引数がnull")
        fun isNull() {
            assertNull(MapperUtils.mapToValue(null))
        }

        @Test
        @DisplayName("引数がコレクション")
        fun isCollection() {
            assertEquals(
                listOf(1, "aaa", "typeA", 2),
                MapperUtils.mapToValue(listOf(1, "aaa", TestType.TYPE_A, TestId(2)))
            )
        }

        @Test
        @DisplayName("引数がEnum")
        fun isEnum() {
            assertEquals("typeA", MapperUtils.mapToValue(TestType.TYPE_A))
        }

        @Test
        @DisplayName("引数がIDの値オブジェクト")
        fun isIdValueObject() {
            assertEquals(1, MapperUtils.mapToValue(TestId(1)))
        }

        @ParameterizedTest
        @MethodSource("otherValue")
        @DisplayName("引数がその他の値")
        fun isOtherValue(value: Any) {
            assertEquals(value, MapperUtils.mapToValue(value))
        }

        private fun otherValue() = listOf(1, "aaa", LocalDate.of(2024, 1, 1), LocalDateTime.of(2024, 1, 1, 0, 0))
    }

    enum class TestType(val value: String) {
        TYPE_A("typeA");

        override fun toString() = value
    }

    data class TestId(override val value: Int) : IdValueObject<Int>()
}
