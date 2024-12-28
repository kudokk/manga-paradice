package jp.mangaka.ssp.infrastructure.datasource.config

import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
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
class CompassMasterDbConfig {
    @Bean("CompassMasterDSP")
    @ConfigurationProperties("spring.datasource.compass-master-db")
    fun dataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean("CompassMasterDS")
    fun dataSource(): DataSource = dataSourceProperties().initializeDataSourceBuilder().build()

    // user_operation_logのAOPを有効にするためBeanとして定義する必要がある
    @Bean("CompassMasterJdbcTemplate")
    fun jdbcTemplate(): JdbcTemplate = JdbcTemplate(dataSource())

    @Bean("CompassMasterJdbc")
    fun jdbcWrapper(): JdbcWrapper = JdbcWrapper(NamedParameterJdbcTemplate(jdbcTemplate()))

    @Bean("CompassMasterTX")
    fun transactionManager(): PlatformTransactionManager = DataSourceTransactionManager(dataSource())
}
