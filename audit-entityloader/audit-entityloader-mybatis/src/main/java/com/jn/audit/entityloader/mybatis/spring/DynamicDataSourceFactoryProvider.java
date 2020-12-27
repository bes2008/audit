package com.jn.audit.entityloader.mybatis.spring;

import com.jn.audit.core.model.ResourceDefinition;
import com.jn.audit.entityloader.mybatis.SqlSessionFactoryProvider;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.DataSourceKeySelector;
import com.jn.sqlhelper.mybatis.spring.datasource.DynamicSqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DynamicDataSourceFactoryProvider implements SqlSessionFactoryProvider {
    private DynamicSqlSessionFactory dynamicDataSourceFactory;

    public DynamicDataSourceFactoryProvider(DynamicSqlSessionFactory dynamicDataSourceFactory) {
        this.dynamicDataSourceFactory = dynamicDataSourceFactory;
    }

    @Override
    public SqlSessionFactory get(ResourceDefinition resourceDefinition, List<Serializable> partitionIds) {
        return getDelegatingSqlSessionFactory();
    }

    private SqlSessionFactory getDelegatingSqlSessionFactory() {
        if (DataSourceKeySelector.getCurrent() != null) {
            Map<DataSourceKey, SqlSessionFactory> factoryMap = dynamicDataSourceFactory.getDelegates();
            return factoryMap.get(DataSourceKeySelector.getCurrent());
        }
        return null;
    }
}
