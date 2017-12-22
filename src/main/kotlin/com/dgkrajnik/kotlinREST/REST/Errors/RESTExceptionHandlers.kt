package com.dgkrajnik.kotlinREST.REST.Errors

import com.dgkrajnik.kotlinREST.REST.Errors.ApiError
import com.dgkrajnik.kotlinREST.REST.Errors.ApiErrorBuilder
import com.dgkrajnik.kotlinREST.REST.Errors.EntityNotFoundException
import com.dgkrajnik.kotlinREST.REST.Errors.ValidationFailedException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException as SpringAccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * Holder for RESTful exception handlers.
 */
@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {
    /**
     * Handler for spring-thrown exceptions when an HTTP request was not readable, for some generic reason.
     */
    override fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val error = "Malformed JSON Request"
        return buildResponseEntity(ApiErrorBuilder(HttpStatus.BAD_REQUEST, error, ex).build())
    }

    /**
     * Handler for spring-thrown exceptions when a client tries to access a nonexistent resource/endpoint.
     */
    override fun handleNoHandlerFoundException(ex: NoHandlerFoundException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val error = "Resource Not Found"
        return buildResponseEntity(ApiErrorBuilder(HttpStatus.NOT_FOUND, error, ex).build())
    }

    /**
     * Handler for developer-thrown exceptions when entities are not found.
     */
    @ExceptionHandler(EntityNotFoundException::class)
    protected fun handleEntityNotFound(ex: EntityNotFoundException): ResponseEntity<Any> {
        val error = "Entity Not Found"
        return buildResponseEntity(ApiErrorBuilder(HttpStatus.NOT_FOUND, error, ex).build())
    }

    /**
     * Handler for developer-thrown exceptions when validation of input data has failed.
     */
    @ExceptionHandler(ValidationFailedException::class)
    protected fun handleValidationFailed(ex: ValidationFailedException): ResponseEntity<Any> {
        val error = "Error in Request Data"
        val apiError = ApiErrorBuilder(HttpStatus.BAD_REQUEST, error, ex)
        apiError.addSubError(ex.asApiValidationError())
        return buildResponseEntity(apiError.build())
    }

    /**
     * Exception handler specifically for annotation-based method authorization.
     */
    @ExceptionHandler(AccessDeniedException::class)
    protected fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<Any> {
        val error = "Access Denied to This Resource"
        return buildResponseEntity(ApiErrorBuilder(HttpStatus.UNAUTHORIZED, error, ex).build())
    }

    /**
     * A little helper to build response entities from APIErrors.
     */
    private fun buildResponseEntity(apiError: ApiError): ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        return ResponseEntity(apiError, headers, apiError.status)
    }
}
