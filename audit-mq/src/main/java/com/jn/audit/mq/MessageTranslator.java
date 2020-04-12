package com.jn.audit.mq;

import com.jn.langx.util.struct.Holder;
import com.lmax.disruptor.EventTranslator;


public interface MessageTranslator<M> extends EventTranslator<Holder<M>> {
    void setMessage(M message);
    M getMessage();

    @Override
    void translateTo(Holder<M> event, long sequence);
}
