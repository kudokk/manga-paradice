package jp.mangaka.ssp.application.service.spot.validation.banner

import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId

/** 広告枠に設定されているサイズ種別の削除に関するルール */
data class SpotSizeTypeDeleteRule(
    // ストラクトに紐づく広告枠
    private val structSpotMap: Map<StructId, Collection<SpotId>>,
    // ストラクトに紐づくクリエイティブに設定されているサイズ種別
    private val structCreativeSizeTypeMap: Map<StructId, Collection<SizeTypeId>>,
    // 広告枠に紐づくサイズ種別
    private val spotSizeTypeMap: Map<SpotId, Collection<SizeTypeId>>
) {
    /**
     * @param spotId 広告枠ID
     * @param sizeTypeId サイズ種別ID
     * @return 対象広告枠から対象サイズを削除しようとしたとき制約違反となるストラクトIDのリスト
     */
    fun unDeletableStructIds(spotId: SpotId, sizeTypeId: SizeTypeId): List<StructId> {
        val structIds = structCreativeSizeTypeMap.filter { it.value.contains(sizeTypeId) }.keys

        // ストラクトに紐づくクリエイティブで利用がなければ削除可能
        if (structIds.isEmpty()) return emptyList()

        return structIds.filter { structId ->
            val spotIds = structSpotMap[structId] ?: return@filter false

            // 対象広告枠を除く広告枠以外で対象サイズの利用がない場合は削除不可
            !spotIds
                .filter { it != spotId }
                .map { spotSizeTypeMap[it] ?: emptyList() }
                .flatten()
                .contains(sizeTypeId)
        }
    }
}
