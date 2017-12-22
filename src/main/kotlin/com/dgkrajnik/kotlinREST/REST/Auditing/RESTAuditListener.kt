package com.dgkrajnik.kotlinREST.REST.Auditing

import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

enum class AfterAuditCode {
    DONE_BEING_WATCHED,
    FAIL_BEING_WATCHED
}

enum class AuditCode {
    BEING_WATCHED {
        override fun done() = AfterAuditCode.DONE_BEING_WATCHED
        override fun failed() = AfterAuditCode.FAIL_BEING_WATCHED
    };

    abstract fun done(): AfterAuditCode
    abstract fun failed(): AfterAuditCode
}

@Component
class AuditEventListener : ApplicationListener<AuditApplicationEvent> {
    val logger: Logger = LoggerFactory.getLogger("Audits")

    override fun onApplicationEvent(event: AuditApplicationEvent) {
        logger.info("""AUDIT EVENT - ${event.auditEvent.type}
           | - From principal - ${event.auditEvent.principal}
           | - At ip - ${event.auditEvent.data?.get("user_ip") ?: "NONE"}
           |${event.auditEvent.data?.get("exception")?.let { " - Exception: $it" } ?: ""}"""
                .trimMargin("|").replace("\n", ""))
    }
}
