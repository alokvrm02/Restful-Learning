package com.dgkrajnik.kotlinREST.REST.Swagger

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger.web.ApiKeyVehicle
import springfox.documentation.swagger.web.SecurityConfiguration

/**
 * Holder for Springfox's Swagger2 configuration.
 */
@Configuration
class SwaggerConfig {
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/hello.*")) // Only build for our actual endpoints at /hello/**
                .build()
                .securitySchemes(listOf(securitySchema()))
                .securityContexts(listOf(securityContext()))
    }

    /**
     * Configure Springfox's option to automatically place an oauth login button on HTML documentation.
     */
    private fun securitySchema(): OAuth {
        val loginEndpoint: LoginEndpoint = LoginEndpoint("/oauth/authorize")
        val grantType: GrantType = ImplicitGrant(loginEndpoint, "swaggerAuth")
        return OAuth("oauth2", listOf(AuthorizationScope("read", "Deafault read-only scope.")), listOf(grantType))
    }

    private fun securityContext(): SecurityContext {
        return SecurityContext.builder().securityReferences(defaultAuth())
                .forPaths(PathSelectors.ant("/hello/**")).build()
    }
    private fun defaultAuth(): List<SecurityReference> {
        val authorizationScope: AuthorizationScope = AuthorizationScope("global", "accessEverything")
        return listOf(SecurityReference("oauth2", arrayOf(authorizationScope)))
    }

    /**
     * Configure Springfox's auth details.
     */
    @Bean
    fun securityInfo(): SecurityConfiguration {
        return SecurityConfiguration("normalClient", "spookysecret", "realm", "spring-hello", "", ApiKeyVehicle.HEADER, "api_key",",");
    }
}

@Configuration
class swaggerStaticEndpoints : WebMvcConfigurerAdapter() {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        //tyvm harrison
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }
}
