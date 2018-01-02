package com.dgkrajnik.kotlinREST.REST.Security

import com.dgkrajnik.kotlinREST.Auth.AuthorizationServerSecurityConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler
import org.springframework.security.oauth2.provider.token.RemoteTokenServices
import javax.annotation.Resource
import javax.sql.DataSource

/**
 * Configuration for the resource server, and oauth-guarded endpoints.
 */
@Configuration
@EnableResourceServer
class WebSecurityConfig : ResourceServerConfigurerAdapter() {
    @Value("\${restful.resource.check_token_url}")
    private lateinit var tokenURL: String

    /**
     * Configure the resource server to poll the auth server to check tokens.
     *
     * Note that the auth server url is hardcoded, *including the port*.
     */
    @Bean
    @Primary
    fun tokenService(): RemoteTokenServices {
        val tokenService = RemoteTokenServices()
        // Note that, because the server port is set *after*
        // the beans get constructed, you can't just use local.server.port.
        tokenService.setCheckTokenEndpointUrl(tokenURL)
        tokenService.setClientId("normalClient")
        tokenService.setClientSecret("spookysecret")
        return tokenService
    }

    /**
     * Configure the endpoints that need to be secured by oauth.
     */
    override fun configure(http: HttpSecurity) {
        //@formatter:off
        http.antMatcher("/hello/secureOAuthData")
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(Http401AuthenticationEntryPoint("Bearer realm=\"webrealm\""))
            .and()
                .authorizeRequests()
                    .anyRequest().access("#oauth2.hasScope('read')")
        //@formatter:on
    }
}

/**
 * Configuration for HTTP Basic user detail storage, and for HTTP Basic-secured and unsecured endpoints.
 */
@Configuration
class ResourceServerSecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Resource(name="resourceDataSource")
    private lateinit var dataSource: DataSource

    /**
     * Configure the endpoints that are secured by HTTP Basic Auth or which are unsecured.
     */
    override fun configure(http: HttpSecurity) {
        //@formatter:off
        http.requestMatchers()
                .antMatchers("/hello/string")
                .antMatchers("/hello/service")
                .antMatchers("/hello/secureData")
                .antMatchers("/hello/bad*")
                .antMatchers("/hello/throwAnError")
                .antMatchers("/hello/loggedEndpoint")
                .antMatchers("/hello/auditedEndpoint")
                .antMatchers("/hello/erroringAuditedEndpoint)")
            .and()
                .csrf().disable()
                // Set up exception handling to automatically return HTTP 401 on failure rather than a redirect.
                .exceptionHandling().authenticationEntryPoint(Http401AuthenticationEntryPoint("Bearer realm=\"webrealm\""))
            .and()
                .authorizeRequests()
                    .antMatchers("/hello/secureData").authenticated()
                    .antMatchers("/hello/string").permitAll()
                    .antMatchers("/hello/service").permitAll()
                    .antMatchers("/hello/badPost").permitAll()
                    .antMatchers("/hello/badRequest").permitAll()
                    .antMatchers("/hello/throwAnError").permitAll()
                    .antMatchers("/hello/loggedEndpoint").permitAll()
                    .antMatchers("/hello/auditedEndpoint").permitAll()
                    .antMatchers("/hello/erroringAuditedEndpoint").permitAll()
            .and()
                .httpBasic()
        //@formatter:on
    }

    /**
     * Configure HTTP Basic users.
     */
    override fun configure(auth: AuthenticationManagerBuilder) {
        var passwordEncoder = BCryptPasswordEncoder()
        auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder)
                .withUser("steve").password(passwordEncoder.encode("userpass")).roles("USER")
    }
}

/**
 * Configuration to enable annotation-based method security.
 * Currently disabled.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
class MethodSecurityConfig : GlobalMethodSecurityConfiguration() {
    //@Inject
    lateinit private var securityConfig: AuthorizationServerSecurityConfiguration

    override fun createExpressionHandler(): MethodSecurityExpressionHandler {
        return OAuth2MethodSecurityExpressionHandler()
    }
}
