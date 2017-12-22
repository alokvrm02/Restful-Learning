package com.dgkrajnik.kotlinREST

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.boot.test.context.SpringBootTest

/**
 * Unit tests for the Service
 */
@RunWith(MockitoJUnitRunner::class)
@SpringBootTest
class HelloServiceUnitTest {
    @InjectMocks
    lateinit var helloService: HelloService

    @Test
    fun testHelloService() {
        var result = helloService.helloAsAService()
        assertNotNull(result)
        assertEquals("Hello, Service!", result)
    }
}
