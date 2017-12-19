package com.dgkrajnik.kotlinREST

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

// Note that because this configuration is still within the same app context as the REST server, it will
// apply to that server. The practical upshot of this is that the REST server will have the same UserDetailsManager.
// This *can* theoretically be decoupled by providing localised authenticationManagers to each endpoint, so...
// TODO: Decouple authenticationmanagerbeans for auth and resource server.
@Configuration
@Order(101)
@EnableWebSecurity
class AuthorizationServerSecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Bean(name=["authAuthenticationManager"])
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Resource(name="authDataSource")
    private lateinit var dataSource: DataSource

    override fun configure(auth: AuthenticationManagerBuilder) {
        var passwordEncoder = BCryptPasswordEncoder()
        auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder)
                .withUser("larry").password(passwordEncoder.encode("adminpass")).roles("ADMIN")
                .and()
                .withUser("neev").password(passwordEncoder.encode("otheruserpass")).roles("USER")
    }

    @Bean
    @Inject
    fun userApprovalHandler(tokenStore: TokenStore, clientDetailsService: ClientDetailsService): TokenStoreUserApprovalHandler {
        val handler = TokenStoreUserApprovalHandler()
        handler.setTokenStore(tokenStore)
        handler.setRequestFactory(DefaultOAuth2RequestFactory(clientDetailsService))
        handler.setClientDetailsService(clientDetailsService)
        return handler
    }

    @Bean
    @Inject
    fun approvalStore(tokenStore: TokenStore): ApprovalStore {
        val store = TokenApprovalStore()
        store.setTokenStore(tokenStore)
        return store
    }
}
