package com.dgkrajnik.kotlinREST

import com.dgkrajnik.kotlinREST.REST.Auditing.AuditCode
import com.dgkrajnik.kotlinREST.REST.Auditing.Auditable
import com.dgkrajnik.kotlinREST.REST.Errors.EntityNotFoundException
import com.dgkrajnik.kotlinREST.REST.Errors.ValidationFailedException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.security.Principal
import java.text.SimpleDateFormat
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest

@SpringBootApplication
@EnableSwagger2
@EnableAspectJAutoProxy
class KotlinRestApplication

fun main(args: Array<String>) {
    var mapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .setDateFormat(SimpleDateFormat("yyyy-MM-dd hh:mm:ss")) // @JsonFormat doesn't deserialise datetimes right.
    SpringApplication.run(KotlinRestApplication::class.java, *args)
}

@Service
class HelloService {
    fun helloAsAService() = "Hello, Service!"
}

/**
 * A shim for testing out sending some automatically de/serialised data.
 */
data class HelloData(val message: String)
/**
 * A shim for testing out sending some automatically de/serialised data.
 */
data class ShimData(val jsonshim: String)
