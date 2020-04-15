package com.jn.audit.core;

import com.jn.audit.core.model.AuditEvent;
import com.jn.audit.mq.Producer;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.concurrent.WrappedTasks;
import com.jn.langx.util.struct.ThreadLocalHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

public class Auditor<AuditedRequest, AuditedRequestContext> {
    private static Logger logger = LoggerFactory.getLogger(Auditor.class);
    public static final ThreadLocalHolder<AuditRequest> auditRequestHolder = new ThreadLocalHolder<AuditRequest>();
    private AuditRequestFilterChain<AuditedRequest> filterChain;
    private AuditEventExtractor auditEventExtractor;
    private Producer<AuditEvent> producer;
    private OperationExtractor<AuditedRequest, AuditedRequestContext> operationExtractor;
    private Executor executor;

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setFilterChain(AuditRequestFilterChain<AuditedRequest> filterChain) {
        this.filterChain = filterChain;
    }

    public void setAuditEventExtractor(AuditEventExtractor auditEventExtractor) {
        this.auditEventExtractor = auditEventExtractor;
    }

    public void setProducer(Producer<AuditEvent> producer) {
        this.producer = producer;
    }

    public void doAudit(AuditedRequest request, AuditedRequestContext ctx) {
        if (executor != null) {
            finishAsyncAudit(startAsyncAudit(request, ctx));
        } else {
            finishSyncAudit(startSyncAudit(request, ctx));
        }
    }

    public AuditRequest<AuditedRequest, AuditedRequestContext> startAsyncAudit(final AuditedRequest request, final AuditedRequestContext ctx) {
        final AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest = new AuditRequest<AuditedRequest, AuditedRequestContext>();
        wrappedRequest.setStartTime(System.currentTimeMillis());
        executor.execute(WrappedTasks.wrap(new Runnable() {
            @Override
            public void run() {
                startAuditInternal(wrappedRequest);
            }
        }));
        return wrappedRequest;
    }

    public AuditRequest<AuditedRequest, AuditedRequestContext> startSyncAudit(final AuditedRequest request, final AuditedRequestContext ctx) {
        final AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest = new AuditRequest<AuditedRequest, AuditedRequestContext>();
        wrappedRequest.setStartTime(System.currentTimeMillis());
        startAuditInternal(wrappedRequest);
        auditRequestHolder.set(wrappedRequest);
        return wrappedRequest;
    }


    private AuditRequest<AuditedRequest, AuditedRequestContext> startAuditInternal(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {

        String operationName = operationExtractor.extractOperationName(wrappedRequest);
        boolean auditIt = true;
        // may be too long time
        if (Emptys.isNotEmpty(filterChain)) {
            auditIt = filterChain.accept(wrappedRequest);
        }
        if (auditIt) {
            AuditEvent auditEvent = auditEventExtractor.get(wrappedRequest);
            wrappedRequest.setAuditEvent(auditEvent);
            wrappedRequest.setAuditIt(auditIt);
        }
        return auditIt ? wrappedRequest : null;
    }


    public void finishSyncAudit(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        try {
            wrappedRequest.setEndTime(System.currentTimeMillis());
            finishAuditInternal(wrappedRequest);
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            auditRequestHolder.reset();
        }
    }

    public void finishAsyncAudit(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        wrappedRequest.setEndTime(System.currentTimeMillis());
        finishAuditInternal(wrappedRequest);
    }

    private void finishAuditInternal(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        if (wrappedRequest != null) {
            String topic = null;
            producer.publish(topic, wrappedRequest.getAuditEvent());
        }
    }


}
