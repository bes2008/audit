package com.jn.audit.examples.springmvcdemo.common.config;

import com.jn.audit.entityloader.mybatis.MybatisEntityLoader;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisEntityLoaderConfig {

    @Bean
    @Autowired
    public MybatisEntityLoader mybatisEntityLoader(SqlSessionFactory sessionFactory) {
        MybatisEntityLoader loader = new MybatisEntityLoader();
        loader.setSessionFactory(sessionFactory);
        return loader;
    }

}
