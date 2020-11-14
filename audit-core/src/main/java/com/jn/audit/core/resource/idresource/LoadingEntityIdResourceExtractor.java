package com.jn.audit.core.resource.idresource;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.Resource;
import com.jn.audit.core.resource.parser.clazz.DefaultEntityClassResourceSupplierParser;
import com.jn.audit.core.resource.supplier.EntityResourceSupplier;
import com.jn.langx.util.struct.Holder;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LoadingEntityIdResourceExtractor<E, AuditedRequest, AuditedRequestContext> extends AbstractIdResourceExtractor<E, AuditedRequest, AuditedRequestContext> {

    private ConcurrentHashMap<Class, Holder<EntityResourceSupplier<E>>> entityResourceSupplierMap = new ConcurrentHashMap<Class, Holder<EntityResourceSupplier<E>>>();

    private DefaultEntityClassResourceSupplierParser parser = DefaultEntityClassResourceSupplierParser.DEFAULT_INSTANCE;

    private EntityLoader entityLoader;

    @Override
    public List<Serializable> findIds(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        return Collections.emptyList();
    }

    @Override
    public List<E> findEntities(AuditRequest<AuditedRequest, AuditedRequestContext> request, List<Serializable> ids) {
        return entityLoader.load(ids);
    }

    @Override
    public Resource extractResource(AuditRequest<AuditedRequest, AuditedRequestContext> request, E entity) {
        return asResource(entity);
    }

    private Resource asResource(E entity) {
        if (entity == null) {
            return null;
        }
        Class entityClass = entity.getClass();
        if (!entityResourceSupplierMap.containsKey(entityClass)) {
            Holder<EntityResourceSupplier<E>> supplier = new Holder<EntityResourceSupplier<E>>();
            try {
                supplier.set(this.parser.parse(entityClass));
            } catch (Throwable ex) {
                // log it
            }
            this.entityResourceSupplierMap.putIfAbsent(entityClass, supplier);
        }
        final Holder<EntityResourceSupplier<E>> supplier = this.entityResourceSupplierMap.get(entityClass);
        if (supplier.isNull()) {
            // log it
            return null;
        }
        return supplier.get().get(entity);
    }

}
