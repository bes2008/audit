package com.jn.audit.core;

import com.jn.audit.core.model.Resource;
import com.jn.langx.util.function.Supplier;

public interface ResourceExtractor<Request> extends Supplier<Request, Resource> {
}
