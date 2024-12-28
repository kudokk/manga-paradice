package jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay

import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("SpotBannerDisplayのテスト")
private class SpotBannerDisplayTest {
    @Nested
    @DisplayName("isDisplayControlのテスト")
    inner class IsDisplayControlTest {
        @ParameterizedTest
        @CsvSource(value = ["10,,,", ",20,,", ",,30,", ",,,40", "10,20,30,40"])
        @DisplayName("表示制御オン")
        fun isDisplayControlled(top: Int?, bottom: Int?, left: Int?, right: Int?) {
            assertTrue(entity(top, bottom, left, right).isDisplayControl())
        }

        @Test
        @DisplayName("表示制御オフ")
        fun isNotDisplayControlled() {
            assertFalse(entity(null, null, null, null).isDisplayControl())
        }

        private fun entity(top: Int?, bottom: Int?, left: Int?, right: Int?) =
            SpotBannerDisplay(mock(), top, bottom, left, right, false, false, null, null, null, null, null, null)
    }
}
