package jp.mangaka.ssp.infrastructure.datasource.dao.spot

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DeliveryMethod
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("Spotのテスト")
private class SpotTest {
    @Nested
    @DisplayName("isAllowNativeのテスト")
    inner class IsAllowNativeTest {
        @Test
        @DisplayName("設定可")
        fun isAllow() {
            assertTrue(spot(UpstreamType.none, DeliveryMethod.js, false).isAllowNative())
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "prebidjs,sdk,true",
                "prebidjs,sdk,false",
                "prebidjs,js,true",
                "prebidjs,js,false",
                "none,sdk,true",
                "none,sdk,false",
                "none,js,true",
            ]
        )
        @DisplayName("設定不可")
        fun isAllow(upstreamType: UpstreamType, deliveryMethod: DeliveryMethod, isAmp: Boolean) {
            assertFalse(spot(upstreamType, deliveryMethod, isAmp).isAllowNative())
        }
    }

    @Nested
    @DisplayName("isAllowVideoのテスト")
    inner class IsAllowVideoTest {
        @Test
        @DisplayName("設定可")
        fun isAllow() {
            assertTrue(spot(UpstreamType.none, DeliveryMethod.js, false).isAllowVideo())
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "prebidjs,sdk,true",
                "prebidjs,sdk,false",
                "prebidjs,js,true",
                "prebidjs,js,false",
                "none,sdk,true",
                "none,sdk,false",
                "none,js,true",
            ]
        )
        @DisplayName("設定不可")
        fun isAllow(upstreamType: UpstreamType, deliveryMethod: DeliveryMethod, isAmp: Boolean) {
            assertFalse(spot(upstreamType, deliveryMethod, isAmp).isAllowVideo())
        }
    }

    private fun spot(upstreamType: UpstreamType, deliveryMethod: DeliveryMethod, isAmp: Boolean): Spot = Spot(
        mock(), mock(), "", mock(), mock(), mock(), upstreamType, deliveryMethod, null, null, 1, isAmp,
        mock(), mock(), mock(), null, null, mock()
    )
}
