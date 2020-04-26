package com.jn.audit.examples.springmvcdemo.common.config;

import com.jn.audit.core.Auditor;
import com.jn.audit.examples.springmvcdemo.service.DbService;
import com.jn.audit.mq.MessageTopicDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfig {

    private DbService dbService;

    public DbService getDbService() {
        return dbService;
    }

    /**
     * 这是一个自定义的写入数据库的 Consumer
     */
    @Autowired
    public void setDbService(Auditor auditor, MessageTopicDispatcher dispatcher, DbService dbService) {
        this.dbService = dbService;
        auditor.getMessageTopicDispatcher().subscribe("DEFAULT", dbService);
    }

}
