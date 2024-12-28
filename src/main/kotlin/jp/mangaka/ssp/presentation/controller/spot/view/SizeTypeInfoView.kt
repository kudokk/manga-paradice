package jp.mangaka.ssp.presentation.controller.spot.view

import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo

data class SizeTypeInfoView(
    val sizeTypeId: SizeTypeId,
    val height: Int,
    val width: Int,
    val platformId: PlatformId,
    val definitionType: SizeTypeInfo.DefinitionType
) {
    companion object {
        /**
         * @param sizeTypeInfos サイズ種別情報尾のエンティティのリスト
         * @return サイズ種別情報尾一覧のView
         */
        fun of(sizeTypeInfos: Collection<SizeTypeInfo>): List<SizeTypeInfoView> =
            sizeTypeInfos.map {
                SizeTypeInfoView(it.sizeTypeId, it.height, it.width, it.platformId, it.definitionType)
            }
    }
}
