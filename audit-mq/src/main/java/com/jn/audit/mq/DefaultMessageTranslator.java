package com.jn.audit.mq;

import com.jn.langx.util.struct.Holder;

public class DefaultMessageTranslator<M> implements MessageTranslator<M> {
    private M message;
    private String topicName;

    @Override
    public String getTopicName() {
        return topicName;
    }

    @Override
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public void setMessage(M message) {
        this.message = message;
    }

    @Override
    public M getMessage() {
        return message;
    }

    @Override
    public void translateTo(Holder<M> event, long sequence) {
        event.set(message);
    }
}
