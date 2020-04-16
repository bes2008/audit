package com.jn.audit.mq;

import com.jn.langx.util.function.Function;

public interface TopicAllocator<M> extends Function<M, String> {

}
