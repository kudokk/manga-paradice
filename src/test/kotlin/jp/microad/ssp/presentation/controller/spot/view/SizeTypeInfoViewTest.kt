package jp.mangaka.ssp.presentation.controller.spot.view

import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SizeTypeInfoViewのテスト")
private class SizeTypeInfoViewTest {
    companion object {
        val sizeTypeId1 = SizeTypeId(1)
        val sizeTypeId2 = SizeTypeId(2)
        val sizeTypeId3 = SizeTypeId(3)
        val platformId1 = PlatformId(1)
        val platformId2 = PlatformId(2)
    }

    @Nested
    @DisplayName("ofのテスト")
    inner class OfTest {
        @Test
        @DisplayName("データあり")
        fun isNotEmpty() {
            val actual = SizeTypeInfoView.of(
                listOf(
                    SizeTypeInfo(sizeTypeId1, 100, 200, platformId1, SizeTypeInfo.DefinitionType.standard),
                    SizeTypeInfo(sizeTypeId2, 300, 400, platformId1, SizeTypeInfo.DefinitionType.standard),
                    SizeTypeInfo(sizeTypeId3, 500, 600, platformId2, SizeTypeInfo.DefinitionType.userdefined),
                )
            )

            assertEquals(
                listOf(
                    SizeTypeInfoView(sizeTypeId1, 100, 200, platformId1, SizeTypeInfo.DefinitionType.standard),
                    SizeTypeInfoView(sizeTypeId2, 300, 400, platformId1, SizeTypeInfo.DefinitionType.standard),
                    SizeTypeInfoView(sizeTypeId3, 500, 600, platformId2, SizeTypeInfo.DefinitionType.userdefined)
                ),
                actual
            )
        }

        @Test
        @DisplayName("データなし")
        fun isEmpty() {
            val actual = SizeTypeInfoView.of(emptyList())

            assertTrue(actual.isEmpty())
        }
    }
}
