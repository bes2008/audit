package com.jn.audit.examples.springmvcdemo.common.config;

import com.jn.audit.entityloader.mybatis.MybatisEntityLoader;
import com.jn.langx.util.concurrent.CommonThreadFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class MyBatisEntityLoaderConfig {
    @Bean("auditEntityLoaderExecutor")
    public ExecutorService auditEntityLoaderExecutor() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new CommonThreadFactory("Audit-Entity-Loader", true));
    }

    @Bean
    @Autowired
    public MybatisEntityLoader mybatisEntityLoader(SqlSessionFactory sessionFactory, @Qualifier("auditEntityLoaderExecutor") ExecutorService executor) {
        MybatisEntityLoader loader = new MybatisEntityLoader();
        loader.setSessionFactory(sessionFactory);
        loader.setExecutor(executor);
        return loader;
    }

}
