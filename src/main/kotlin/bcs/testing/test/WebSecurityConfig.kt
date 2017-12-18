package bcs.testing.test

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import javax.inject.Inject

@Configuration //Make this as a configuration class
@EnableWebSecurity //Turn on Web Security
class SecurityWebInitializer : WebSecurityConfigurerAdapter(){
    // AUTHORISATION
    // aka determining WHO you are.
    override fun configure(http: HttpSecurity) {
        //This tells Spring Security to authorize all requests
        //We use formLogin and httpBasic
        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()

                .formLogin()
                .and()

                .httpBasic()
    }

    @Inject
    // AUTHENTICATION
    // aka what YOU are allowed to do
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        // These are example users in memory used to check logging in works
        auth
                .inMemoryAuthentication()

                .withUser("bob")
                .password("belcher")
                .roles("USER")
                .and()

                .withUser("admin")
                .password("admin")
                .roles("USER", "ADMIN")
    }
}