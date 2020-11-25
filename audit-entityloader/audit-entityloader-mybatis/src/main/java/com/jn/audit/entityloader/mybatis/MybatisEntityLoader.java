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

public class MybatisEntityLoader implements EntityLoader<Object> {
    private static final Logger logger = LoggerFactory.getLogger(MybatisEntityLoader.class);
    private static final String STATEMENT_ID = "statementId";
    private static final String SELECT_TYPE = "selectType";
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
        String statementId = mapAccessor.getString(STATEMENT_ID);
        String selectType = mapAccessor.getString(SELECT_TYPE, "selectList");
        Preconditions.checkNotEmpty(statementId, "the {} is undefined in the resource definition", STATEMENT_ID);
        if ("selectOne".equals(selectType)) {
            SqlSession session = sessionFactory.openSession();
            try {
                Object object = session.selectOne(statementId, ids.get(0));
                return Collects.newArrayList(object);
            } finally {
                session.close();
            }
        } else if ("selectList".equals(selectType)) {
            SqlSession session = sessionFactory.openSession();
            try {
                return session.selectList(statementId, ids);
            } finally {
                session.close();
            }
        }
        return null;
    }

    public SqlSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
