package com.dgkrajnik.kotlinREST

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
import org.junit.Assert.*
import org.springframework.http.converter.HttpMessageNotReadableException

/**
 * Tests to verify that the oauth-secured endpoints work.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class HelloOAuthEndpointIntegrationTests {
    /*
     * These tests *could* be done in mockmvc too, but getting them to work is somewhat finicky. One of the biggest issues
     * is that the resource server makes http requests to a static URL to do the token checking, and this obviously won't
     * work in a mock container. While this could be fixed, it's ultimately more work than just puppeteering a server.
     */
    val BASE_PATH = "/hello"

	@Inject
	lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun testOAuth() {
        val loginResponse = oAuthLogin("neev", "otheruserpass")
        assertNotNull(loginResponse)
        assertEquals(HttpStatus.OK, loginResponse.statusCode)
    }

    @Test
    fun testOAuthFailure() {
        val result = testRestTemplate.getForEntity("/oauth/token", String::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
    }

    @Test
    fun testHelloFailureOAuth() {
        val result = testRestTemplate.getForEntity("$BASE_PATH/secureOAuthData", HelloData::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
    }

    @Test
    fun testOAuthBadCreds() {
        try {
            val loginResponse = oAuthLogin("steve", "userpass")
        } catch (e: HttpMessageNotReadableException) {
            return
        }
        fail()
    }

    @Test
    fun testHelloOAuth() {
        val loginResponse = oAuthLogin("neev", "otheruserpass")
        assertNotNull(loginResponse)
        assertEquals(HttpStatus.OK, loginResponse.statusCode)

        val token = loginResponse.body.accessToken
        val tokenHeaders = HttpHeaders()
        tokenHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        tokenHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer $token")
        val tokenRequest = HttpEntity(null, tokenHeaders)
        val result = testRestTemplate.exchange(
                "$BASE_PATH/secureOAuthData",
                HttpMethod.GET,
                tokenRequest,
                HelloData::class.java
        )
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(HelloData("Hello, OAuth!"), result.body)
    }

    @Test
    fun testOAuthImplicit() {
        val loginResponse = oAuthImplicitLogin("neev", "otheruserpass")
        assertNotNull(loginResponse)
        assertEquals(HttpStatus.OK, loginResponse.statusCode)
    }

    private fun oAuthLogin(user: String, pass: String): ResponseEntity<OAuthResponse> {
        var loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        var loginData: MultiValueMap<String, String> = LinkedMultiValueMap(mapOf(
                "username" to listOf(user),
                "password" to listOf(pass),
                "grant_type" to listOf("password")
        ))
        val loginRequest = HttpEntity(loginData, loginHeaders)
        val loginResponse = testRestTemplate
                .withBasicAuth("normalClient", "spookysecret")
                .postForEntity("/oauth/token", loginRequest, OAuthResponse::class.java)
        return loginResponse
    }

    private fun oAuthImplicitLogin(user: String, pass: String): ResponseEntity<String> {
        val loginResponse = testRestTemplate
                .withBasicAuth(user, pass)
                .getForEntity("/oauth/authorize?grant_type=implicit&response_type=token&client_id=normalClient&redirect_uri=http://localhost:8080/nul", String::class.java)
        return loginResponse
    }

    data class OAuthResponse(
            @JsonProperty("access_token") val accessToken: String,
            @JsonProperty("token_type") val tokenType: String,
            @JsonProperty("expires_in") val expiresIn: Int,
            @JsonProperty("scope") val scope: String
    )
}
