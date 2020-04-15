package com.jn.audit.core.model;

import java.io.Serializable;

public class Service implements Serializable {
    public static final long serialVersionUID = 1L;

    private String serviceId;   // {optional}, if not specified, equal
    private String serviceName; // {required} the service application
    private String serviceProtocol; // HTTP, HTTPS, TCP etc ...
    private String serviceIp;
    private int servicePort;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceProtocol() {
        return serviceProtocol;
    }

    public void setServiceProtocol(String serviceProtocol) {
        this.serviceProtocol = serviceProtocol;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }
}
