package bcs.testing.test.REST.Error

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import javax.validation.ConstraintViolationException


@RestControllerAdvice
class GlobalControllerExceptionHandler {

    val log = KotlinLogging.logger {  }

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun constraintViolationException(ex: ConstraintViolationException): ErrorResponse {
        log.info{ "Bad Request - 5001"}
        return ErrorResponse(500, 5001, ex.message)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun noHandlerFound(ex: Exception, req: WebRequest): ErrorResponse {
        log.info { "No Handler Found - 4041" }
        return ErrorResponse(404, 4041, ex.message)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun unknownException(ex: Exception, req: WebRequest): ErrorResponse {
        log.info { "Unhandled Error - 5002" }
        return ErrorResponse(500, 5002, ex.message)
    }

}