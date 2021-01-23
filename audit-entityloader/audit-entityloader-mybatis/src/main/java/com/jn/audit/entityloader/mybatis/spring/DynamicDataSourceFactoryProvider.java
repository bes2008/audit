package com.jn.audit.entityloader.mybatis.spring;

import com.jn.audit.core.AuditRequest;
import com.jn.sqlhelper.datasource.key.DataSourceKeySelector;
import com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource.DynamicDataSourceSqlSessionFactoryProvider;
import com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource.DynamicSqlSessionFactory;

public class DynamicDataSourceFactoryProvider extends DynamicDataSourceSqlSessionFactoryProvider<AuditRequest> {
    public DynamicDataSourceFactoryProvider(DynamicSqlSessionFactory dynamicSqlSessionFactory) {
        this(new AuditRequestDataSourceKeySelector(), dynamicSqlSessionFactory);
    }

    public DynamicDataSourceFactoryProvider(DataSourceKeySelector<AuditRequest> selector, DynamicSqlSessionFactory dynamicSqlSessionFactory) {
        setSelector(selector);
        setDynamicSqlSessionFactory(dynamicSqlSessionFactory);
    }
}
