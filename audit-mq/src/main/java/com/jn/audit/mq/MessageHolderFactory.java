package com.jn.audit.mq;

import com.jn.langx.util.struct.Holder;
import com.lmax.disruptor.EventFactory;

public class MessageHolderFactory<M> implements EventFactory<Holder<M>> {
    @Override
    public Holder<M> newInstance() {
        return new Holder<M>();
    }
}
