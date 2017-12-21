package com.dgkrajnik.kotlinREST

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.jdbc.datasource.init.DataSourceInitializer
import org.springframework.jdbc.datasource.init.DatabasePopulator
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import javax.inject.Inject
import javax.sql.DataSource

/**
 * Generic data store for the resource server.
 *
 * Mostly used for user details, for authorization after authentication.
 * Also refer to AuthServerDataStore; this class is very similar.
 *
 * @See AuthServerDataStore
 */
@Configuration
class ResourceServerDataStore {
    @Value("classpath:resource-schema.sql")
    lateinit var schemaScript: Resource

    @Value("\${restful.datasource.resource.url}")
    private lateinit var resourceURL: String

    private fun resourceDatabasePopulator(): DatabasePopulator {
        val populator = ResourceDatabasePopulator()
        populator.addScript(schemaScript)
        return populator
    }

    @Bean
    fun resourceDataSource(): DataSource {
        val dataSource = DriverManagerDataSource(resourceURL, "SA", "")
        resourceDatabasePopulator().populate(dataSource.connection)
        return dataSource
    }
}
