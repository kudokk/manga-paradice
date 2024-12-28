package jp.mangaka.ssp.application.valueobject

import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory
import java.io.Serializable
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaConstructor
import kotlin.reflect.jvm.javaType

abstract class IdValueObject<T : Comparable<T>> : Serializable, Comparable<IdValueObject<T>> {
    abstract val value: T

    // 異なるクラスを引数に取れてしまうので、その場合は弾く
    override fun compareTo(other: IdValueObject<T>): Int =
        if (this::class == other::class) {
            value.compareTo(other.value)
        } else {
            throw IllegalArgumentException("異なるクラス同士の比較が行われました。")
        }

    final override fun toString(): String = value.toString()

    companion object {
        fun assertNonNegative(value: Int): Unit = assertNonNegative(value.toLong())

        fun assertNonNegative(value: Long) {
            if (value < 0) throw IllegalArgumentException("不正なID値を用いた値オブジェクトの生成処理が行われました。")
        }
    }
}

class IdValueObjectConverterFactory : ConverterFactory<Any, IdValueObject<*>> {
    override fun <T : IdValueObject<*>> getConverter(targetType: Class<T>): Converter<Any, T> {
        return IdValueObjectConverter(targetType)
    }

    class IdValueObjectConverter<T : IdValueObject<*>>(targetType: Class<T>) : Converter<Any, T> {
        private val constructor = targetType.kotlin.primaryConstructor!!
        private val valueType = constructor.parameters
            .takeIf { it.size == 1 }
            ?.let { it[0].type.javaType }
            ?: throw IllegalArgumentException("IDの値オブジェクトに複数のプロパティが存在します")

        override fun convert(source: Any): T {
            val value = when (valueType) {
                Int::class.java -> toInt(source)
                Long::class.java -> toLong(source)
                else -> throw IllegalArgumentException("対応していない型 $valueType への変換です")
            }

            return constructor.javaConstructor!!.newInstance(value)
        }

        private fun toInt(value: Any): Int = when (value) {
            is String -> value.toInt()
            is Long -> value.toInt()
            is Int -> value
            else -> throw IllegalArgumentException("対応していない型 ${value::class} からの変換です")
        }

        private fun toLong(value: Any): Long = when (value) {
            is String -> value.toLong()
            is Int -> value.toLong()
            is Long -> value
            else -> throw IllegalArgumentException("対応していない型 ${value::class} からの変換です")
        }
    }
}
