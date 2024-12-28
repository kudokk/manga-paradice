package jp.mangaka.ssp.application.service.spot.validation

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.service.spot.validation.banner.SpotSizeTypeDeleteRule
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("StructSizeTypeErrorのテスト")
private class StructSizeTypeErrorTest {
    companion object {
        val spotId = SpotId(1)
        val structId1 = StructId(1)
        val structId2 = StructId(2)
        val structId3 = StructId(3)
        val sizeTypeId1 = SizeTypeId(1)
        val sizeTypeId2 = SizeTypeId(2)
        val sizeTypeId3 = SizeTypeId(3)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val deleteSpotSizeTypes = listOf(
            mockSizeTypeInfo(sizeTypeId1, 100, 200),
            mockSizeTypeInfo(sizeTypeId2, 300, 400),
            mockSizeTypeInfo(sizeTypeId3, 500, 600)
        )
        val spotSizeTypeDeleteRule: SpotSizeTypeDeleteRule = mock {
            on { unDeletableStructIds(spotId, sizeTypeId1) } doReturn listOf(structId1, structId2, structId3)
            on { unDeletableStructIds(spotId, sizeTypeId2) } doReturn listOf(structId1, structId3)
            on { unDeletableStructIds(spotId, sizeTypeId3) } doReturn emptyList<StructId>()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = StructSizeTypeError.of(spotId, deleteSpotSizeTypes, spotSizeTypeDeleteRule)

            assertEquals(
                listOf(
                    StructSizeTypeError(100, 200, listOf(structId1, structId2, structId3)),
                    StructSizeTypeError(300, 400, listOf(structId1, structId3))
                ),
                actual
            )
        }
    }

    fun mockSizeTypeInfo(sizeTypeId: SizeTypeId, width: Int, height: Int): SizeTypeInfo = mock {
        on { this.sizeTypeId } doReturn sizeTypeId
        on { this.width } doReturn width
        on { this.height } doReturn height
    }
}
