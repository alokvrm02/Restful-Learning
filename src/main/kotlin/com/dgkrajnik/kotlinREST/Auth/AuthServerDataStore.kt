package com.dgkrajnik.kotlinREST.Auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
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
 * Generic data store for the auth server.
 *
 * Used for token storage and client information storage.
 */
@Configuration
class AuthServerDataStore {
    @Value("classpath:auth-schema.sql")
    private lateinit var schemaScript: Resource

    @Value("\${restful.datasource.auth.url}")
    private lateinit var authURL: String

    /**
     * Generic populator that refreshes the database using a schema script.
     */
    private fun databasePopulator(): DatabasePopulator {
        val populator = ResourceDatabasePopulator()
        populator.addScript(schemaScript)
        return populator
    }

    /**
     * Bean for the actual auth DataSource. Automatically populates the DB, with username SA and blank password.
     */
    @Bean
    @Primary // Spring thinks it's clever, and needs a primary DataSource for some internal stuff.
    fun authDataSource(): DataSource {
        val dataSource = DriverManagerDataSource(authURL, "SA", "")
        databasePopulator().populate(dataSource.connection)
        return dataSource
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JdbcTokenStore(authDataSource())
    }
}
