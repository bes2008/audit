package com.jn.audit.entityloader.esmvc;

import com.jn.audit.core.model.ResourceDefinition;
import com.jn.audit.core.resource.idresource.AbstractEntityLoader;
import com.jn.esmvc.service.ESModelService;
import com.jn.langx.factory.Factory;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

public class EsmvcEntityLoader<AuditedRequestContext> extends AbstractEntityLoader<Object, AuditedRequestContext> {
    private static final String SERVICE_IMPL_CLASS_NAME = "serviceImplClassName";
    private static final Logger logger = LoggerFactory.getLogger(EsmvcEntityLoader.class);

    private String name = "esmvc";
    private Factory<Class, ESModelService> esModelServiceFactory;

    public void setName(String name) {
        if (Emptys.isNotEmpty(name)) {
            this.name = name;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    protected int getBatchSize(MapAccessor mapAccessor, List<Serializable> ids) {
        return mapAccessor.getInteger("batchSize", 100);
    }

    @Override
    protected List<Object> loadInternal(AuditedRequestContext auditedRequestContext, ResourceDefinition resourceDefinition, List<Serializable> partitionIds) {
        Preconditions.checkNotNull(esModelServiceFactory, "the EsModelServiceFactory is null or empty");
        if (Emptys.isEmpty(partitionIds)) {
            return null;
        }
        MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        String serviceImplClassName = mapAccessor.getString(SERVICE_IMPL_CLASS_NAME);
        Preconditions.checkNotEmpty(serviceImplClassName, "the {} is undefined in the resource definition", SERVICE_IMPL_CLASS_NAME);
        Class serviceImplClass = null;
        try {
            serviceImplClass = ClassLoaders.loadImplClass(serviceImplClassName, EsmvcEntityLoader.class.getClassLoader(), ESModelService.class);
        } catch (Throwable ex) {
            logger.error("Can't find the ESModel : {}", serviceImplClassName);
            return null;
        }
        ESModelService esModelService = esModelServiceFactory.get(serviceImplClass);
        List<String> stringIds = Pipeline.of(partitionIds).map(new Function<Serializable, String>() {
            @Override
            public String apply(Serializable id) {
                return id.toString();
            }
        }).asList();

        List entities = null;
        try {
            entities = esModelService.getByIds(stringIds);
        } catch (Throwable ex) {
            logger.error("Error occur when loading {} instances, ids: {}", serviceImplClassName, Strings.join(", ", stringIds));
        }
        return entities;

    }

    public Factory<Class, ESModelService> getEsModelServiceFactory() {
        return esModelServiceFactory;
    }

    public void setEsModelServiceFactory(Factory<Class, ESModelService> esModelServiceFactory) {
        this.esModelServiceFactory = esModelServiceFactory;
    }
}
