package com.jn.audit.spring.boot.autoconfigure;

public class AuditAdvisorPointcutProperties {
    private String expression;
    private int order;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
