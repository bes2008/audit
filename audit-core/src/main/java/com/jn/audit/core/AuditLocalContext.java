package com.jn.audit.core;


import com.jn.langx.util.struct.ThreadLocalHolder;

public class AuditLocalContext {
    public static final ThreadLocalHolder<AuditRequest> auditRequestHolder = new ThreadLocalHolder<AuditRequest>();

}
