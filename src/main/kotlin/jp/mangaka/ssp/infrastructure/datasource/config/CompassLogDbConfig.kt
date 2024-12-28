package jp.mangaka.ssp.infrastructure.datasource.config

import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class CompassLogDbConfig {
    @Bean("CompassLogDSP")
    @ConfigurationProperties("spring.datasource.compass-log-db")
    fun dataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean("CompassLogDS")
    fun dataSource(): DataSource = dataSourceProperties().initializeDataSourceBuilder().build()

    @Bean("CompassLogJdbc")
    fun jdbcWrapper(): JdbcWrapper = JdbcWrapper(NamedParameterJdbcTemplate(dataSource()))

    @Bean("CompassLogTX")
    fun transactionManager(): PlatformTransactionManager = DataSourceTransactionManager(dataSource())
}
