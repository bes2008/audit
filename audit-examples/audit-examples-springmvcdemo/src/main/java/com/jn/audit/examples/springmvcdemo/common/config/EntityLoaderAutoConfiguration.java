package com.jn.audit.examples.springmvcdemo.common.config;

import com.jn.audit.entityloader.mybatis.MybatisEntityLoader;
import com.jn.audit.entityloader.mybatis.spring.dynamicdatasource.DynamicSqlSessionFactoryProvider;
import com.jn.langx.util.concurrent.CommonThreadFactory;
import com.jn.sqlhelper.datasource.key.MethodInvocationDataSourceKeySelector;
import com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource.DynamicSqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class EntityLoaderAutoConfiguration {
    @Bean("auditEntityLoaderExecutor")
    public ExecutorService auditEntityLoaderExecutor() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new CommonThreadFactory("Audit-Entity-Loader", true));
    }

    @Bean
    @Autowired
    public MybatisEntityLoader mybatisEntityLoader(ObjectProvider<MethodInvocationDataSourceKeySelector> keySelectorProvider, SqlSessionFactory sessionFactory, @Qualifier("auditEntityLoaderExecutor") ExecutorService executor) {
        MybatisEntityLoader loader = new MybatisEntityLoader();
        loader.setExecutor(executor);

        if (sessionFactory instanceof DynamicSqlSessionFactory) {
            MethodInvocationDataSourceKeySelector selector = keySelectorProvider.getIfAvailable();
            loader.setSessionFactoryProvider(new DynamicSqlSessionFactoryProvider(selector, (DynamicSqlSessionFactory) sessionFactory));
        } else {
            loader.setSessionFactory(sessionFactory);
        }
        return loader;
    }


}
