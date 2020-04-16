package com.jn.audit.examples.springmvcdemo.common.config;

import com.jn.audit.core.AuditSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "audit")
public class AuditProperties extends AuditSettings {
    private List<String> topics;

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
