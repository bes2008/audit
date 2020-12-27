package com.jn.audit.entityloader.mybatis;

import com.jn.audit.core.model.ResourceDefinition;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.Serializable;
import java.util.List;

public interface SqlSessionFactoryProvider {
    SqlSessionFactory get(ResourceDefinition resourceDefinition, List<Serializable> partitionIds);
}
