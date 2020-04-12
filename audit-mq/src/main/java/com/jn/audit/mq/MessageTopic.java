package com.jn.audit.mq;

import com.jn.langx.lifecycle.Destroyable;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;

public class MessageTopic implements Destroyable, Initializable {
    private String name;

    public String getName() {
        return this.name;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init() throws InitializationException {

    }
}
