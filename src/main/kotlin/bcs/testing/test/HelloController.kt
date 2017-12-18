package bcs.testing.test

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@RestController
@RequestMapping("hello") // /hello route
class HelloController {
    // Dependency injections
    @Inject
    lateinit var helloService: HelloService

    // Routes
    @GetMapping("/string")
    fun helloString() = "Hello string!"

    @GetMapping("/service")
    fun helloService() = helloService.getHello()

    @GetMapping("/data")
    fun helloData() = Hello("Hello data!")
}
