package com.dgkrajnik.kotlinREST

import org.aspectj.lang.annotation.*
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import java.security.Principal
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest

// An annotation which lets the user indicate that a given method should be fully audited.
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Auditable(val auditCode: AuditCode)

@Aspect
@Component
class AuditAspect {
    @Inject
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @Before("@annotation(auditable) && args(httpRequest, principal)")
    fun explicitlyLog(auditable: Auditable, httpRequest: HttpServletRequest, principal: Principal?) {
        applicationEventPublisher.publishEvent(AuditApplicationEvent(
                Date(),
                principal?.name ?: "anon",
                auditable.auditCode.toString(),
                mapOf("user_ip" to httpRequest.remoteAddr)
        ))
    }

    @AfterReturning("@annotation(auditable) && args(httpRequest, principal)")
    fun logSuccess(auditable: Auditable, httpRequest: HttpServletRequest, principal: Principal?) {
        applicationEventPublisher.publishEvent(AuditApplicationEvent(
                Date(),
                principal?.name ?: "anon",
                auditable.auditCode.done().toString(),
                mapOf("user_ip" to httpRequest.remoteAddr)
        ))
    }

    @AfterThrowing("@annotation(auditable) && args(httpRequest, principal)", throwing="ex")
    fun logFailure(auditable: Auditable, httpRequest: HttpServletRequest, principal: Principal?, ex: Exception) {
        applicationEventPublisher.publishEvent(AuditApplicationEvent(
                Date(),
                principal?.name ?: "anon",
                auditable.auditCode.failed().toString(),
                mapOf (
                        "user_ip" to httpRequest.remoteAddr,
                        "exception" to ex
                )
        ))
    }
}
