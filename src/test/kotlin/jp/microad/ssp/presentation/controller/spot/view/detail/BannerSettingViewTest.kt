package jp.mangaka.ssp.presentation.controller.spot.view.detail

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.infrastructure.datasource.dao.decoration.Decoration
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo.DefinitionType
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplay
import jp.mangaka.ssp.presentation.controller.spot.view.DecorationView
import jp.mangaka.ssp.presentation.controller.spot.view.SizeTypeInfoView
import jp.mangaka.ssp.presentation.controller.spot.view.detail.CloseButtonView.ColorView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("BannerSettingViewのテスト")
private class BannerSettingViewTest {
    companion object {
        val sizeTypeId1 = SizeTypeId(1)
        val sizeTypeId2 = SizeTypeId(2)
        val sizeTypeId3 = SizeTypeId(3)
        val platformId1 = PlatformId(1)
        val platformId2 = PlatformId(2)
        val decorationId1 = DecorationId(1)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val spotBannerDisplay1 = SpotBannerDisplay(
            mock(), null, null, null, null, true, false, mock(), 1, 200, "rgba(1,23,456,0.1)",
            "rgba(23,456,1,1.0)", "rgba(456,1,23,0.7)"
        )
        val spotBannerDisplay2 = SpotBannerDisplay(
            mock(), null, null, null, null, false, true, mock(), null, null, null, null, null
        )
        val sizeTypeInfos = listOf(
            SizeTypeInfo(sizeTypeId1, 100, 200, platformId1, DefinitionType.standard),
            SizeTypeInfo(sizeTypeId2, 300, 400, platformId2, DefinitionType.standard),
            SizeTypeInfo(sizeTypeId3, 500, 600, platformId1, DefinitionType.userdefined)
        )
        val decoration = Decoration(decorationId1, "decoration1", mock(), 100, "bgColor", "bandText", "bandColor")

        @Test
        @DisplayName("全項目入力あり")
        fun isFull() {
            val actual = BannerSettingView.of(spotBannerDisplay1, sizeTypeInfos, decoration)

            assertEquals(
                BannerSettingView(
                    listOf(
                        SizeTypeInfoView(sizeTypeId1, 100, 200, platformId1, DefinitionType.standard),
                        SizeTypeInfoView(sizeTypeId2, 300, 400, platformId2, DefinitionType.standard),
                        SizeTypeInfoView(sizeTypeId3, 500, 600, platformId1, DefinitionType.userdefined)
                    ),
                    true,
                    false,
                    CloseButtonView(
                        1, 200, ColorView(1, 23, 456, 0.1), ColorView(23, 456, 1, 1.0), ColorView(456, 1, 23, 0.7)
                    ),
                    DecorationView(decorationId1, "decoration1", 100, "bgColor", "bandText", "bandColor")
                ),
                actual
            )
        }

        @Test
        @DisplayName("必須項目のみ")
        fun isNotFull() {
            val actual = BannerSettingView.of(spotBannerDisplay2, sizeTypeInfos, null)

            assertEquals(
                BannerSettingView(
                    listOf(
                        SizeTypeInfoView(sizeTypeId1, 100, 200, platformId1, DefinitionType.standard),
                        SizeTypeInfoView(sizeTypeId2, 300, 400, platformId2, DefinitionType.standard),
                        SizeTypeInfoView(sizeTypeId3, 500, 600, platformId1, DefinitionType.userdefined)
                    ),
                    false,
                    true,
                    null,
                    null
                ),
                actual
            )
        }
    }
}
