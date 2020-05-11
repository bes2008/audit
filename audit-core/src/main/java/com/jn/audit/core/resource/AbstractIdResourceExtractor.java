package com.jn.audit.core.resource;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.Resource;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractIdResourceExtractor<E, AuditedRequest, AuditedRequestContext> implements ResourceExtractor<AuditedRequest, AuditedRequestContext> {

    public abstract List<Serializable> findIds(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest);

    public abstract List<E> findEntities(List<Serializable> ids);

    @Override
    public List<Resource> get(final AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        List<Serializable> ids = findIds(wrappedRequest);
        return findResources(wrappedRequest.getRequest(), ids);
    }

    public List<Resource> findResources(final AuditedRequest request, List<Serializable> ids) {
        List<E> entities = findEntities(ids);
        return Pipeline.<E>of(entities).map(new Function<E, Resource>() {
            @Override
            public Resource apply(E e) {
                return extractResource(request, e);
            }
        }).asList();
    }

    public abstract Resource extractResource(AuditedRequest request, E entity);
}
