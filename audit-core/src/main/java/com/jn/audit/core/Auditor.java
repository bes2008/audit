package com.jn.audit.core;

import com.jn.audit.core.model.AuditEvent;
import com.jn.audit.mq.Producer;
import com.jn.langx.lifecycle.Destroyable;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.concurrent.WrappedTasks;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.struct.ThreadLocalHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

public class Auditor<AuditedRequest, AuditedRequestContext> implements Initializable, Destroyable {
    private static Logger logger = LoggerFactory.getLogger(Auditor.class);
    public static ThreadLocalHolder<AuditRequest> auditRequestHolder = new ThreadLocalHolder<AuditRequest>();
    private AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> filterChain;
    private AuditEventExtractor<AuditedRequest, AuditedRequestContext> auditEventExtractor;
    private Producer<AuditEvent> producer;
    private Executor executor;
    private boolean asyncAudit = false;

    @Override
    public void destroy() {
        executor = null;
    }

    @Override
    public void init() throws InitializationException {

    }

    public boolean isAsyncAudit() {
        return asyncAudit && executor != null;
    }

    public void setAsyncAudit(boolean asyncAudit) {
        this.asyncAudit = asyncAudit;
    }


    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setFilterChain(AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> filterChain) {
        this.filterChain = filterChain;
    }

    public void setAuditEventExtractor(AuditEventExtractor<AuditedRequest, AuditedRequestContext> auditEventExtractor) {
        this.auditEventExtractor = auditEventExtractor;
    }

    public void setProducer(Producer<AuditEvent> producer) {
        this.producer = producer;
    }

    public void doAudit(AuditedRequest request, AuditedRequestContext ctx) {
        if (isAsyncAudit()) {
            finishAsyncAudit(startAsyncAudit(request, ctx));
        } else {
            finishSyncAudit(startSyncAudit(request, ctx));
        }
    }

    public AuditRequest<AuditedRequest, AuditedRequestContext> startAsyncAudit(final AuditedRequest request, final AuditedRequestContext ctx) {
        final AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest = new AuditRequest<AuditedRequest, AuditedRequestContext>();
        logger.warn("start async audit {}", wrappedRequest.toString());
        wrappedRequest.setStartTime(System.currentTimeMillis());
        final ClassLoader mThreadClassLoader = Thread.currentThread().getContextClassLoader();
        executor.execute(WrappedTasks.wrap(new Runnable() {
            @Override
            public void run() {
                ClassLoaders.doAction(mThreadClassLoader, new Function<Object, Object>() {
                    @Override
                    public Object apply(Object input) {
                        startAuditInternal(wrappedRequest);
                        return null;
                    }
                }, null);
            }
        }));
        return wrappedRequest;
    }

    public AuditRequest<AuditedRequest, AuditedRequestContext> startSyncAudit(final AuditedRequest request, final AuditedRequestContext ctx) {
        final AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest = new AuditRequest<AuditedRequest, AuditedRequestContext>();
        logger.warn("start sync audit {}", wrappedRequest.toString());
        wrappedRequest.setStartTime(System.currentTimeMillis());
        startAuditInternal(wrappedRequest);
        auditRequestHolder.set(wrappedRequest);
        return wrappedRequest;
    }


    private AuditRequest<AuditedRequest, AuditedRequestContext> startAuditInternal(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
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
            logger.warn("finish sync audit {}", wrappedRequest.toString());
            auditRequestHolder.reset();
        }
    }

    public void finishAsyncAudit(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        wrappedRequest.setEndTime(System.currentTimeMillis());
        logger.warn("finish sync audit {}", wrappedRequest.toString());
        finishAuditInternal(wrappedRequest);
    }

    private void finishAuditInternal(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        if (wrappedRequest != null) {
            String topic = null;
            producer.publish(topic, wrappedRequest.getAuditEvent());
        }
    }


}
