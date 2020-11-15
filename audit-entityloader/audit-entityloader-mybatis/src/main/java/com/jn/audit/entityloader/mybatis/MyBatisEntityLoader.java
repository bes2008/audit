package com.jn.audit.entityloader.mybatis;

import com.jn.audit.core.model.ResourceDefinition;
import com.jn.audit.core.resource.idresource.EntityLoader;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.MapAccessor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

public class MyBatisEntityLoader implements EntityLoader<Object> {
    private static final Logger logger = LoggerFactory.getLogger(MyBatisEntityLoader.class);
    private static final String STATEMENT_ID_FQN = "statementIdFQN";
    private String name = "mybatis";
    private SqlSessionFactory sessionFactory;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (Emptys.isNotEmpty(name)) {
            this.name = name;
        }
    }

    @Override
    public List load(ResourceDefinition resourceDefinition, List<Serializable> ids) {
        if (Emptys.isEmpty(ids)) {
            return null;
        }
        MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        String statementIdFQN = mapAccessor.getString(STATEMENT_ID_FQN);
        Preconditions.checkNotEmpty(statementIdFQN, "the {} is undefined in the resource definition", STATEMENT_ID_FQN);
        if (ids.size() == 1) {
            SqlSession session = sessionFactory.openSession();
            try {
                Object object = session.selectOne(statementIdFQN, ids.get(0));
                return Collects.newArrayList(object);
            } finally {
                session.close();
            }
        } else {
            SqlSession session = sessionFactory.openSession();
            try {
                return session.selectList(statementIdFQN, ids);
            } finally {
                session.close();
            }
        }
    }

    public SqlSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
