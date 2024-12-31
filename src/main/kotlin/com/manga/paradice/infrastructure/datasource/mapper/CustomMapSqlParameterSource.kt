package com.manga.paradice.infrastructure.datasource.mapper

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource

class CustomMapSqlParameterSource : MapSqlParameterSource {

    constructor() : super()

    constructor(paramName: String, value: Any?) : super(paramName, value)

    constructor(values: Map<String?, *>?) : super(values)

    override fun getValue(paramName: String): Any? = MapperUtils.mapToValue(super.getValue(paramName))
}
