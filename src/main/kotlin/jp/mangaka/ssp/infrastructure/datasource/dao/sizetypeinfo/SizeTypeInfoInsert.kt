package jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo

import jp.mangaka.ssp.application.valueobject.platform.PlatformId

data class SizeTypeInfoInsert(
    val width: Int,
    val height: Int,
    val platformId: PlatformId
)
