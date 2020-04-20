package com.jn.audit.spring.boot.autoconfigure;

import com.jn.audit.core.AuditSettings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "audit")
@ConditionalOnMissingBean(name = "auditProperties")
public class AuditProperties extends AuditSettings {
    private boolean enabled = true;
    private List<String> httpInterceptorPatterns;
    private boolean debugConsumerEnabled = true;

    public boolean isDebugConsumerEnabled() {
        return debugConsumerEnabled;
    }

    public void setDebugConsumerEnabled(boolean debugConsumerEnabled) {
        this.debugConsumerEnabled = debugConsumerEnabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getHttpInterceptorPatterns() {
        return httpInterceptorPatterns;
    }

    public void setHttpInterceptorPatterns(List<String> httpInterceptorPatterns) {
        this.httpInterceptorPatterns = httpInterceptorPatterns;
    }
}
