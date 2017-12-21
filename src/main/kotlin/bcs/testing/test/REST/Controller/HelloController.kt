package bcs.testing.test.REST.Controller

import io.swagger.annotations.ApiOperation
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@RestController
@RequestMapping("hello") // /hello route
class HelloController {

    private val log = KotlinLogging.logger {  }

    // Dependency injections
    @Inject
    lateinit var helloService: HelloService

    // Routes
    @ApiOperation(value = "getHelloString", nickname = "getHelloString")
    @GetMapping("/string")
    fun helloString(): String {
        log.info { "hello/string accessed" }
        return "Hello string!"
    }

    @GetMapping("/service")
    fun helloService(): String {
        log.info { "hello/service accessed" }
        return helloService.getHello()
    }

    @GetMapping("/data")
    fun helloData(): Hello {
        log.info{ "hello/data accessed" }
        return Hello("Hello data!")
    }

    @GetMapping("/unsecured")
    fun helloUnprotected(): String {
        log.info { "hello/unprotected accessed" }
        return "Beep boop"
    }
}
