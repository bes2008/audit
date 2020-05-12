package com.jn.audit.core.resource;

import com.jn.audit.core.model.Resource;
import com.jn.langx.util.valuegetter.ValueGetter;

/**
 *
 * @param <T>  the entity
 */
public interface ResourceSupplier<T> extends ValueGetter<T, Resource> {
}
