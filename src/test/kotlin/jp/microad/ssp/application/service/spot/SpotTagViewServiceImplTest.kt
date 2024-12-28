package jp.mangaka.ssp.application.service.spot

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.service.spot.helper.SpotGetWithCheckHelper
import java.math.BigDecimal
import java.time.LocalDateTime
import jp.mangaka.ssp.application.service.spot.util.CryptUtils
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.proprietydsp.ProprietyDspId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteType
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DeliveryMethod
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.presentation.controller.spot.view.SizeTypeInfoView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource

@DisplayName("SpotTagViewServiceImplのテスト")
private class SpotTagViewServiceImplTest {
    companion object {
        val coAccountId = CoAccountId(1)
        const val ENCRYPTED_SPOT_ID = "0123456789abcdef0123456789abcdef"
    }

    val cryptUtils: CryptUtils = mock()
    val spotGetWithCheckHelper: SpotGetWithCheckHelper = mock()
    val sizeTypeInfoDao: SizeTypeInfoDao = mock()

    val sut = spy(SpotTagViewServiceImpl(cryptUtils, spotGetWithCheckHelper, sizeTypeInfoDao))

    @Nested
    @DisplayName("getSizeAttributeByOrConditionのテスト")
    inner class GetSizeAttributeByOrConditionTest {
        @ParameterizedTest
        @DisplayName("正常")
        @CsvSource(
            "100, 100, 'width=\"100\" height=\"100\" '",
            "100, , 'width=\"100\" '",
            ", 100, 'height=\"100\" '",
            ", , ''"
        )
        fun isCorrect(width: Int?, height: Int?, expectedValue: String) =
            assertEquals(expectedValue, sut.getSizeAttributeByOrCondition(width, height))
    }

    @Nested
    @DisplayName("getSizeStyleByOrConditionのテスト")
    inner class GetSizeStyleByOrConditionTest {
        @ParameterizedTest
        @DisplayName("正常")
        @CsvSource(
            "100, 100, style=\"width:100px; height:100px; \"",
            "100, , style=\"width:100px; \"",
            ", 100, style=\"height:100px; \"",
            ", , ''"
        )
        fun isCorrect(width: Int?, height: Int?, expectedValue: String) =
            assertEquals(expectedValue, sut.getSizeStyleByOrCondition(width, height))
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getMacroUrlParametersのテスト")
    inner class GetMacroUrlParametersTest {
        val expectedValue1 = mutableMapOf(
            "url" to "${'$'}{COMPASS_EXT_URL}",
            "referrer" to "${'$'}{COMPASS_EXT_REF}"
        )
        val expectedValue2 = mutableMapOf<String, String>()

        private fun validParams() = listOf(
            Arguments.of(true, expectedValue1),
            Arguments.of(false, expectedValue2)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isCorrect(isAllowedMacroUrl: Boolean, expectedValue: MutableMap<String, String>) =
            assertEquals(expectedValue, sut.getMacroUrlParameters(isAllowedMacroUrl))
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getDeviceParametersのテスト")
    inner class GetDeviceParametersTest {
        val expectedValue1 = mutableMapOf(
            "ifa" to "${'$'}{COMPASS_EXT_IFA}",
            "appid" to "${'$'}{COMPASS_EXT_APPID}",
            "geo" to "${'$'}{COMPASS_EXT_GEO}"
        )
        val expectedValue2 = mutableMapOf<String, String>()

        private fun validParams() = listOf(
            Arguments.of(SiteType.a_app, expectedValue1),
            Arguments.of(SiteType.pc_web, expectedValue2)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isCorrect(siteType: SiteType, expectedValue: MutableMap<String, String>) =
            assertEquals(expectedValue, sut.getDeviceParameters(siteType))
    }

    @Nested
    @DisplayName("generateHeaderTagのテスト")
    inner class GenerateHeaderTagTest {
        val expectedValue =
            """
                <script type="text/javascript">
                  var mangakaCompass = mangakaCompass || {};
                  mangakaCompass.queue = mangakaCompass.queue || [];
                </script>
                <script type="text/javascript" charset="UTF-8" src="//j.mangaka.net/js/compass.js" onload="new mangakaCompass.AdInitializer().initialize();" async></script>
                
            """.trimIndent().replace("\n", "\r\n")

        @Test
        @DisplayName("正常")
        fun isCorrect() = assertEquals(expectedValue, sut.generateHeaderTag())
    }

    @Nested
    @DisplayName("generateJsWebTagのテスト")
    inner class GenerateJsWebTagTest {
        val adParams = mutableMapOf(
            "spot" to "$ENCRYPTED_SPOT_ID",
            "url" to "${'$'}{COMPASS_EXT_URL}",
            "referrer" to "${'$'}{COMPASS_EXT_REF}"
        )

        val expectedValue =
            """
                <div id="$ENCRYPTED_SPOT_ID" >
                  <script type="text/javascript">
                    mangakaCompass.queue.push({
                      "spot": "$ENCRYPTED_SPOT_ID",
                      "url": "${'$'}{COMPASS_EXT_URL}",
                      "referrer": "${'$'}{COMPASS_EXT_REF}"
                    });
                  </script>
                </div>
                
            """.trimIndent().replace("\n", "\r\n")

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn("").`when`(sut).getSizeStyleByOrCondition(any(), any())
            doReturn(adParams).`when`(sut).getMacroUrlParameters(any())
            assertEquals(expectedValue, sut.generateJsWebTag(ENCRYPTED_SPOT_ID, true, 0, 0))
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("generatePrebidTagのテスト")
    inner class GeneratePrebidTagTest {
        val sizeTypeInfoList1 = SizeTypeInfoView.of(
            sizeTypeInfos = listOf(
                SizeTypeInfo(SizeTypeId(1), 100, 100, PlatformId(1), SizeTypeInfo.DefinitionType.standard),
                SizeTypeInfo(SizeTypeId(2), 200, 200, PlatformId(2), SizeTypeInfo.DefinitionType.userdefined),
                SizeTypeInfo(SizeTypeId(3), 300, 300, PlatformId(1), SizeTypeInfo.DefinitionType.standard)
            )
        )
        val sizeTypeInfoList2 = SizeTypeInfoView.of(
            sizeTypeInfos = listOf(
                SizeTypeInfo(SizeTypeId(1), 100, 100, PlatformId(1), SizeTypeInfo.DefinitionType.standard)
            )
        )
        val sizeTypeInfoList3 = SizeTypeInfoView.of(sizeTypeInfos = listOf())

        val expectedValue1 =
            """
                {
                  code: '$ENCRYPTED_SPOT_ID', // codeの値は適宜編集してご利用ください
                  mediaTypes: {
                    banner: {
                      sizes: [[100, 100],[200, 200],[300, 300]]
                    }
                  },
                  bids: [
                    {
                      bidder: 'mangaka',
                      params: {
                        spot: '$ENCRYPTED_SPOT_ID',
                        url: '${'$'}{COMPASS_EXT_URL}',
                        referrer: '${'$'}{COMPASS_EXT_REF}',
                        ifa: '${'$'}{COMPASS_EXT_IFA}',
                        appid: '${'$'}{COMPASS_EXT_APPID}',
                        geo: '${'$'}{COMPASS_EXT_GEO}'
                      }
                    }
                  ]
                }
            
            """.trimIndent().replace("\n", "\r\n")
        val expectedValue2 =
            """
                {
                  code: '$ENCRYPTED_SPOT_ID', // codeの値は適宜編集してご利用ください
                  mediaTypes: {
                    banner: {
                      sizes: [[100, 100]]
                    }
                  },
                  bids: [
                    {
                      bidder: 'mangaka',
                      params: {
                        spot: '$ENCRYPTED_SPOT_ID'
                      }
                    }
                  ]
                }
            
            """.trimIndent().replace("\n", "\r\n")
        val expectedValue3 =
            """
                {
                  code: '$ENCRYPTED_SPOT_ID', // codeの値は適宜編集してご利用ください
                  mediaTypes: {
                    banner: {
                      sizes: []
                    }
                  },
                  bids: [
                    {
                      bidder: 'mangaka',
                      params: {
                        spot: '$ENCRYPTED_SPOT_ID'
                      }
                    }
                  ]
                }
            
            """.trimIndent().replace("\n", "\r\n")

        private fun validParams() = listOf(
            Arguments.of(SiteType.i_app, true, sizeTypeInfoList1, expectedValue1),
            Arguments.of(SiteType.sp_web, false, sizeTypeInfoList2, expectedValue2),
            Arguments.of(SiteType.sp_web, false, sizeTypeInfoList3, expectedValue3)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isCorrect(
            siteType: SiteType,
            isAllowedMacroUrl: Boolean,
            sizeTypeInfoList: List<SizeTypeInfoView>,
            expectedValue: String
        ) =
            assertEquals(
                expectedValue,
                sut.generatePrebidTag(ENCRYPTED_SPOT_ID, siteType, isAllowedMacroUrl, sizeTypeInfoList)
            )
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("generateGamTagのテスト")
    inner class GenerateGamTagTest {
        val expectedValue1 =
            """
                <script type="text/javascript" charset="UTF-8" src="https://cdn.mangaka.jp/js/compass-gam.js"></script>
                <script type="text/javascript">
                  var mangakaCompass = mangakaCompass || {};
                  mangakaCompass.gamQueue = mangakaCompass.gamQueue || [];
                  mangakaCompass.gamQueue.push({
                    "spot": "$ENCRYPTED_SPOT_ID",
                    "url": "${'$'}{COMPASS_EXT_URL}",
                    "referrer": "${'$'}{COMPASS_EXT_REF}",
                    "ifa": "${'$'}{COMPASS_EXT_IFA}",
                    "appid": "${'$'}{COMPASS_EXT_APPID}",
                    "geo": "${'$'}{COMPASS_EXT_GEO}",
                    "width": "200",
                    "height": "100"
                  });
                </script>
            
            """.trimIndent().replace("\n", "\r\n")
        val expectedValue2 =
            """
                <script type="text/javascript" charset="UTF-8" src="https://cdn.mangaka.jp/js/compass-gam.js"></script>
                <script type="text/javascript">
                  var mangakaCompass = mangakaCompass || {};
                  mangakaCompass.gamQueue = mangakaCompass.gamQueue || [];
                  mangakaCompass.gamQueue.push({
                    "spot": "$ENCRYPTED_SPOT_ID",
                    "width": "200"
                  });
                </script>
            
            """.trimIndent().replace("\n", "\r\n")
        val expectedValue3 =
            """
                <script type="text/javascript" charset="UTF-8" src="https://cdn.mangaka.jp/js/compass-gam.js"></script>
                <script type="text/javascript">
                  var mangakaCompass = mangakaCompass || {};
                  mangakaCompass.gamQueue = mangakaCompass.gamQueue || [];
                  mangakaCompass.gamQueue.push({
                    "spot": "$ENCRYPTED_SPOT_ID",
                    "height": "100"
                  });
                </script>
            
            """.trimIndent().replace("\n", "\r\n")
        val expectedValue4 =
            """
                <script type="text/javascript" charset="UTF-8" src="https://cdn.mangaka.jp/js/compass-gam.js"></script>
                <script type="text/javascript">
                  var mangakaCompass = mangakaCompass || {};
                  mangakaCompass.gamQueue = mangakaCompass.gamQueue || [];
                  mangakaCompass.gamQueue.push({
                    "spot": "$ENCRYPTED_SPOT_ID"
                  });
                </script>
            
            """.trimIndent().replace("\n", "\r\n")

        private fun validParams() = listOf(
            Arguments.of(SiteType.a_app, true, 200, 100, expectedValue1),
            Arguments.of(SiteType.pc_web, false, 200, null, expectedValue2),
            Arguments.of(SiteType.pc_web, false, null, 100, expectedValue3),
            Arguments.of(SiteType.pc_web, false, null, null, expectedValue4)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isCorrect(
            siteType: SiteType,
            isAllowedMacroUrl: Boolean,
            width: Int?,
            height: Int?,
            expectedValue: String
        ) = assertEquals(
            expectedValue,
            sut.generateGamTag(ENCRYPTED_SPOT_ID, siteType, isAllowedMacroUrl, width, height)
        )
    }

    @Nested
    @DisplayName("generateJsAppTagのテスト")
    inner class GenerateJsAppTagTest {
        val expectedValue =
            """
                <div id="$ENCRYPTED_SPOT_ID" >
                  <script type="text/javascript">
                    mangakaCompass.queue.push({
                      "spot": "$ENCRYPTED_SPOT_ID",
                      "ifa": "${'$'}{COMPASS_EXT_IFA}",
                      "appid": "${'$'}{COMPASS_EXT_APPID}",
                      "geo": "${'$'}{COMPASS_EXT_GEO}"
                    });
                  </script>
                </div>
            
            """.trimIndent().replace("\n", "\r\n")

        @Test
        @DisplayName("正常")
        fun isCorrect() = assertEquals(expectedValue, sut.generateJsAppTag(ENCRYPTED_SPOT_ID))
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("generateAmpTagのテスト")
    inner class GenerateAmpTagTest {
        val expectedValue1 =
            """
                <amp-ad 
                  type="mangaka"
                  data-spot="$ENCRYPTED_SPOT_ID"
                  data-url="${'$'}{COMPASS_EXT_URL}"
                  data-referrer="${'$'}{COMPASS_EXT_REF}"
                  data-ifa="${'$'}{COMPASS_EXT_IFA}"
                  data-appid="${'$'}{COMPASS_EXT_APPID}"
                  data-geo="${'$'}{COMPASS_EXT_GEO}">
                </amp-ad>
            
            """.trimIndent().replace("\n", "\r\n")
        val expectedValue2 =
            """
                <amp-ad 
                  type="mangaka"
                  data-spot="$ENCRYPTED_SPOT_ID"
                  data-ifa="${'$'}{COMPASS_EXT_IFA}"
                  data-appid="${'$'}{COMPASS_EXT_APPID}"
                  data-geo="${'$'}{COMPASS_EXT_GEO}">
                </amp-ad>
            
            """.trimIndent().replace("\n", "\r\n")

        private fun validParams() = listOf(
            Arguments.of(true, expectedValue1),
            Arguments.of(false, expectedValue2)
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("正常")
        fun isCorrect(isAllowedMacroUrl: Boolean, expectedValue: String) {
            doReturn("").`when`(sut).getSizeAttributeByOrCondition(any(), any())
            assertEquals(expectedValue, sut.generateAmpTag(ENCRYPTED_SPOT_ID, isAllowedMacroUrl, 0, 0))
        }
    }

    @Nested
    @DisplayName("generateSpotIdTagのテスト")
    inner class GenerateSpotIdTagTest {
        val expectedValue =
            """
                <Spot spot-id="$ENCRYPTED_SPOT_ID" />

            """.trimIndent().replace("\n", "\r\n")

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn("").`when`(sut).getSizeAttributeByOrCondition(any(), any())
            assertEquals(expectedValue, sut.generateSpotIdTag(ENCRYPTED_SPOT_ID, 100, 100))
        }
    }

    @Nested
    @DisplayName("generateReactComponentのテスト")
    inner class GenerateReactComponentTest {
        val expectedValue =
            """
                'use client';
                import Script from "next/script";
                import {useEffect} from "react";
                function Spot({spotId, width, height}: { spotId: string, width?: string, height?: string }) {
                  function getAd() {
                    // @ts-ignore
                    if (window.mangakaCompass == null) return
                    // @ts-ignore
                    window.mangakaCompass.queue = window.mangakaCompass.queue || [];
                    // @ts-ignore
                    window.mangakaCompass.queue.push({
                      spot: spotId,
                      url: "${'$'}{COMPASS_EXT_URL}",
                      referrer: "${'$'}{COMPASS_EXT_REF}",
                      ifa: "${'$'}{COMPASS_EXT_IFA}",
                      appid: "${'$'}{COMPASS_EXT_APPID}",
                      geo: "${'$'}{COMPASS_EXT_GEO}"
                    })
                  }
                  useEffect(() => {
                    getAd();
                  }, [])
                  function init() {
                    getAd()
                    // @ts-ignore
                    new window.mangakaCompass.AdInitializer().initialize()
                  }
                  return (
                    <>
                      <Script
                        type="text/javascript"
                        charSet="UTF-8"
                        src="//j.mangaka.net/js/compass.js"
                        onLoad={init}
                        async
                      />
                      <div id={spotId} {... {width, height}} ></div>
                    </>
                  )
                }
                export default Spot;
            
            """.trimIndent()

        // パラメータを取得できるかのテストは実装済みのため、パラメータを表示できるかのテストのみを記載
        @Test
        @DisplayName("正常")
        fun isCorrect() =
            assertEquals(expectedValue, sut.generateReactComponent(ENCRYPTED_SPOT_ID, SiteType.a_app, true))
    }

    @Nested
    @DisplayName("generateVueComponentWithHeaderのテスト")
    inner class GenerateVueComponentWithHeader {
        val expectedValue =
            """
                <template>
                  <div :id="spotId" :width="width" :height="height">
                    <component is="script" type="text/javascript">
                      var mangakaCompass = mangakaCompass || {};
                      mangakaCompass.queue = mangakaCompass.queue || [];
                    </component>
                  </div>
                </template>
    
                <script setup lang="ts">
                import {onMounted} from "vue";
    
                const props = defineProps({spotId: String, width: String, height: String});
    
                onMounted(() => {
                  // @ts-ignore
                  mangakaCompass.queue.push({
                    spot: props.spotId,
                    url: "${'$'}{COMPASS_EXT_URL}",
                    referrer: "${'$'}{COMPASS_EXT_REF}",
                    ifa: "${'$'}{COMPASS_EXT_IFA}",
                    appid: "${'$'}{COMPASS_EXT_APPID}",
                    geo: "${'$'}{COMPASS_EXT_GEO}"
                  });
                })
                </script>
            
            """.trimIndent()

        // パラメータを取得できるかのテストは実装済みのため、パラメータを表示できるかのテストのみを記載
        @Test
        @DisplayName("正常")
        fun isCorrect() =
            assertEquals(expectedValue, sut.generateVueComponentWithHeader(ENCRYPTED_SPOT_ID, SiteType.a_app, true))
    }

    @Nested
    @DisplayName("generateVueComponentWithoutHeaderのテスト")
    inner class GenerateVueComponentWithoutHeaderTest {
        val expectedValue =
            """
                <template>
                  <div :id="spotId" :width="width" :height="height">
                    <component is="script" type="text/javascript">
                      var mangakaCompass = mangakaCompass || {};
                      mangakaCompass.queue = mangakaCompass.queue || [];
                    </component>
                    <component
                      is="script"
                      type="text/javascript"
                      charset="UTF-8"
                      src="//j.mangaka.net/js/compass.js"
                      onload="new mangakaCompass.AdInitializer().initialize();"
                    />
                  </div>
                </template>
                
                <script setup lang="ts">
                import {onMounted} from "vue";
                
                const props = defineProps({spotId: String, width: String, height: String});
                
                onMounted(() => {
                  // @ts-ignore
                  mangakaCompass.queue.push({
                    spot: props.spotId,
                    url: "${'$'}{COMPASS_EXT_URL}",
                    referrer: "${'$'}{COMPASS_EXT_REF}",
                    ifa: "${'$'}{COMPASS_EXT_IFA}",
                    appid: "${'$'}{COMPASS_EXT_APPID}",
                    geo: "${'$'}{COMPASS_EXT_GEO}"
                  });
                })
                </script>
            
            """.trimIndent()

        // パラメータを取得できるかのテストは実装済みのため、パラメータを表示できるかのテストのみを記載
        @Test
        @DisplayName("正常")
        fun isCorrect() =
            assertEquals(expectedValue, sut.generateVueComponentWithoutHeader(ENCRYPTED_SPOT_ID, SiteType.a_app, true))
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpotTagのテスト")
    inner class GetSpotTagTest {
        val PREBID_JS = UpstreamType.prebidjs
        val NONE = UpstreamType.none
        val INLINE = DisplayType.inline
        val OVERLAY = DisplayType.overlay
        val INTERSTITIAL = DisplayType.interstitial
        val PC_WEB = SiteType.pc_web
        val SP_WEB = SiteType.sp_web
        val A_APP = SiteType.a_app
        val I_APP = SiteType.i_app
        val JS = DeliveryMethod.js
        val SDK = DeliveryMethod.sdk

        private fun validParams() = listOf(
            Arguments.of(1, PREBID_JS, INLINE, PC_WEB, JS, 1, 0, 0, 0),
            Arguments.of(2, PREBID_JS, INLINE, PC_WEB, SDK, 1, 0, 0, 0),
            Arguments.of(3, PREBID_JS, INLINE, SP_WEB, JS, 1, 0, 0, 0),
            Arguments.of(4, PREBID_JS, INLINE, SP_WEB, SDK, 1, 0, 0, 0),
            Arguments.of(5, PREBID_JS, INLINE, I_APP, JS, 1, 0, 0, 0),
            Arguments.of(6, PREBID_JS, INLINE, I_APP, SDK, 1, 0, 0, 0),
            Arguments.of(7, PREBID_JS, INLINE, A_APP, JS, 1, 0, 0, 0),
            Arguments.of(8, PREBID_JS, INLINE, A_APP, SDK, 1, 0, 0, 0),
            Arguments.of(9, PREBID_JS, OVERLAY, PC_WEB, JS, 1, 0, 0, 0),
            Arguments.of(10, PREBID_JS, OVERLAY, PC_WEB, SDK, 1, 0, 0, 0),
            Arguments.of(11, PREBID_JS, OVERLAY, SP_WEB, JS, 1, 0, 0, 0),
            Arguments.of(12, PREBID_JS, OVERLAY, SP_WEB, SDK, 1, 0, 0, 0),
            Arguments.of(13, PREBID_JS, OVERLAY, I_APP, JS, 1, 0, 0, 0),
            Arguments.of(14, PREBID_JS, OVERLAY, I_APP, SDK, 1, 0, 0, 0),
            Arguments.of(15, PREBID_JS, OVERLAY, A_APP, JS, 1, 0, 0, 0),
            Arguments.of(16, PREBID_JS, OVERLAY, A_APP, SDK, 1, 0, 0, 0),
            Arguments.of(17, PREBID_JS, INTERSTITIAL, PC_WEB, JS, 1, 0, 0, 0),
            Arguments.of(18, PREBID_JS, INTERSTITIAL, PC_WEB, SDK, 1, 0, 0, 0),
            Arguments.of(19, PREBID_JS, INTERSTITIAL, SP_WEB, JS, 1, 0, 0, 0),
            Arguments.of(20, PREBID_JS, INTERSTITIAL, SP_WEB, SDK, 1, 0, 0, 0),
            Arguments.of(21, PREBID_JS, INTERSTITIAL, I_APP, JS, 1, 0, 0, 0),
            Arguments.of(22, PREBID_JS, INTERSTITIAL, I_APP, SDK, 1, 0, 0, 0),
            Arguments.of(23, PREBID_JS, INTERSTITIAL, A_APP, JS, 1, 0, 0, 0),
            Arguments.of(24, PREBID_JS, INTERSTITIAL, A_APP, SDK, 1, 0, 0, 0),
            Arguments.of(25, NONE, INLINE, PC_WEB, JS, 0, 1, 0, 0),
            Arguments.of(26, NONE, INLINE, PC_WEB, SDK, 0, 1, 0, 0),
            Arguments.of(27, NONE, INLINE, SP_WEB, JS, 0, 1, 0, 0),
            Arguments.of(28, NONE, INLINE, SP_WEB, SDK, 0, 1, 0, 0),
            Arguments.of(29, NONE, INLINE, I_APP, JS, 0, 0, 0, 1),
            Arguments.of(30, NONE, INLINE, I_APP, SDK, 0, 0, 0, 0),
            Arguments.of(31, NONE, INLINE, A_APP, JS, 0, 0, 0, 1),
            Arguments.of(32, NONE, INLINE, A_APP, SDK, 0, 0, 0, 0),
            Arguments.of(33, NONE, OVERLAY, PC_WEB, JS, 0, 0, 1, 0),
            Arguments.of(34, NONE, OVERLAY, PC_WEB, SDK, 0, 0, 1, 0),
            Arguments.of(35, NONE, OVERLAY, SP_WEB, JS, 0, 0, 1, 0),
            Arguments.of(36, NONE, OVERLAY, SP_WEB, SDK, 0, 0, 1, 0),
            Arguments.of(37, NONE, OVERLAY, I_APP, JS, 0, 0, 0, 1),
            Arguments.of(38, NONE, OVERLAY, I_APP, SDK, 0, 0, 0, 0),
            Arguments.of(39, NONE, OVERLAY, A_APP, JS, 0, 0, 0, 1),
            Arguments.of(40, NONE, OVERLAY, A_APP, SDK, 0, 0, 0, 0),
            Arguments.of(41, NONE, INTERSTITIAL, PC_WEB, JS, 0, 0, 1, 0),
            Arguments.of(42, NONE, INTERSTITIAL, PC_WEB, SDK, 0, 0, 1, 0),
            Arguments.of(43, NONE, INTERSTITIAL, SP_WEB, JS, 0, 0, 1, 0),
            Arguments.of(44, NONE, INTERSTITIAL, SP_WEB, SDK, 0, 0, 1, 0),
            Arguments.of(45, NONE, INTERSTITIAL, I_APP, JS, 0, 0, 0, 1),
            Arguments.of(46, NONE, INTERSTITIAL, I_APP, SDK, 0, 0, 0, 0),
            Arguments.of(47, NONE, INTERSTITIAL, A_APP, JS, 0, 0, 0, 1),
            Arguments.of(48, NONE, INTERSTITIAL, A_APP, SDK, 0, 0, 0, 0),
        )

        @ParameterizedTest
        @MethodSource("validParams")
        @DisplayName("spotTag生成関数の分岐テスト")
        fun branchTest(
            id: Int,
            upstreamType: UpstreamType,
            displayType: DisplayType,
            siteType: SiteType,
            deliveryMethod: DeliveryMethod,
            count1: Int,
            count2: Int,
            count3: Int,
            count4: Int
        ) {
            val spot = setMockSpot(id, displayType, upstreamType, deliveryMethod)
            val site = setMockSite(id, siteType)

            doReturn(spot).whenever(spotGetWithCheckHelper).getSpotWithCheck(SpotId(id), SpotStatus.entries)
            doReturn(site).whenever(spotGetWithCheckHelper)
                .getSiteWithCheck(coAccountId, SiteId(id), SiteStatus.entries)
            doReturn(id.toString()).whenever(cryptUtils).encryptForTag(any())

            sut.getSpotTag(coAccountId, SpotId(id))
            verify(sut, times(count1)).generatePrebidTag(eq(id.toString()), any(), any(), any())
            verify(sut, times(count2)).generateJsWebTag(eq(id.toString()), any(), eq(100), eq(300))
            verify(sut, times(count3)).generateJsWebTag(eq(id.toString()), any(), eq(null), eq(null))
            verify(sut, times(count4)).generateJsAppTag(eq(id.toString()))
        }

        private fun setMockSpot(
            id: Int,
            displayType: DisplayType,
            upstreamType: UpstreamType,
            deliveryMethod: DeliveryMethod
        ) = Spot(
            SpotId(id), SiteId(id), "[TEXT]_", SpotStatus.archive, PlatformId(3),
            displayType, upstreamType, deliveryMethod, 100, 300, 20,
            true, ProprietyDspId(20), Spot.Anonymous.off, BigDecimal(0.5), "[TEXT]descriptions3",
            "https://test.com/3/", mock()
        )

        private fun setMockSite(id: Int, siteType: SiteType) = Site(
            SiteId(id), CoAccountId(1), "サイト1", SiteStatus.active, true, PlatformId(1), siteType,
            ProprietyDspId(1), LocalDateTime.parse("2023-01-01T00:00:00")
        )
    }
}
