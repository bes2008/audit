package com.jn.audit.core.resource.idresource;

import com.jn.langx.registry.Registry;
import com.jn.langx.util.Preconditions;

import java.util.concurrent.ConcurrentHashMap;

public class EntityLoaderRegistry implements Registry<String, EntityLoader> {
    private final ConcurrentHashMap<String, EntityLoader> map = new ConcurrentHashMap<String, EntityLoader>();

    @Override
    public void register(EntityLoader entityLoader) {
        register(entityLoader.getName(), entityLoader);
    }

    @Override
    public void register(String name, EntityLoader entityLoader) {
        if(entityLoader instanceof EntityLoaderDispatcher){
            return;
        }
        Preconditions.checkNotEmpty(name,"the entity loader name is null or empty");
        map.put(name, entityLoader);
    }

    @Override
    public EntityLoader get(String name) {
        return map.get(name);
    }
}
