package bcs.testing.test

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import springfox.documentation.swagger2.annotations.EnableSwagger2

// This file is the main spring application
@SpringBootApplication
@EnableSwagger2
class HelloApplication

fun main(args: Array<String>) {
    SpringApplication.run(HelloApplication::class.java, *args)
}
