package jp.mangaka.ssp.application.service.struct

import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructDao
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.springframework.stereotype.Component

@Component
class StructGetWithCheckHelper(
    private val structDao: StructDao
) {
    /**
     * ストラクトID・ストラクトステータスに合致するストラクトがすべて存在する場合のみ取得する.
     *
     * @param structIds ストラクトIDのリスト
     * @param statuses ストラクトステータスのリスト
     * @return 引数のストラクトID・ストラクトステータスに合致する Struct のリスト
     * @throws CompassManagerException ストラクトID・ストラクトステータスに合致しないストラクトが含まれているとき
     */
    fun getStructsWithCheck(
        structIds: Collection<StructId>,
        statuses: Collection<StructStatus>
    ): List<StructCo> = structDao
        .selectByIdsAndStatuses(structIds, statuses)
        .also { entities ->
            val notFoundIds = structIds - entities.map { it.structId }.toSet()

            if (notFoundIds.isNotEmpty()) {
                throw CompassManagerException(
                    "ストラクトID：$notFoundIds/ストラクトステータス：${statuses}に合致するエンティティが取得できませんでした。"
                )
            }
        }
}
