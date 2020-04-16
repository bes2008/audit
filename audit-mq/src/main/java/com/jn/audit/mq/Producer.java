package com.jn.audit.mq;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;

public interface Producer<M> extends MessageTopicDispatcherAware {
    void publish(@NonNull M message);

    void publish(@Nullable String topicName, @NonNull M message);

    void setTopicAllocator(TopicAllocator<M> allocator);


}
