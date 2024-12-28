package jp.mangaka.ssp.infrastructure.datasource.mapper

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource

class CustomBeanPropertySqlParameterSource(obj: Any) : BeanPropertySqlParameterSource(obj) {
    override fun getValue(paramName: String): Any? = MapperUtils.mapToValue(super.getValue(paramName))
}
