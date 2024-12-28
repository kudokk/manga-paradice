package jp.mangaka.ssp.infrastructure.datasource.dao.relaystructcreative

import jp.mangaka.ssp.application.valueobject.struct.StructId

interface RelayStructCreativeDao {
    /**
     * @param structIds ストラクトIDのリスト
     * @return 引数のストラクトIDに合致する RelayStructCreative のリスト
     */
    fun selectByStructIds(structIds: Collection<StructId>): List<RelayStructCreative>
}
