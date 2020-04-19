package com.jn.audit.core;

import com.jn.audit.core.model.AuditEvent;
import com.jn.audit.mq.MessageTopicDispatcher;
import com.jn.audit.mq.MessageTopicDispatcherAware;
import com.jn.audit.mq.Producer;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.Destroyable;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.concurrent.WrappedTasks;
import com.jn.langx.util.concurrent.completion.CompletableFuture;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Function2;
import com.jn.langx.util.struct.ThreadLocalHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

public class Auditor<AuditedRequest, AuditedRequestContext> implements Initializable, Destroyable, MessageTopicDispatcherAware {
    private static Logger logger = LoggerFactory.getLogger(Auditor.class);
    public static ThreadLocalHolder<AuditRequest> auditRequestHolder = new ThreadLocalHolder<AuditRequest>();
    private static ThreadLocalHolder<CompletableFuture<Void>> asyncTaskHolder = new ThreadLocalHolder<CompletableFuture<Void>>();
    @NonNull
    private AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> beforeExtractFilterChain;

    @NonNull
    private AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> afterExtractFilterChain;

    @NonNull
    private AuditEventExtractor<AuditedRequest, AuditedRequestContext> auditEventExtractor;
    @NonNull
    private Producer<AuditEvent> producer;
    @Nullable
    private Executor executor;
    private boolean asyncAudit = true;
    private Function2<AuditedRequest, AuditedRequestContext, AuditRequest<AuditedRequest, AuditedRequestContext>> auditRequestFactory;

    @Override
    public void destroy() {
        executor = null;
    }

    @Override
    public void init() throws InitializationException {

    }

    public boolean isAsyncAudit(AuditedRequest request) {
        if (asyncAudit && executor != null && request != null) {
            try {
                Class httpServletRequest = ClassLoaders.loadClass("javax.servlet.http.HttpServletRequest", request.getClass().getClassLoader());
                return httpServletRequest == null;
            } catch (ClassNotFoundException ex) {
                return true;
            }
        }
        return false;
    }

    public void setAsyncAudit(boolean asyncAudit) {
        this.asyncAudit = asyncAudit;
    }


    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public Executor getExecutor() {
        return executor;
    }


    public void setProducer(Producer<AuditEvent> producer) {
        this.producer = producer;
    }


    public Producer<AuditEvent> getProducer() {
        return producer;
    }

    /**
     * 该方法应用于 非AOP情况下， 即不需要在某个方法前、后执行的情况。
     *
     * @param request
     * @param ctx
     */
    public void doAudit(AuditedRequest request, AuditedRequestContext ctx) {
        if (isAsyncAudit(request)) {
            finishAsyncAudit(startAsyncAudit(request, ctx));
        } else {
            finishSyncAudit(startSyncAudit(request, ctx));
        }
    }

    public AuditRequest<AuditedRequest, AuditedRequestContext> startAudit(final AuditedRequest request, final AuditedRequestContext ctx) {
        if (isAsyncAudit(request)) {
            return startAsyncAudit(request, ctx);
        } else {
            return startSyncAudit(request, ctx);
        }
    }

    public AuditRequest<AuditedRequest, AuditedRequestContext> startAsyncAudit(final AuditedRequest request, final AuditedRequestContext ctx) {
        final AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest = createAuditRequest(request, ctx);
        auditRequestHolder.set(wrappedRequest);
        logger.warn("start async audit {}", wrappedRequest.toString());
        wrappedRequest.setStartTime(System.currentTimeMillis());
        final ClassLoader mThreadClassLoader = Thread.currentThread().getContextClassLoader();
        asyncTaskHolder.set(CompletableFuture.runAsync(WrappedTasks.wrap(new Runnable() {
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
        }), executor));
        return wrappedRequest;
    }

    public AuditRequest<AuditedRequest, AuditedRequestContext> startSyncAudit(final AuditedRequest request, final AuditedRequestContext ctx) {
        final AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest = createAuditRequest(request, ctx);
        logger.warn("start sync audit {}", wrappedRequest.toString());
        wrappedRequest.setStartTime(System.currentTimeMillis());
        startAuditInternal(wrappedRequest);
        auditRequestHolder.set(wrappedRequest);
        return wrappedRequest;
    }


    private AuditRequest<AuditedRequest, AuditedRequestContext> startAuditInternal(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        boolean auditIt = true;
        // may be too long time
        if (Emptys.isNotEmpty(beforeExtractFilterChain)) {
            auditIt = beforeExtractFilterChain.accept(wrappedRequest);
        }
        if (!auditIt) {
            wrappedRequest.setAuditIt(auditIt);
            return wrappedRequest;
        }
        AuditEvent auditEvent = auditEventExtractor.get(wrappedRequest);
        wrappedRequest.setAuditEvent(auditEvent);
        if (Emptys.isNotEmpty(afterExtractFilterChain)) {
            auditIt = afterExtractFilterChain.accept(wrappedRequest);
        }
        if (auditIt) {
            wrappedRequest.setAuditEvent(auditEvent);
            wrappedRequest.setAuditIt(auditIt);
        }
        return auditIt ? wrappedRequest : null;
    }


    public void finishAudit(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        if (isAsyncAudit(wrappedRequest.getRequest())) {
            finishAsyncAudit(wrappedRequest);
        } else {
            finishSyncAudit(wrappedRequest);
        }
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

    public void finishAsyncAudit(final AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        wrappedRequest.setEndTime(System.currentTimeMillis());
        logger.warn("finish sync audit {}", wrappedRequest.toString());
        CompletableFuture future = asyncTaskHolder.get();
        if (future != null) {
            future.thenRunAsync(WrappedTasks.wrap(new Runnable() {
                @Override
                public void run() {
                    finishAuditInternal(wrappedRequest);
                }
            }));
        }
        asyncTaskHolder.reset();
        auditRequestHolder.reset();
    }

    private void finishAuditInternal(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        if (wrappedRequest != null) {
            AuditEvent event = wrappedRequest.getAuditEvent();
            if (wrappedRequest.isAuditIt() && event != null) {
                event.setStartTime(wrappedRequest.getStartTime());
                event.setEndTime(wrappedRequest.getEndTime());
                event.setDuration(wrappedRequest.getEndTime() - wrappedRequest.getStartTime());
                producer.publish(wrappedRequest.getTopic(), event);
            }
        }
    }

    @Override
    public MessageTopicDispatcher getMessageTopicDispatcher() {
        return producer.getMessageTopicDispatcher();
    }

    @Override
    public void setMessageTopicDispatcher(MessageTopicDispatcher dispatcher) {
        producer.setMessageTopicDispatcher(dispatcher);
    }

    private AuditRequest<AuditedRequest, AuditedRequestContext> createAuditRequest(AuditedRequest auditedRequest, AuditedRequestContext ctx) {
        AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest = null;
        if (auditRequestFactory != null) {
            wrappedRequest = auditRequestFactory.apply(auditedRequest, ctx);
        }
        if (wrappedRequest == null) {
            wrappedRequest = new AuditRequest<AuditedRequest, AuditedRequestContext>();
            wrappedRequest.setRequest(auditedRequest);
            wrappedRequest.setRequestContext(ctx);
        }
        return wrappedRequest;
    }

    public Function2<AuditedRequest, AuditedRequestContext, AuditRequest<AuditedRequest, AuditedRequestContext>> getAuditRequestFactory() {
        return auditRequestFactory;
    }

    public void setAuditRequestFactory(Function2<AuditedRequest, AuditedRequestContext, AuditRequest<AuditedRequest, AuditedRequestContext>> auditRequestFactory) {
        this.auditRequestFactory = auditRequestFactory;
    }

    public AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> getBeforeExtractFilterChain() {
        return beforeExtractFilterChain;
    }

    public void setBeforeExtractFilterChain(AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> beforeExtractFilterChain) {
        this.beforeExtractFilterChain = beforeExtractFilterChain;
    }

    public AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> getAfterExtractFilterChain() {
        return afterExtractFilterChain;
    }

    public void setAfterExtractFilterChain(AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> afterExtractFilterChain) {
        this.afterExtractFilterChain = afterExtractFilterChain;
    }

    public AuditEventExtractor<AuditedRequest, AuditedRequestContext> getAuditEventExtractor() {
        return auditEventExtractor;
    }

    public void setAuditEventExtractor(AuditEventExtractor<AuditedRequest, AuditedRequestContext> auditEventExtractor) {
        this.auditEventExtractor = auditEventExtractor;
    }
}
