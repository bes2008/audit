package com.jn.audit.examples.springmvcdemo.common.config;

import com.jn.audit.core.AuditSettings;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "audit")
public class AuditProperties extends AuditSettings {

}
