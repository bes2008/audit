package com.jn.audit.core.resource.idresource;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.ResourceDefinition;

import java.io.Serializable;
import java.util.List;

public interface EntityLoader<E, AuditedRequest, AuditedRequestContext> {
    String getName();

    List<E> load(AuditRequest<AuditedRequest, AuditedRequestContext> request, ResourceDefinition resourceDefinition, List<Serializable> ids);
}
