package jp.mangaka.ssp.application.service.spot

import jp.mangaka.ssp.application.service.spot.validation.banner.SpotSizeTypeDeleteRule
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.dao.creative.Creative.CreativeStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.creative.CreativeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetypeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relaystructcreative.RelayStructCreativeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relaystructspot.RelayStructSpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructDao
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Component

@Component
class SpotSizeTypeDeleteRuleHelper(
    private val creativeDao: CreativeDao,
    private val relaySpotSizetypeDao: RelaySpotSizetypeDao,
    private val relayStructCreativeDao: RelayStructCreativeDao,
    private val relayStructSpotDao: RelayStructSpotDao,
    private val structDao: StructDao
) {
    /**
     * @param spotId 広告枠
     * @return 広告枠のサイズ種別の削除ルール
     */
    fun getRule(spotId: SpotId): SpotSizeTypeDeleteRule {
        val structSpotMap = getStructSpotMap(spotId)

        return SpotSizeTypeDeleteRule(
            structSpotMap,
            getStructCreativeSizeTypeMap(structSpotMap.keys),
            getSpotSizeTypeMap(structSpotMap.values.flatten().distinct())
        )
    }

    @TestOnly
    fun getStructSpotMap(spotId: SpotId): Map<StructId, List<SpotId>> {
        val relayStructSpots = relayStructSpotDao.selectBySpotId(spotId)
        val structs = structDao.selectByIdsAndStatuses(
            relayStructSpots.map { it.structId },
            StructStatus.viewableStatuses
        )

        return relayStructSpotDao
            .selectByStructIds(structs.map { it.structId })
            .groupBy({ it.structId }, { it.spotId })
    }

    @TestOnly
    fun getStructCreativeSizeTypeMap(structIds: Collection<StructId>): Map<StructId, List<SizeTypeId>> {
        val relayStructCreatives = relayStructCreativeDao.selectByStructIds(structIds)
        val creativeMap = creativeDao.selectByIdsAndStatuses(
            relayStructCreatives.map { it.creativeId },
            CreativeStatus.viewableStatuses
        ).associateBy { it.creativeId }

        return relayStructCreatives
            .mapNotNull {
                val sizeTypeId = creativeMap[it.creativeId]?.sizeTypeId ?: return@mapNotNull null
                it.structId to sizeTypeId
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { it.value.distinct() }
    }

    @TestOnly
    fun getSpotSizeTypeMap(spotIds: Collection<SpotId>): Map<SpotId, List<SizeTypeId>> =
        relaySpotSizetypeDao
            .selectBySpotIds(spotIds)
            .groupBy({ it.spotId }, { it.sizeTypeId })
}
