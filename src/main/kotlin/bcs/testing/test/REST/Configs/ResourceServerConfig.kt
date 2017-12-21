package bcs.testing.test.REST.Configs

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler

@Configuration
@EnableResourceServer
class Oauth2ResourceServerConfig : ResourceServerConfigurerAdapter() {
    val RESOURCE_ID = "SPRING_REST_API"

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.resourceId(RESOURCE_ID).stateless(false)
    }

    override fun configure(http: HttpSecurity) {
        http
                .anonymous().disable()
                .requestMatchers().antMatchers("/hello/string", "/hello/service",
                                                    "/hello/data")
                .and()

                .authorizeRequests()
                .antMatchers("/hello/string")
                .access("hasRole('ADMIN')")
                .and()

                .authorizeRequests()
                .antMatchers("/hello/service")
                .access("hasRole('USER')")
                .and()

                .exceptionHandling().accessDeniedHandler(OAuth2AccessDeniedHandler())
    }
}