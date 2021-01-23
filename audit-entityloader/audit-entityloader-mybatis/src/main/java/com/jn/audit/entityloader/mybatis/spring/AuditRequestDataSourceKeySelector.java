package com.jn.audit.entityloader.mybatis.spring;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.AuditEvent;
import com.jn.audit.core.model.ResourceDefinition;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Emptys;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.DataSourceKeySelector;
import com.jn.sqlhelper.datasource.key.MethodInvocationDataSourceKeySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditRequestDataSourceKeySelector implements DataSourceKeySelector<AuditRequest> {
    private static final Logger logger = LoggerFactory.getLogger(AuditRequestDataSourceKeySelector.class);

    private MethodInvocationDataSourceKeySelector delegate;

    public void setDelegate(MethodInvocationDataSourceKeySelector delegate) {
        this.delegate = delegate;
    }

    @Override
    public DataSourceKey select(AuditRequest auditRequest) {
        AuditEvent auditEvent = auditRequest.getAuditEvent();

        ResourceDefinition resourceDefinition = auditEvent.getOperation().getDefinition().getResourceDefinition();

        DataSourceKey key = null;
        String id = resourceDefinition.getDefinitionAccessor().getString("datasource");
        if (Emptys.isNotEmpty(id)) {
            try {
                key = DataSources.buildDataSourceKey(id);
            } catch (Throwable ex) {
                logger.error("error occur when parse datasource key string : {}", id);
            }
        }
        if (key == null && delegate != null && auditRequest.getRequestContext() instanceof MethodInvocation) {
            key = delegate.select((MethodInvocation) auditRequest.getRequestContext());
        }
        return key;
    }
}
