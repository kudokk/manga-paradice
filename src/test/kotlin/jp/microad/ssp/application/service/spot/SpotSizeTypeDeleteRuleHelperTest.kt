package jp.mangaka.ssp.application.service.spot

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.service.spot.validation.banner.SpotSizeTypeDeleteRule
import jp.mangaka.ssp.application.valueobject.creative.CreativeId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.dao.creative.Creative
import jp.mangaka.ssp.infrastructure.datasource.dao.creative.Creative.CreativeStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.creative.CreativeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetype
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetypeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relaystructcreative.RelayStructCreative
import jp.mangaka.ssp.infrastructure.datasource.dao.relaystructcreative.RelayStructCreativeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relaystructspot.RelayStructSpot
import jp.mangaka.ssp.infrastructure.datasource.dao.relaystructspot.RelayStructSpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructDao
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SpotSizeTypeDeleteRuleHelperのテスト")
private class SpotSizeTypeDeleteRuleHelperTest {
    companion object {
        val spotId1 = SpotId(1)
        val spotId2 = SpotId(2)
        val spotId3 = SpotId(3)
        val structId1 = StructId(1)
        val structId2 = StructId(2)
        val structId3 = StructId(3)
        val structId4 = StructId(4)
        val creativeId1 = CreativeId(1)
        val creativeId2 = CreativeId(2)
        val creativeId3 = CreativeId(3)
        val creativeId4 = CreativeId(4)
        val sizeTypeId1 = SizeTypeId(1)
        val sizeTypeId2 = SizeTypeId(2)
        val sizeTypeId3 = SizeTypeId(3)
    }

    val creativeDao: CreativeDao = mock()
    val relaySpotSizetypeDao: RelaySpotSizetypeDao = mock()
    val relayStructCreativeDao: RelayStructCreativeDao = mock()
    val relayStructSpotDao: RelayStructSpotDao = mock()
    val structDao: StructDao = mock()

    val sut = spy(
        SpotSizeTypeDeleteRuleHelper(
            creativeDao, relaySpotSizetypeDao, relayStructCreativeDao, relayStructSpotDao, structDao
        )
    )

    @Nested
    @DisplayName("getSpotSizeTypeDeleteRuleのテスト")
    inner class GetSpotSizeTypeDeleteRuleTest {
        val structSpotMap = mapOf(
            structId1 to listOf(spotId1, spotId2, spotId3),
            structId2 to listOf(spotId1, spotId2),
            structId3 to listOf(spotId3)
        )
        val structCreativeSizeTypeMap: Map<StructId, List<SizeTypeId>> = mock()
        val spotSizeTypeMap: Map<SpotId, List<SizeTypeId>> = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(structSpotMap).whenever(sut).getStructSpotMap(any())
            doReturn(structCreativeSizeTypeMap).whenever(sut).getStructCreativeSizeTypeMap(any())
            doReturn(spotSizeTypeMap).whenever(sut).getSpotSizeTypeMap(any())

            val actual = sut.getRule(spotId1)

            assertEquals(
                SpotSizeTypeDeleteRule(structSpotMap, structCreativeSizeTypeMap, spotSizeTypeMap),
                actual
            )
            verify(sut, times(1)).getStructSpotMap(spotId1)
            verify(sut, times(1)).getStructCreativeSizeTypeMap(setOf(structId1, structId2, structId3))
            verify(sut, times(1)).getSpotSizeTypeMap(listOf(spotId1, spotId2, spotId3))
        }
    }

    @Nested
    @DisplayName("getStructSpotMapのテスト")
    inner class GetStructSpotMapTest {
        val relayStructSpotsOfSpotId1 = listOf(
            mockRelayStructSpot(structId1, spotId1),
            mockRelayStructSpot(structId2, spotId1),
            mockRelayStructSpot(structId3, spotId1),
            mockRelayStructSpot(structId4, spotId1)
        )
        val structs = listOf(structId1, structId2, structId3).map { mockStruct(it) }
        val relaySpotStructs = listOf(
            mockRelayStructSpot(structId1, spotId1),
            mockRelayStructSpot(structId1, spotId2),
            mockRelayStructSpot(structId1, spotId3),
            mockRelayStructSpot(structId2, spotId1),
            mockRelayStructSpot(structId2, spotId2),
            mockRelayStructSpot(structId3, spotId2)
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(relayStructSpotsOfSpotId1).whenever(relayStructSpotDao).selectBySpotId(any())
            doReturn(structs).whenever(structDao).selectByIdsAndStatuses(any(), any())
            doReturn(relaySpotStructs).whenever(relayStructSpotDao).selectByStructIds(any())

            val actual = sut.getStructSpotMap(spotId1)

            assertEquals(
                mapOf(
                    structId1 to listOf(spotId1, spotId2, spotId3),
                    structId2 to listOf(spotId1, spotId2),
                    structId3 to listOf(spotId2)
                ),
                actual
            )
            verify(relayStructSpotDao, times(1)).selectBySpotId(spotId1)
            verify(structDao, times(1)).selectByIdsAndStatuses(
                listOf(structId1, structId2, structId3, structId4),
                StructStatus.viewableStatuses
            )
            verify(relayStructSpotDao, times(1)).selectByStructIds(listOf(structId1, structId2, structId3))
        }
    }

    @Nested
    @DisplayName("getStructCreativeSizeTypeMapのテスト")
    inner class GetStructCreativeSizeTypeMapTest {
        val structIds = listOf(structId1, structId2, structId3)
        val relayStructCreatives = listOf(
            mockRelayStructCreative(structId1, creativeId1),
            mockRelayStructCreative(structId1, creativeId2),
            mockRelayStructCreative(structId2, creativeId1),
            mockRelayStructCreative(structId2, creativeId3),
            mockRelayStructCreative(structId3, creativeId2),
            mockRelayStructCreative(structId3, creativeId4),
        )
        val creatives = listOf(
            mockCreative(creativeId1, sizeTypeId2),
            mockCreative(creativeId2, sizeTypeId1),
            mockCreative(creativeId3, sizeTypeId3)
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(relayStructCreatives).whenever(relayStructCreativeDao).selectByStructIds(any())
            doReturn(creatives).whenever(creativeDao).selectByIdsAndStatuses(any(), any())

            val actual = sut.getStructCreativeSizeTypeMap(structIds)

            assertEquals(
                mapOf(
                    structId1 to listOf(sizeTypeId2, sizeTypeId1),
                    structId2 to listOf(sizeTypeId2, sizeTypeId3),
                    structId3 to listOf(sizeTypeId1)
                ),
                actual
            )
            verify(relayStructCreativeDao, times(1)).selectByStructIds(structIds)
            verify(creativeDao, times(1)).selectByIdsAndStatuses(
                listOf(creativeId1, creativeId2, creativeId1, creativeId3, creativeId2, creativeId4),
                CreativeStatus.viewableStatuses
            )
        }
    }

    @Nested
    @DisplayName("getSpotSizeTypeMapのテスト")
    inner class GetSpotSizeTypeMapTest {
        val relaySpotSizeTypes = listOf(
            mockRelaySpotSizeType(spotId1, sizeTypeId1),
            mockRelaySpotSizeType(spotId1, sizeTypeId2),
            mockRelaySpotSizeType(spotId1, sizeTypeId3),
            mockRelaySpotSizeType(spotId2, sizeTypeId1),
            mockRelaySpotSizeType(spotId2, sizeTypeId3),
            mockRelaySpotSizeType(spotId3, sizeTypeId2)
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(relaySpotSizeTypes).whenever(relaySpotSizetypeDao).selectBySpotIds(any())

            val actual = sut.getSpotSizeTypeMap(listOf(spotId1, spotId2, spotId3))

            assertEquals(
                mapOf(
                    spotId1 to listOf(sizeTypeId1, sizeTypeId2, sizeTypeId3),
                    spotId2 to listOf(sizeTypeId1, sizeTypeId3),
                    spotId3 to listOf(sizeTypeId2)
                ),
                actual
            )
        }
    }

    fun mockRelayStructSpot(structId: StructId, spotId: SpotId): RelayStructSpot = mock {
        on { this.structId } doReturn structId
        on { this.spotId } doReturn spotId
    }

    fun mockStruct(structId: StructId): StructCo = mock {
        on { this.structId } doReturn structId
    }

    fun mockRelayStructCreative(structId: StructId, creativeId: CreativeId): RelayStructCreative = mock {
        on { this.structId } doReturn structId
        on { this.creativeId } doReturn creativeId
    }

    fun mockCreative(creativeId: CreativeId, sizeTypeId: SizeTypeId): Creative = mock {
        on { this.creativeId } doReturn creativeId
        on { this.sizeTypeId } doReturn sizeTypeId
    }

    fun mockRelaySpotSizeType(spotId: SpotId, sizeTypeId: SizeTypeId): RelaySpotSizetype = mock {
        on { this.spotId } doReturn spotId
        on { this.sizeTypeId } doReturn sizeTypeId
    }
}
