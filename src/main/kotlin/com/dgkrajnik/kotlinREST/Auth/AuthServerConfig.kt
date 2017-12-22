package com.dgkrajnik.kotlinREST.Auth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import javax.annotation.Resource
import javax.inject.Inject
import javax.inject.Named
import javax.sql.DataSource

@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfiguration : AuthorizationServerConfigurerAdapter() {
    @Inject
    @Named("authDataSource")
    lateinit var dataSource: DataSource

    @Inject
    lateinit var tokenStore: TokenStore

    @Resource(name="authAuthenticationManager")
    lateinit var authenticationManager: AuthenticationManager

    @Inject
    lateinit var userApprovalHandler: TokenStoreUserApprovalHandler

    /**
     * Simple JDBC-backed storage for the auth server, accepting password and implicit auth types only, with no refresh tokens.
     */
    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.jdbc(dataSource)
                .withClient("normalClient")
                .secret("spookysecret")
                .authorizedGrantTypes("password", "implicit")
                .scopes("read")
    }

    /**
     * Authorise with the auth server auth manager, approve with the auth server token-based user approval store, and
     * store tokens in the auth server token store.
     */
    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
                .authenticationManager(authenticationManager)
    }

    /**
     * Configures the auth server so that anybody can get the server's token pubkey (tokenKey), and only authenticated clients can check token validity.
     *
     */
    override fun configure(oAuthServer: AuthorizationServerSecurityConfigurer) {
        /* Strictly speaking, token key access is only necessary for JWT-based token auth, but the tokenKey is a pubkey anyway.
         * check_token, used for non-JWT flows, could also be totally open with minor security implications at worst, but the
         * server has to actually perform work to check a token, so it's probably a good idea to only let real clients
         * do the check procedure.
         */
        oAuthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
    }

    /**
     * Simple JDBC-based token store.
     */
    @Bean
    fun tokenStore(): TokenStore {
        return JdbcTokenStore(dataSource)
    }
}
