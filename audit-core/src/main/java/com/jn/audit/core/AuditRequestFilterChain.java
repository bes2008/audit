package com.jn.audit.core;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;

import java.util.List;

public class AuditRequestFilterChain<AuditedRequest> implements AuditRequestFilter<AuditedRequest> {

    private List<AuditRequestFilter<AuditedRequest>> filters = Collects.newArrayList();

    @Override
    public boolean accept(final AuditRequest<AuditedRequest> auditRequest) {
        return Collects.allMatch(filters, new Predicate<AuditRequestFilter<AuditedRequest>>() {
            @Override
            public boolean test(AuditRequestFilter<AuditedRequest> filter) {
                return filter.accept(auditRequest);
            }
        });
    }

    public void addFilter(AuditRequestFilter<AuditedRequest> filter) {
        filters.add(filter);
    }
}
