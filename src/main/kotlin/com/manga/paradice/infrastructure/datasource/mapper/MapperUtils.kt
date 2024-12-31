package com.manga.paradice.infrastructure.datasource.mapper

import com.manga.paradice.application.valueobject.IdValueObject

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
