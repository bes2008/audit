package com.jn.audit.core;

import com.jn.audit.core.model.*;
import com.jn.langx.Builder;

public class AuditEventBuilder implements Builder<AuditEvent> {
    private AuditEvent event = new AuditEvent();

    public AuditEventBuilder principal(Principal principal) {
        event.setPrincipal(principal);
        return this;
    }

    public AuditEventBuilder operate(Operate operate) {
        event.setOperate(operate);
        return this;
    }

    public AuditEventBuilder resource(Resource resource) {
        event.setResource(resource);
        return this;
    }

    public AuditEventBuilder service(Service service) {
        event.setService(service);
        return this;
    }

    @Override
    public AuditEvent build() {
        return event;
    }
}
