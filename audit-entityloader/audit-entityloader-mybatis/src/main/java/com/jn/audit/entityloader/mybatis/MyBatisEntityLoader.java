package com.jn.audit.entityloader.mybatis;

import com.jn.audit.core.model.ResourceDefinition;
import com.jn.audit.core.resource.idresource.EntityLoader;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.MapAccessor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.Serializable;
import java.util.List;

public class MyBatisEntityLoader implements EntityLoader<Object> {

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
        String statementIdFQN = mapAccessor.getString("statementIdFQN");
        Preconditions.checkNotNull(statementIdFQN, "the mybatis statement id is null");
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
