package com.manga.paradice.infrastructure.datasource.config

import com.manga.paradice.infrastructure.datasource.JdbcWrapper
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class CoreMasterDbConfig {
    @Bean("CoreMasterDSP")
    @ConfigurationProperties("spring.datasource.core-master-db")
    fun dataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean("CoreMasterDS")
    fun dataSource(): DataSource = dataSourceProperties().initializeDataSourceBuilder().build()

    // user_operation_logのAOPを有効にするためBeanとして定義する必要がある
    @Bean("CoreMasterJdbcTemplate")
    fun jdbcTemplate(): JdbcTemplate = JdbcTemplate(dataSource())

    @Bean("CoreMasterJdbc")
    fun jdbcWrapper(): JdbcWrapper = JdbcWrapper(NamedParameterJdbcTemplate(jdbcTemplate()))

    @Bean("CoreMasterTX")
    fun transactionManager(): PlatformTransactionManager = DataSourceTransactionManager(dataSource())
}
