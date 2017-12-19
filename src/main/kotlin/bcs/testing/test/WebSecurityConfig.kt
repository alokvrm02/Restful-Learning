package bcs.testing.test

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.approval.ApprovalStore
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore
import javax.inject.Inject

/**
 * This file configures the web security
 */

@Order(101)
@Configuration
@EnableWebSecurity
class SecurityWebInitializer : WebSecurityConfigurerAdapter() {

    @Inject
    lateinit var clientService : ClientDetailsService

    // AUTHORISATION
    // aka determining WHO you are.
    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable()
                .anonymous().disable()

                .authorizeRequests()
                .antMatchers("/oauth/token")
                    .permitAll()
    }

    @Inject
    // AUTHENTICATION
    // aka what YOU are allowed to do
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        // These are example users in memory used to check logging in works
        auth
                .inMemoryAuthentication()
                .withUser("bob").password("belcher").roles("USER")
                .and()
                .withUser("admin").password("admin").roles("USER", "ADMIN")
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun tokenStore(): TokenStore {
        return InMemoryTokenStore()
    }

    @Bean
    @Inject
    fun userApprovalHander(tokenStore: TokenStore): TokenStoreUserApprovalHandler {
        var handler: TokenStoreUserApprovalHandler = TokenStoreUserApprovalHandler()

        handler.setTokenStore(tokenStore)
        handler.setRequestFactory(DefaultOAuth2RequestFactory(clientService))
        handler.setClientDetailsService(clientService)

        return handler
    }

    @Bean
    @Inject
    fun approvalStore(tokenStore: TokenStore): ApprovalStore {
        var store: TokenApprovalStore = TokenApprovalStore()

        store.setTokenStore(tokenStore)

        return store
    }
}