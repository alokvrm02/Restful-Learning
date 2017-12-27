package bcs.testing.test.REST.Configs

import com.google.common.base.Predicate
import com.google.common.base.Predicates.or
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.PathSelectors.regex
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger.web.ApiKeyVehicle
import springfox.documentation.swagger.web.SecurityConfiguration

// This file holds Springfox's Swagger2 configuration.
@Configuration
class SwaggerConfig {
    // Docket = A summary or other brief statement of the contents of a document; an abstract.
    @Bean
    fun swaggerSpringMvcPlugin(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(paths())
                .build()
                .securitySchemes(listOf(securitySchema()))
                .securityContexts(listOf(securityContext()))
    }

    // Only document these paths
    private fun paths(): Predicate<String> {
        return or(
                regex("/hello.*")
        )
    }

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

    @Bean
    fun securityInfo(): SecurityConfiguration {
        return SecurityConfiguration("trusted-client", "secret", "realm", "spring-hello",
                "", ApiKeyVehicle.HEADER, "api_key",",")
    }
}
