package jp.mangaka.ssp.util.exception

/**
 * 409エラーに対応する例外クラス
 * 編集がコンフリクトした時に発生する
 */
class ResourceConflictException(message: String) : RuntimeException(message) {
    init {
        stackTrace = filterStackTrace(stackTrace)
    }
}
