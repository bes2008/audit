package com.jn.audit.mq;

public interface Consumer<M> {
    boolean subscribe(String topic);
    void unsubscribe(String topic);
    void consume(M message);
}
