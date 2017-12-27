package bcs.testing.test.REST.Configs

import org.springframework.context.annotation.Configuration
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler

// This configuration enables method security, which allows for
// Aspect Oriented Programming security to be applied to methods.
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
class MethodSecurityConfig : GlobalMethodSecurityConfiguration() {
    override fun createExpressionHandler(): MethodSecurityExpressionHandler {
        return OAuth2MethodSecurityExpressionHandler()
    }
}
