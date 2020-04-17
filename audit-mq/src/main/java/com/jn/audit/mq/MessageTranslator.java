package com.jn.audit.mq;

import com.jn.langx.util.struct.Holder;
import com.lmax.disruptor.EventTranslator;

/**
 * 多个topic不能共用同一个translator
 * @param <M>
 */
public interface MessageTranslator<M> extends EventTranslator<Holder<M>> ,TopicNameAware {
    void setMessage(M message);
    M getMessage();

    @Override
    void translateTo(Holder<M> event, long sequence);
}
