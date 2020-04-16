package com.jn.audit.examples.springmvcdemo.common.config;

import com.jn.audit.core.AuditSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "audit")
public class AuditProperties extends AuditSettings {

}
