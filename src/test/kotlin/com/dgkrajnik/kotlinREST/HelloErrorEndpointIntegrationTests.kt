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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * Tests to check that error handling is working as expected.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration
@WebAppConfiguration
class HelloErrorEndpointIntegrationTests {
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
    fun testErrorHandling() {
        mvc.perform(get("$BASE_PATH/throwAnError"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Malformed JSON Request"))
                .andExpect(jsonPath("$.debug_message").value("Wink wonk"))
    }

    @Test
    fun testCustomErrorHandling() {
        mvc.perform(get("$BASE_PATH/badRequest"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Entity Not Found"))
                .andExpect(jsonPath("$.debug_message").value("Entity null != 22 not found."))
    }

    @Test
    fun testCustomVerificationErrorHandling() {
        mvc.perform(post("$BASE_PATH/badPost").param("reqparam", "24"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Error in Request Data"))
                .andExpect(jsonPath("$.sub_errors[0].rejected_value").value(24))
    }
}
