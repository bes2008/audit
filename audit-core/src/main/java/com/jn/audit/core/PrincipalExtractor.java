package com.jn.audit.core;

import com.jn.audit.core.model.Principal;
import com.jn.langx.util.function.Supplier;

public interface PrincipalExtractor<Request> extends Supplier<Request, Principal> {
}
