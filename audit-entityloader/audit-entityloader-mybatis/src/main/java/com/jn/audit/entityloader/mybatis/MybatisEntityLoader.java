package com.jn.audit.entityloader.mybatis;

import com.jn.audit.core.model.ResourceDefinition;
import com.jn.audit.core.resource.idresource.EntityLoader;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
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
    private static final String SELECT_LIST_SEPARATOR = "separator";
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
    public List load(ResourceDefinition resourceDefinition, final List ids) {
        if (Emptys.isEmpty(ids)) {
            return null;
        }
        MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        final String statementId = mapAccessor.getString(STATEMENT_ID);
        String selectType = mapAccessor.getString(SELECT_TYPE, "selectList");
        String selectListIdSeparator = mapAccessor.getString(SELECT_LIST_SEPARATOR, "");
        final int batchSize = mapAccessor.getInteger("batchSize", 100);
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
            final SqlSession session = sessionFactory.openSession();
            try {
                if (Emptys.getLength(ids) == 1) {
                    Serializable id = (Serializable) ids.get(0);
                    if (id instanceof String && Emptys.isNotEmpty(selectListIdSeparator)) {
                        String idListString = (String) id;
                        if (Emptys.isNotEmpty(idListString)) {
                            String[] idStrings = Strings.split(idListString, selectListIdSeparator);
                            if (Emptys.isNotEmpty(idStrings)) {
                                ids.clear();
                                Collects.addAll(ids, idStrings);
                            }
                        }
                    }
                }
                if (Emptys.isNotEmpty(ids)) {
                    final List entities = Collects.emptyArrayList();
                    Collects.forEach(Collects.asList(Arrs.range(0, ids.size(), batchSize)), new Consumer<Integer>() {
                        @Override
                        public void accept(Integer offset) {
                            List<Serializable> partitionIds = Pipeline.of(ids).skip(offset).limit(batchSize).asList();
                            List partition = session.selectList(statementId, partitionIds);
                            entities.addAll(partition);
                        }
                    });
                    return entities;
                }
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
