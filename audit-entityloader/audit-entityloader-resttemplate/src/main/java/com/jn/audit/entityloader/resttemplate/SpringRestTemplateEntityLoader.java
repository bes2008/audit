package com.jn.audit.entityloader.resttemplate;

import com.jn.audit.core.model.ResourceDefinition;
import com.jn.audit.core.resource.idresource.EntityLoader;

import java.io.Serializable;
import java.util.List;

public class SpringRestTemplateEntityLoader implements EntityLoader<Object> {

    //private RestTemplate restTemplate;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<Object> load(ResourceDefinition resourceDefinition, List<Serializable> ids) {
        return null;
    }
}
