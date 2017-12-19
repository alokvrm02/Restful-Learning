package com.dgkrajnik.kotlinREST

import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.authentication.AuthenticationManager
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.annotation.Resource
import javax.inject.Inject
import javax.inject.Named
import javax.sql.DataSource

@Configuration
@EnableResourceServer
class WebSecurityConfig : ResourceServerConfigurerAdapter() {
    @Primary
    @Bean
    fun tokenService(): RemoteTokenServices {
        val tokenService = RemoteTokenServices()
        // Note that, because the server port is set *after*
        // the beans get constructed, you can't just use local.server.port.
        tokenService.setCheckTokenEndpointUrl(
                "http://localhost:8080/oauth/check_token")
        tokenService.setClientId("normalClient")
        tokenService.setClientSecret("spookysecret")
        return tokenService
    }

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

@Configuration
class ResourceServerSecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Resource(name="resourceDataSource")
    private lateinit var dataSource: DataSource

    override fun configure(http: HttpSecurity) {
        //@formatter:off
        http.regexMatcher("/hello/(string|service|secureData|bad.+|throwAnError)")
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(Http401AuthenticationEntryPoint("Bearer realm=\"webrealm\""))
            .and()
                .authorizeRequests()
                    .antMatchers("/hello/secureData").authenticated()
                    .antMatchers("/hello/string").permitAll()
                    .antMatchers("/hello/service").permitAll()
                    .antMatchers("/hello/badPost").permitAll()
                    .antMatchers("/hello/badRequest").permitAll()
                    .antMatchers("/hello/throwAnError").permitAll()
            .and()
                .httpBasic()
        //@formatter:on
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        var passwordEncoder = BCryptPasswordEncoder()
        auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder)
                .withUser("steve").password(passwordEncoder.encode("userpass")).roles("USER")
    }
}

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
class MethodSecurityConfig : GlobalMethodSecurityConfiguration() {
    //@Inject
    lateinit private var securityConfig: AuthorizationServerSecurityConfiguration

    override fun createExpressionHandler(): MethodSecurityExpressionHandler {
        return OAuth2MethodSecurityExpressionHandler()
    }
}
