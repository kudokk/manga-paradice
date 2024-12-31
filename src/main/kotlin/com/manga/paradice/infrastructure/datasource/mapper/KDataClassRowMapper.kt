package com.manga.paradice.infrastructure.datasource.mapper

import com.manga.paradice.application.valueobject.IdValueObject
import org.springframework.beans.BeanUtils
import org.springframework.beans.BeanWrapperImpl
import org.springframework.beans.TypeConverter
import org.springframework.core.MethodParameter
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.JdbcUtils
import java.lang.reflect.Constructor
import java.sql.ResultSet
import java.sql.SQLException
import java.util.Locale
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaConstructor

/**
 * SpringのDataClassRowMapperはKotlinのデータクラス以外も想定した実装になっていて
 * パフォーマンスに問題があったため独自のRowMapperを定義.
 */
class KDataClassRowMapper<T : Any>(mappedClass: KClass<T>) : RowMapper<T> {
    // プライマリコンストラクタがある前提なので強制キャスト
    private val mappedConstructor: Constructor<T> = mappedClass.primaryConstructor!!.javaConstructor!!
    private val mappedParameters: List<MappedParameter> = MappedParameter.newInstances(mappedConstructor)
    private val defaultConversionService = DefaultConversionService.getSharedInstance()
    private val indexMap: MutableMap<Int, Int> = mutableMapOf()

    override fun mapRow(rs: ResultSet, rowNum: Int): T {
        val typeConverter = typeConverter()

        val args = mappedParameters.mapIndexed { constructorIndex, parameter ->
            // DataClassRowMapperでは毎行インデックスの導出を行っているが、非効率なのでキャッシュする形にしている.
            val rowIndex = indexMap.computeIfAbsent(constructorIndex) {
                try {
                    rs.findColumn(parameter.lowerName)
                } catch (ex: SQLException) {
                    rs.findColumn(parameter.snakeName)
                }
            }

            val value = getColumnValue(rs, rowIndex, parameter.typeDescriptor.type)
            typeConverter.convertIfNecessary(value, parameter.typeDescriptor.type, parameter.typeDescriptor)
        }.toTypedArray()

        return mappedConstructor.newInstance(*args)
    }

    private fun typeConverter(): TypeConverter = BeanWrapperImpl().apply {
        this.conversionService = defaultConversionService
    }

    // else句の関数は、標準的でない型の場合はSQLExceptionをスロー＆キャッチして if句の関数を呼び出す実装になっている.
    // 件数が多いとパフォーマンスに影響があるため、事前に独自型をチェックしている.
    private fun getColumnValue(rs: ResultSet, index: Int, paramType: Class<*>): Any? =
        if (superTypes.any { it.isAssignableFrom(paramType) }) {
            JdbcUtils.getResultSetValue(rs, index)
        } else {
            JdbcUtils.getResultSetValue(rs, index, paramType)
        }

    private data class MappedParameter(
        val lowerName: String,
        val snakeName: String,
        val typeDescriptor: TypeDescriptor
    ) {
        companion object {
            fun newInstances(constructor: Constructor<*>): List<MappedParameter> = BeanUtils
                .getParameterNames(constructor)
                .mapIndexed { index, name ->
                    MappedParameter(
                        name.lowercase(Locale.US),
                        JdbcUtils.convertPropertyNameToUnderscoreName(name),
                        TypeDescriptor(MethodParameter(constructor, index))
                    )
                }
        }
    }

    companion object {
        private val superTypes = setOf(
            IdValueObject::class.java
        )

        @JvmStatic
        fun <T : Any> newInstance(mappedClass: KClass<T>): KDataClassRowMapper<T> {
            return KDataClassRowMapper(mappedClass)
        }
    }
}
