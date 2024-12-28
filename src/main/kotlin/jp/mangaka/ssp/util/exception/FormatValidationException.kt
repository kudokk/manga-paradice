package jp.mangaka.ssp.util.exception

import jakarta.validation.ConstraintViolation
import org.springframework.validation.BindingResult

/**
 * フォーマットバリデーションの例外クラス
 * フォーマットバリデーションで不正が検知された場合、この例外クラスがスローされる
 */
class FormatValidationException(val errorFields: Map<String, String>) : RuntimeException(
    errorFields.entries.joinToString(
        prefix = "Invalid Input. Field errors: [",
        separator = " / ",
        postfix = "]"
    ) { "${it.key}: ${it.value}" }
) {
    init {
        stackTrace = filterStackTrace(stackTrace)
    }

    companion object {
        /**
         * バリデーションエラーがある場合は例外を投げる
         */
        fun checkErrorResult(errors: BindingResult) {
            checkErrorResult(errors.fieldErrors.associate { it.field to (it.defaultMessage ?: "") })
        }

        /**
         * バリデーションエラーがある場合は例外を投げる
         */
        fun <T> checkErrorResult(errors: Set<ConstraintViolation<T>>) {
            // フィールド重複時は先に入ってた方を優先する
            checkErrorResult(errors.associate { it.propertyPath.toString() to it.message })
        }

        /**
         * バリデーションエラーがある場合は例外を投げる
         */
        fun checkErrorResult(errorFields: Map<String, String>) {
            if (errorFields.isEmpty()) return

            throwFormatValidationException(errorFields)
        }

        /**
         * バリデーションエラーがある場合は例外を投げる
         */
        fun throwFormatValidationException(errorFields: Map<String, String>) {
            throw FormatValidationException(errorFields)
        }
    }
}
