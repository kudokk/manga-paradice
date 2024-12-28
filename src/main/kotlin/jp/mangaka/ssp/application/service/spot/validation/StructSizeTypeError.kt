package jp.mangaka.ssp.application.service.spot.validation

import jakarta.validation.constraints.Null
import jp.mangaka.ssp.application.service.spot.validation.banner.SpotSizeTypeDeleteRule
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo

data class StructSizeTypeError(
    @field:Null(message = "\${validatedValue}")
    private val width: Int,
    @field:Null(message = "\${validatedValue}")
    private val height: Int,
    @field:Null(message = "\${validatedValue}")
    private val structIds: List<StructId>
) {
    companion object {
        /**
         * @param spotId 広告枠ID
         * @param deleteSpotSizeTypes 削除対象のサイズ種別
         * @param spotSizeTypeDeleteRule 広告枠サイズ種別紐づけの削除ルール
         * @return ストラクト紐づけエラーのリスト
         */
        fun of(
            spotId: SpotId,
            deleteSpotSizeTypes: Collection<SizeTypeInfo>,
            spotSizeTypeDeleteRule: SpotSizeTypeDeleteRule
        ): List<StructSizeTypeError> = deleteSpotSizeTypes.mapNotNull {
            val structIds = spotSizeTypeDeleteRule.unDeletableStructIds(spotId, it.sizeTypeId)

            if (structIds.isNotEmpty()) {
                StructSizeTypeError(it.width, it.height, structIds)
            } else {
                null
            }
        }
    }
}
