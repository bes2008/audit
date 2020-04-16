package com.jn.audit.mq;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;

public interface Producer<M> extends TopicAllocator<M> {
    void publish(@Nullable String topicName, @NonNull M message);
}
