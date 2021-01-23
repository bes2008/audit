package com.jn.audit.core.resource.idresource;

import com.jn.audit.core.model.ResourceDefinition;

import java.io.Serializable;
import java.util.List;

public interface EntityLoader<E, AuditedRequestContext> {
    String getName();

    List<E> load(AuditedRequestContext auditedRequestContext, ResourceDefinition resourceDefinition, List<Serializable> ids);
}
