package jp.mangaka.ssp.util.aop

import jakarta.servlet.http.HttpServletRequest
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.user.UserId
import jp.mangaka.ssp.infrastructure.datasource.dao.operationlog.CompassUserOperationLogDao
import jp.mangaka.ssp.infrastructure.datasource.dao.operationlog.CompassUserOperationLogInsert
import jp.mangaka.ssp.presentation.config.secutiry.AccountUserDetails
import org.apache.commons.codec.binary.StringUtils
import org.apache.commons.lang3.ObjectUtils
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.jetbrains.annotations.TestOnly
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.core.SqlParameterValue
import org.springframework.jdbc.core.SqlProvider
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import org.springframework.jdbc.core.simple.AbstractJdbcInsert
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.ServletRequestUtils
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Aspect
@Component
class CompassUserOperationLogAspect(
    private val compassUserOperationLogDao: CompassUserOperationLogDao
) {
    private val logger = LoggerFactory.getLogger(CompassUserOperationLogAspect::class.java)

    @Before(
        "execution(* org.springframework.jdbc.core.JdbcTemplate.update" +
            "(String || org.springframework.jdbc.core.PreparedStatementCreator, ..))" +
            "|| execution(* org.springframework.jdbc.core.JdbcTemplate.batchUpdate" +
            "(String || org.springframework.jdbc.core.PreparedStatementCreator, ..))"
    )
    fun addUserOperationLog(joinPoint: JoinPoint) {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val ipAddress = getIpAddress(request)
        val coAccountId = getCoAccountId(request)
        val userId = getUserId()
        val queryParams = extractQueryParams(joinPoint)

        if (ObjectUtils.anyNotNull(ipAddress, coAccountId, userId)) {
            logger.warn(
                "cannot acquire the user info of this SQL [" +
                    "userId: $userId, coAccountId: $coAccountId, IP Address: $ipAddress]" +
                    "SQL is [" + queryParams.originalQuery + "]"
            )
        }

        if (queryParams.isChange()) {
            logger.warn(
                "this sql or sqlparameter can register only partly [" +
                    "userId: $userId, coAccountId: $coAccountId, IP Address: $ipAddress]" +
                    "SQL is [${queryParams.truncatedQuery}...] ${queryParams.originalQuery.length} words,  " +
                    "parameters ${queryParams.originalParamsSize} bytes."
            )
        }

        try {
            compassUserOperationLogDao.insert(
                CompassUserOperationLogInsert(
                    ipAddressToLong(ipAddress),
                    userId ?: UserId(0),
                    coAccountId ?: CoAccountId.zero,
                    queryParams.truncatedQuery,
                    queryParams.truncatedParams
                )
            )
        } catch (e: DataAccessException) {
            if (logger.isDebugEnabled) {
                logger.debug("userId: $userId, coAccountId: $coAccountId, IP Address: $ipAddress")
            }

            logger.error(e.message)
            logger.error(e.stackTrace.toString())
        }
    }

    /**
     * @param joinPoint JoinPoint
     * @return joinPointから抽出したクエリとパラメーター
     */
    @TestOnly
    fun extractQueryParams(joinPoint: JoinPoint): QueryParams {
        val queryParam = joinPoint.args[0]
        return if (queryParam is String) {
            QueryParams(queryParam.toString(), convertSqlParamsToString(joinPoint.args[1]))
        } else {
            QueryParams(extractExecSql(queryParam as PreparedStatementCreator), extractExecParameters(queryParam))
        }
    }

    @TestOnly
    fun extractExecSql(arg: PreparedStatementCreator): String {
        return try {
            if (arg is SqlProvider) return arg.sql ?: ""

            val jdbc = arg.javaClass.getDeclaredField("arg$1").run {
                this.setAccessible(true)
                this[arg]
            }

            return if (jdbc is AbstractJdbcInsert) jdbc.insertString else ""
        } catch (e: Exception) {
            when (e) {
                is IllegalAccessException,
                is IllegalArgumentException,
                is NoSuchFieldException,
                is SecurityException -> {
                    logger.error("クエリーログテーブルへの書き出しに失敗しました。", e)
                    ""
                }

                else -> throw e
            }
        }
    }

    @TestOnly
    fun extractExecParameters(arg: PreparedStatementCreator): String? = try {
        when (arg) {
            is BeanPropertySqlParameterSource -> appendBeanPropertySqlParameter(arg)
            // 1つ目の引数にparametersプロパティがあれば取得
            is SqlProvider -> arg.javaClass.getDeclaredField("parameters").run {
                this.setAccessible(true)
                (this[arg] as List<*>).map {
                    when (it) {
                        is SqlParameterValue -> it.value
                        else -> it
                    }
                }
            }.toString()

            else -> arg.javaClass.getDeclaredField("arg$2").run {
                this.setAccessible(true)
                when (val param = this[arg]) {
                    is List<*> -> getParameterValues(param)
                    else -> param.toString()
                }
            }
        }
    } catch (e: Exception) {
        logger.error("クエリーログテーブルへの書き出しに失敗しました。", e)
        null
    }

    @TestOnly
    @Suppress("UNCHECKED_CAST")
    fun convertSqlParamsToString(params: Any?): String? {
        if (params == null) return null

        try {
            if (params is BatchPreparedStatementSetter) {
                val parametersField = try {
                    params.javaClass.getDeclaredField("val\$batchValues")
                } catch (e: NoSuchFieldException) {
                    params.javaClass.getDeclaredField("val\$batchArgs")
                }

                parametersField.setAccessible(true)

                val parameterFiled = parametersField[params]
                if (parameterFiled is List<*>) {
                    return parameterFiled
                        .map { getParameterValues(it as List<*>) }
                        .toString()
                } else if (parameterFiled is Array<*> && parameterFiled.isArrayOf<SqlParameterSource>()) {
                    return (parameterFiled as Array<SqlParameterSource>).map {
                        when (it) {
                            is BeanPropertySqlParameterSource -> appendBeanPropertySqlParameter(it)
                            is MapSqlParameterSource -> appendMapSqlParameter(it)
                            else -> ""
                        }
                    }.filter { it.isNotEmpty() }.joinToString(",", "[", "]")
                }
                return null
            } else {
                return when {
                    params is BeanPropertySqlParameterSource -> appendBeanPropertySqlParameter(params)
                    params is MapSqlParameterSource -> appendMapSqlParameter(params)
                    params is Array<*> && params.isArrayOf<Any>() -> appendListParameter(params as Array<Any>)
                    params is Map<*, *> -> appendMapParameter(params as Map<String, Any>)
                    else -> ""
                }
            }
        } catch (e: Exception) {
            logger.error("クエリーログテーブルへの書き出しに失敗しました。", e)
            return null
        }
    }

    @TestOnly
    fun getParameterValues(valuesObj: List<*>): String = valuesObj.map {
        when (it) {
            null -> "null"
            is LocalDateTime -> it.format(dateTimeFormatter)
            is Enum<*> -> it.name
            is Byte,
            is Short,
            is Int,
            is BigInteger,
            is Float,
            is Double,
            is BigDecimal,
            is String -> it
            else -> try {
                // SqlParameterValue型を想定
                val valueField = it.javaClass.getDeclaredField("value")
                valueField.setAccessible(true)
                valueField[it]?.toString() ?: "null"
            } catch (e: Exception) {
                logger.error("${it.javaClass.getName()}からのクエリーログパラメータ取得に失敗しました。", e)
                ""
            }
        }
    }.toString()

    @TestOnly
    fun appendBeanPropertySqlParameter(parameterSource: BeanPropertySqlParameterSource): String = parameterSource
        .readablePropertyNames
        .filter { "class" != it }
        .joinToString(",", "[", "]") { "$it:${parameterSource.getValue(it)}" }

    @TestOnly
    fun appendMapSqlParameter(parameterSource: MapSqlParameterSource): String = parameterSource
        .values
        .keys.joinToString(",", "[", "]") { "$it:${parameterSource.getValue(it)}" }

    @TestOnly
    fun appendListParameter(parameterList: Array<Any>) = parameterList
        .map {
            when (it) {
                is SqlParameterValue -> it.value
                else -> it
            }
        }
        .joinToString(",", "[", "]")

    @TestOnly
    fun appendMapParameter(parameterMap: Map<String, Any>) = parameterMap
        .keys.joinToString(",", "[", "]") { "$it:${parameterMap[it]}" }

    @TestOnly
    fun getIpAddress(request: HttpServletRequest): String =
        if ((request.getHeader("X-Forwarded-For") != null)) {
            request.getHeader("X-Forwarded-For")
        } else {
            request.remoteAddr
        }

    @TestOnly
    fun getCoAccountId(request: HttpServletRequest): CoAccountId? =
        ServletRequestUtils
            .getIntParameter(request, "coAccountId")
            ?.let { CoAccountId(it) }

    @TestOnly
    fun getUserId(): UserId? = SecurityContextHolder.getContext().authentication?.principal?.let {
        (it as AccountUserDetails).user.userId
    }

    /**
     * @param ipAddress IPアドレス
     * @return IPアドレスを10進数に変換した値
     */
    @TestOnly
    fun ipAddressToLong(ipAddress: String): Long {
        if (ipAddress.isEmpty()) return 0

        val values = ipAddress
            .split("\\.".toRegex())
            .mapNotNull { value -> value.toLongOrNull()?.takeIf { it in 0..255 } }
            .takeIf { it.size == 4 }
            ?: return 0

        return ((values[0] shl 24) + (values[1] shl 16) + (values[2] shl 8) + values[3])
    }

    data class QueryParams(val originalQuery: String, val originalParams: String?) {
        val originalParamsSize = StringUtils.getBytesUtf8(originalParams)?.size ?: 0
        val truncatedQuery: String = if (originalQuery.length > MAX_QUERY_SIZE) {
            originalQuery.substring(0, MAX_QUERY_SIZE)
        } else {
            originalQuery
        }
        val truncatedParams: String? = if (originalParamsSize > MAX_PARAMS_SIZE) {
            originalParams?.let { String(it.toByteArray(), 0, MAX_PARAMS_SIZE) }
        } else {
            originalParams
        }

        fun isChange(): Boolean = originalQuery != truncatedQuery || originalParams != truncatedParams

        companion object {
            private const val MAX_QUERY_SIZE = 1024
            private const val MAX_PARAMS_SIZE = 65535
        }
    }

    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}
