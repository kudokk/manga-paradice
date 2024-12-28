package jp.mangaka.ssp.infrastructure.datasource.dao.operationlog

interface CompassUserOperationLogDao {
    /**
     * ユーザー操作履歴の登録を行う
     *
     * @param userOperationLog ユーザー操作履歴
     */
    fun insert(userOperationLog: CompassUserOperationLogInsert)
}
