package bcs.testing.test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import javax.inject.Inject


// INTEGRATION TESTS
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloEndointIntegrationTest {
    @Inject
    lateinit var testRestTemplate: TestRestTemplate
    @Test
    fun testHelloController() {
        val result = testRestTemplate
                .withBasicAuth("admin","admin")
                .getForEntity("/hello/string", String::class.java)
        assertNotNull(result)
        assertEquals(result.statusCode, HttpStatus.OK)
        assertEquals(result.body, "Hello string!")
    }
    @Test
    fun testHelloService() {
        val result = testRestTemplate
                .withBasicAuth("admin","admin")
                .getForEntity("/hello/service", String::class.java)
        assertNotNull(result)
        assertEquals(result.statusCode, HttpStatus.OK)
        assertEquals(result.body, "Hello service!")
    }
    @Test
    fun testHelloDto() {
        val result = testRestTemplate
                .withBasicAuth("admin","admin")
                .getForEntity("/hello/data", Hello::class.java)
        assertNotNull(result)
        assertEquals(result.statusCode, HttpStatus.OK)
        assertEquals(result.body, Hello("Hello data!"))
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
