package com.jn.audit.entityloader.mybatis.spring;

import com.jn.audit.core.model.ResourceDefinition;
import com.jn.sqlhelper.datasource.key.DataSourceKeySelector;
import com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource.DynamicDataSourceSqlSessionFactoryProvider;
import com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource.DynamicSqlSessionFactory;

public class DynamicDataSourceFactoryProvider extends DynamicDataSourceSqlSessionFactoryProvider<ResourceDefinition> {

    public DynamicDataSourceFactoryProvider(DataSourceKeySelector<ResourceDefinition> selector, DynamicSqlSessionFactory dynamicSqlSessionFactory) {
        setSelector(selector);
        setDynamicSqlSessionFactory(dynamicSqlSessionFactory);
    }
}
