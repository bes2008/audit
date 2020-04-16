package com.jn.audit.core;

import com.jn.langx.factory.Factory;

public interface AuditorFactory<Settings extends AuditSettings> extends Factory<Settings, Auditor> {

}
