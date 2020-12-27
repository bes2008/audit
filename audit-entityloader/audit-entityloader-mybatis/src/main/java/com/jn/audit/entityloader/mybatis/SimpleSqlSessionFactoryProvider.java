package com.jn.audit.entityloader.mybatis;

import com.jn.audit.core.model.ResourceDefinition;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.Serializable;
import java.util.List;

public class SimpleSqlSessionFactoryProvider implements SqlSessionFactoryProvider {
    private SqlSessionFactory sessionFactory;

    public SimpleSqlSessionFactoryProvider(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public SqlSessionFactory get(ResourceDefinition resourceDefinition, List<Serializable> partitionIds) {
        return sessionFactory;
    }
}
