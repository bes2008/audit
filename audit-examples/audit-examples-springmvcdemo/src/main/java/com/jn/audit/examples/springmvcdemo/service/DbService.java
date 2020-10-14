package com.jn.audit.examples.springmvcdemo.service;

import com.jn.agileway.dmmq.core.Consumer;
import com.jn.agileway.dmmq.core.MessageHolder;
import com.jn.audit.core.model.AuditEvent;
import com.jn.langx.util.struct.Holder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DbService implements Consumer<AuditEvent> {
    private static final Logger logger = LoggerFactory.getLogger(DbService.class);

    @Override
    public String getName() {
        return "Audit-Database-Consumer";
    }

    @Override
    public void onEvent(MessageHolder<AuditEvent> event, long sequence, boolean endOfBatch) throws Exception {
        logger.info("DB service");
    }

    @Override
    public void handleEventException(Throwable ex, long sequence, Holder<AuditEvent> event) {

    }

    @Override
    public void handleOnStartException(Throwable ex) {

    }

    @Override
    public void handleOnShutdownException(Throwable ex) {

    }

    @Override
    public void onTimeout(long sequence) throws Exception {

    }
}
