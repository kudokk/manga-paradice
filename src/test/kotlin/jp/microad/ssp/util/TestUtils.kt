package jp.mangaka.ssp.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

object TestUtils {
    @JvmStatic
    fun emptyStrings(): List<String?> = listOf(null, "", " ", "\t")

    /**
     * コレクションの要素が一致しているか検証する
     *
     * @param T
     * @param expected 期待値
     * @param actual 検証する値
     */
    fun <T> assertEqualsInAnyOrder(expected: Collection<T>, actual: Collection<T>) {
        assertEquals(expected.toSet(), actual.toSet())
    }

    /**
     * コレクションが空か検証する
     *
     * @param T
     * @param actual 検証する値
     */
    fun <T> assertEmpty(actual: Collection<T>) {
        assertTrue(actual.isEmpty())
    }
}
