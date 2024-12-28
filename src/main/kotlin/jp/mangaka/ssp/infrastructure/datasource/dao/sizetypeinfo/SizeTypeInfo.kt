package jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo

import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId

data class SizeTypeInfo(
    val sizeTypeId: SizeTypeId,
    val height: Int,
    val width: Int,
    val platformId: PlatformId,
    val definitionType: DefinitionType
) {
    enum class DefinitionType {
        standard, userdefined
    }
}
