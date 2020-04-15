package com.jn.audit.examples.springmvcdemo.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AuditProperties.class})
public class AuditConfig {

}
