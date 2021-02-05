package com.jn.audit.examples.springmvcdemo.common.config;

import com.jn.agileway.dmmq.core.MessageTopicDispatcher;
import com.jn.audit.core.Auditor;
import com.jn.audit.examples.springmvcdemo.service.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfig {

    /**
     * 这是一个自定义的写入数据库的 Consumer
     */
    @Autowired
    public void setDbService(Auditor auditor, DbService dbService) {
        MessageTopicDispatcher dispatcher = auditor.getMessageTopicDispatcher();
        dispatcher.subscribe("DEFAULT", dbService);
    }


}
