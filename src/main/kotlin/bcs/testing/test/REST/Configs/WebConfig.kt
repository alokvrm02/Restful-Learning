package bcs.testing.test.REST.Configs

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

// This file is necessary to have /swagger-ui.html work with exception handling of 404 errors.
@Configuration
class WebConfig: WebMvcConfigurerAdapter() {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/")

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")

    }
}
