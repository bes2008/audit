package com.jn.audit.core.resource;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.Resource;
import com.jn.langx.util.function.Supplier;

import java.util.List;

/**
 * 提取 Resource
 * @param <AuditedRequest>
 * @param <AuditedRequestContext>
 */
public interface ResourceExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, List<Resource>> {

}
