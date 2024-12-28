package jp.mangaka.ssp.infrastructure.datasource.dao.creative

import jp.mangaka.ssp.application.valueobject.creative.CreativeId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId

data class Creative(
    val creativeId: CreativeId,
    val creativeStatus: CreativeStatus,
    val sizeTypeId: SizeTypeId
) {
    enum class CreativeStatus {
        stop, active, archive, history;

        companion object {
            /** 閲覧可能なステータスのリスト */
            val viewableStatuses = listOf(stop, active, archive)
        }
    }
}
