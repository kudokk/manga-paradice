package jp.mangaka.ssp.util.exception

/**
 * システムエラー（HttpStatus=500)に対応するException
 */
class CompassManagerException : RuntimeException {
    constructor(message: String) : super(message) {
        stackTrace = filterStackTrace(stackTrace)
    }

    constructor(message: String, t: Throwable) : super(message, t) {
        stackTrace = filterStackTrace(stackTrace)
    }
}
