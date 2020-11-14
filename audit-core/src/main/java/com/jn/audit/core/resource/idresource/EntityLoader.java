package com.jn.audit.core.resource.idresource;

import java.io.Serializable;
import java.util.List;

public interface EntityLoader<E> {
    List<E> load(List<Serializable> ids);
}
