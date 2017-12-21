package bcs.testing.test.REST.Configs

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler
import org.springframework.security.oauth2.provider.token.TokenStore
import javax.inject.Inject

@Configuration
@EnableAuthorizationServer
class AuthServerOauth2Config : AuthorizationServerConfigurerAdapter() {

    val REALM = "EXAMPLE_REALM"

    @Inject
    lateinit var tokenStore : TokenStore

    @Inject
    lateinit var handler : UserApprovalHandler

    @Inject
    @Qualifier("authenticationManagerBean")
    lateinit var authManager : AuthenticationManager

    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        security
                .realm(REALM+"/client")
    }

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.inMemory()
                .withClient("trusted-client")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                .scopes("read", "write", "trust")
                .secret("secret")
                .accessTokenValiditySeconds(300)//invalid after 5 minutes.
                .refreshTokenValiditySeconds(600)//refresh after 10 minutes.
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.tokenStore(tokenStore).userApprovalHandler(handler)
                .authenticationManager(authManager)
    }

}