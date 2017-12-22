package com.dgkrajnik.kotlinREST

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import javax.inject.Inject
import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.Before
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * Simple tests on basic endpoints to check that the system is at a basic level of configuration.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration
@WebAppConfiguration
class HelloBasicEndpointIntegrationTests {
    @Inject
    private lateinit var context: WebApplicationContext

    private lateinit var mvc: MockMvc

    @Before
    fun setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply<DefaultMockMvcBuilder>(springSecurity())
                .build()
    }

    val BASE_PATH = "/hello"

	@Test
    fun testHelloController() {
        mvc.perform(get("$BASE_PATH/string"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Hello, Spring!"))
    }

    @Test
    fun testHelloService() {
        mvc.perform(get("$BASE_PATH/service"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Hello, Service!"))
    }

    @Test
    fun testHelloDTO() {
        mvc.perform(get("$BASE_PATH/secureData").with(httpBasic("steve", "userpass")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message").value("Hello, Data!"))
    }

    @Test
    fun testHelloDTOFailure() {
        mvc.perform(get("$BASE_PATH/secureData").with(httpBasic("neev", "otheruserpass")))
                .andExpect(status().isUnauthorized())
    }
}
