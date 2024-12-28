package jp.mangaka.ssp.infrastructure.datasource.mapper

import jp.mangaka.ssp.application.valueobject.IdValueObject

object MapperUtils {

    /**
     * @param value オブジェクト
     * @return IDの値オブジェクトやEnum値などをSpringが扱える型に変換した値
     */
    fun mapToValue(value: Any?): Any? = when (value) {
        null -> null
        is Collection<*> -> value.map { mapToValue(it) }
        is Enum<*> -> value.toString()
        is IdValueObject<*> -> value.value
        else -> value
    }
}
