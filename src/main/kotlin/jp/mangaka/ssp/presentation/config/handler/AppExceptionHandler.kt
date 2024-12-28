package jp.mangaka.ssp.presentation.config.handler

import ch.qos.logback.classic.Level
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.user.UserId
import jp.mangaka.ssp.presentation.config.secutiry.AccountUserDetails
import jp.mangaka.ssp.util.exception.CompassManagerException
import jp.mangaka.ssp.util.exception.FormatValidationException
import jp.mangaka.ssp.util.exception.ResourceConflictException
import org.jetbrains.annotations.TestOnly
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestUtils
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class AppExceptionHandler(@Value("\${logging.level.org.springframework.web}") logLevel: Level) {
    private val logger = LoggerFactory.getLogger(AppExceptionHandler::class.java).also {
        (it as ch.qos.logback.classic.Logger).level = logLevel
    }

    /**
     * 500エラーの例外ハンドリング
     * @return エラーメッセージのレスポンス
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CompassManagerException::class)
    fun bidderManageError(e: CompassManagerException): Map<String, Any> {
        logger.error("${logPrefix()}Internal Server Error", e)
        return mapOf("message" to "Internal Server Error")
    }

    /**
     * 404エラーの例外ハンドリング
     * 存在しないページへのアクセス時ににハンドリングされる。
     * @return エラーメッセージのレスポンス
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(
        NoHandlerFoundException::class,
        MissingServletRequestParameterException::class
    )
    fun urlNotFoundError(e: Exception): Map<String, Any> {
        logger.info("${logPrefix()}Not Found", e)
        return mapOf("message" to "Not Found")
    }

    /**
     * 403エラーの例外ハンドリング
     * 権限が不足している場合にハンドリングされる。
     * @return エラーメッセージのレスポンス
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException::class)
    fun resourceAccessDeniedError(e: AccessDeniedException): Map<String, Any> {
        logger.debug("${logPrefix()}Access Denied", e)
        return mapOf("message" to "Access Denied")
    }

    /**
     * 入力フォームの不正リクエスト(400エラー)の例外ハンドリング
     * @return エラーメッセージのレスポンス
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FormatValidationException::class)
    fun formatValidationError(e: FormatValidationException): Map<String, Any> {
        logger.debug("${logPrefix()}Invalid Input", e)
        return mapOf(
            "message" to "Failed Validation",
            "errorFields" to e.errorFields
        )
    }

    /**
     * 不正なリクエストパラメーター(400エラー)の例外ハンドリング
     * @return エラーメッセージのレスポンス
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun invalidRequestParamError(e: Exception?): Map<String, Any> {
        logger.debug("${logPrefix()}Bad Request", e)
        return mapOf("message" to "Invalid Request Parameter")
    }

    /**
     * 編集がコンフリクトした場合(409エラー)の例外ハンドリング
     * @return エラーメッセージのレスポンス
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ResourceConflictException::class)
    fun resourceConflictError(e: ResourceConflictException): Map<String, Any> {
        logger.debug("${logPrefix()}Resource Conflict", e)
        return mapOf("message" to "Resource Conflict")
    }

    @TestOnly
    fun logPrefix(): String = "[${userId()?.value}][${coAccountId()?.value}] "

    @TestOnly
    fun userId(): UserId? = SecurityContextHolder.getContext().authentication?.principal?.let {
        (it as AccountUserDetails).user.userId
    }

    @TestOnly
    fun coAccountId(): CoAccountId? = ServletRequestUtils.getIntParameter(
        (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request,
        "coAccountId"
    )?.let { CoAccountId(it) }
}
