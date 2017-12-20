package bcs.testing.test

import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import javax.inject.Inject
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

val client_id = "trusted-client"
val client_secret = "secret"

// INTEGRATION TESTS
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class HelloEndpointIntegrationTest {
    @Inject
    lateinit var testRestTemplate: TestRestTemplate

    @Inject
    lateinit var mockmvc: MockMvc

    @Test
    fun noToken() {
        mockmvc.perform(get("/hello/string"))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun testAccessString() {
        val accessToken = obtainAccessToken("admin", "admin")
        mockmvc.perform(get("/hello/string")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Hello string!")))
    }

    @Test
    fun testNoAccess() {
        // role is USER, so doesn't have access
        val accessToken = obtainAccessToken("bob", "belcher")
        mockmvc.perform(get("/hello/string")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden)
    }

    @Test
    fun testHelloService() {
        // resource protected to USER role
        val accessToken = obtainAccessToken("bob", "belcher")
        mockmvc.perform(get("/hello/service")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Hello service!")))
    }

    @Test
    fun testHelloData() {
        val accessToken = obtainAccessToken("admin", "admin")
        mockmvc.perform(get("/hello/data")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Hello data!")))
    }

    /**
     * Obtains an access token from the auth server and returns it as a string.
     */
    fun obtainAccessToken(username: String, password: String): String {
        var params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("grant_type", "password")
        params.add("client_id", client_id)
        params.add("username", username)
        params.add("password", password)

        var result: ResultActions =
                mockmvc.perform(post("/oauth/token")
                        .params(params)
                        .with(httpBasic(client_id, client_secret))
                        .accept("application/json;charset=UTF-8"))
                        .andExpect(status().isOk)
                        .andExpect(content().contentType("application/json;charset=UTF-8"))
        var resultString = result.andReturn().response.contentAsString

        var jsonParser: JacksonJsonParser = JacksonJsonParser()
        return jsonParser.parseMap(resultString).get("access_token").toString()
    }
}

// UNIT TESTS
// > Controller
@RunWith(MockitoJUnitRunner::class)
class HelloControllerUnitTest {
    @InjectMocks
    lateinit var helloController: HelloController
    @Mock
    lateinit var helloService: HelloService
    @Test
    fun testHelloController() {
        val result = helloController.helloString()
        assertNotNull(result)
        assertEquals("Hello string!", result)
    }
    @Test
    fun testHelloService() {
        doReturn("Hello service!").`when`(helloService).getHello()
        val result = helloController.helloService()
        assertNotNull(result)
        assertEquals("Hello service!", result)
    }
    @Test
    fun testHelloDto() {
        val result = helloController.helloData()
        assertNotNull(result)
        assertEquals(Hello("Hello data!"), result)
    }
}

// > Service
@RunWith(MockitoJUnitRunner::class)
class HelloServiceUnitTest {
    @InjectMocks
    lateinit var helloService: HelloService
    @Test
    fun testHelloController() {
        val result = helloService.getHello()
        assertNotNull(result)
        assertEquals("Hello service!", result)
    }
}
