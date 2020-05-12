package com.jn.audit.core.resource.supplier;

import com.jn.langx.util.valuegetter.MemberValueGetter;

public class EntityResourceSupplier<T> extends ContainerResourceSupplier<T, MemberValueGetter> {
    /**
     * 实体类
     */
    private Class<T> entityClass;

    public EntityResourceSupplier(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public String toString() {
        return "EntityResourceSupplier{" +
                "entityClass=" + entityClass +
                '}';
    }
}
