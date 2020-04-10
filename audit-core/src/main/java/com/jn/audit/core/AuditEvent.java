package com.jn.audit.core;

import java.io.Serializable;

public class AuditEvent implements Serializable {
    public static final long serialVersionUID = 1L;
    private String id;

    // who
    private String principalId;
    private String principalName;
    private PrincipalType principalType;
    private String clientIp;    // {required} ip
    private int clientPort;     // {required} port

    // when
    private long time;  // {required} UTC time

    /**
     * channel
     */
    private String service; // {required}
    private String serviceProtocol; // HTTP, HTTPS, TCP
    private String serviceIp;
    private int servicePort;

    /**
     * target
     */
    private String resourceId;
    private String resourceType;
    private String resourceName;

    /**
     * do what
     */
    private String operateCode;
    private String operateName;
    private String parameters;
}
