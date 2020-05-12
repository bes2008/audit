package com.jn.audit.core.resource.parser.clazz;

import com.jn.audit.core.resource.parser.ResourceSupplierParser;
import com.jn.audit.core.resource.supplier.EntityResourceSupplier;

/**
 * 根据Entity Class 来解析出 ResourceSupplier
 */
public interface EntityClassResourceSupplierParser<T> extends ResourceSupplierParser<Class<T>, EntityResourceSupplier<T>> {


}
