package com.jn.audit.core;

import com.jn.audit.core.model.AuditEvent;
import com.jn.audit.mq.Producer;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.struct.ThreadLocalHolder;

public class Auditor<AuditedRequest, AuditedRequestContext> {
    public static final ThreadLocalHolder<AuditRequest> auditRequestHolder = new ThreadLocalHolder<AuditRequest>();
    private AuditRequestFilterChain<AuditedRequest> filterChain;
    private AuditEventExtractor auditEventExtractor;
    private Producer<AuditEvent> producer;

    public void setFilterChain(AuditRequestFilterChain<AuditedRequest> filterChain) {
        this.filterChain = filterChain;
    }

    public void setAuditEventExtractor(AuditEventExtractor auditEventExtractor) {
        this.auditEventExtractor = auditEventExtractor;
    }

    public void setProducer(Producer<AuditEvent> producer) {
        this.producer = producer;
    }

    public void startAudit(AuditedRequest request, AuditedRequestContext ctx) {
        AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest = new AuditRequest<AuditedRequest, AuditedRequestContext>();
        boolean auditIt = true;
        if (Emptys.isNotEmpty(filterChain)) {
            auditIt = filterChain.accept(wrappedRequest);
        }
        if (auditIt) {
            AuditEvent auditEvent = auditEventExtractor.get(wrappedRequest);
            wrappedRequest.setAuditEvent(auditEvent);
            wrappedRequest.setAuditIt(auditIt);
            auditRequestHolder.set(wrappedRequest);
        }
    }

    public void finishAudit() {
        AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest = auditRequestHolder.get();
        if (wrappedRequest != null) {
            String topic = null;
            producer.publish(topic, wrappedRequest.getAuditEvent());
            auditRequestHolder.reset();
        }

    }


}
