package jp.mangaka.ssp.application.service.spot.validation.banner

import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.util.TestUtils.assertEmpty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SpotSizeTypeDeleteRuleのテスト")
private class SpotSizeTypeDeleteRuleTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
        val spotId3 = SpotId(3)
        val spotId4 = SpotId(4)
        val structId1 = StructId(1)
        val structId2 = StructId(2)
        val structId3 = StructId(3)
        val structId4 = StructId(4)
        val sizeTypeId1 = SizeTypeId(1)
        val sizeTypeId2 = SizeTypeId(2)
        val sizeTypeId3 = SizeTypeId(3)
        val sizeTypeId4 = SizeTypeId(4)
    }

    @Nested
    @DisplayName("unDeletableStructIdsのテスト")
    inner class UnDeletableStructIdsTest {
        val sut = SpotSizeTypeDeleteRule(
            mapOf(
                structId1 to listOf(spotId1, spotId2, spotId3, spotId4),
                structId2 to listOf(spotId1, spotId3, spotId4),
                structId3 to listOf(spotId1, spotId2, spotId3),
                structId4 to listOf(spotId1, spotId3)
            ),
            mapOf(
                structId1 to listOf(sizeTypeId1, sizeTypeId2, sizeTypeId3),
                structId2 to listOf(sizeTypeId1, sizeTypeId2),
                structId3 to listOf(sizeTypeId1, sizeTypeId3),
                structId4 to listOf(sizeTypeId2, sizeTypeId3)
            ),
            mapOf(
                spotId1 to listOf(sizeTypeId1, sizeTypeId2, sizeTypeId3, sizeTypeId4),
                spotId2 to listOf(sizeTypeId2, sizeTypeId4),
                spotId3 to listOf(sizeTypeId1, sizeTypeId2, sizeTypeId4),
                spotId4 to listOf(sizeTypeId1, sizeTypeId2, sizeTypeId3)
            )
        )

        @Test
        @DisplayName("ストラクトに紐づくクリエイティブで利用がないサイズ")
        fun isNotUseStructCreative() {
            assertEmpty(sut.unDeletableStructIds(spotId1, sizeTypeId4))
        }

        @Test
        @DisplayName("ストラクトに対象広告枠以外で利用がないサイズがない")
        fun isUseOtherSpot() {
            assertEmpty(sut.unDeletableStructIds(spotId1, sizeTypeId2))
        }

        @Test
        @DisplayName("ストラクトに対象広告枠以外で利用がないサイズがある")
        fun isUnUseOtherSpot() {
            assertEquals(
                listOf(structId3, structId4),
                sut.unDeletableStructIds(spotId1, sizeTypeId3)
            )
        }
    }
}
