package com.dgkrajnik.kotlinREST

import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.approval.ApprovalStore
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory
import org.springframework.security.oauth2.provider.token.TokenStore
import javax.annotation.Resource
import javax.inject.Inject
import javax.sql.DataSource

@Configuration
@Order(101)
@EnableWebSecurity
class AuthorizationServerSecurityConfiguration : WebSecurityConfigurerAdapter() {
    /**
     * 'Exports' this AuthenticationManager so that we can select it specifically elsewhere.
     */
    @Bean(name=["authAuthenticationManager"])
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Resource(name="authDataSource")
    private lateinit var dataSource: DataSource

    /**
     *  Builds the AuthenticationManager specifically for the auth server, backed by its datasource.
     */
    override fun configure(auth: AuthenticationManagerBuilder) {
        var passwordEncoder = BCryptPasswordEncoder()
        auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder)
                .withUser("larry").password(passwordEncoder.encode("adminpass")).roles("ADMIN")
                .and()
                .withUser("neev").password(passwordEncoder.encode("otheruserpass")).roles("USER")
    }

    /**
     * Configure the oauth endpoints to return 401 on authentication failure.
     */
    override fun configure(http: HttpSecurity) {
        http.antMatcher("/oauth/**")
            .exceptionHandling().authenticationEntryPoint(Http401AuthenticationEntryPoint("Bearer realm=\"webrealm\""))
    }

    /**
     * Set up tokenstore-based user approval for the auth server.
     */
    @Bean
    @Inject
    fun userApprovalHandler(tokenStore: TokenStore, clientDetailsService: ClientDetailsService): TokenStoreUserApprovalHandler {
        val handler = TokenStoreUserApprovalHandler()
        handler.setTokenStore(tokenStore)
        handler.setRequestFactory(DefaultOAuth2RequestFactory(clientDetailsService))
        handler.setClientDetailsService(clientDetailsService)
        return handler
    }
}
