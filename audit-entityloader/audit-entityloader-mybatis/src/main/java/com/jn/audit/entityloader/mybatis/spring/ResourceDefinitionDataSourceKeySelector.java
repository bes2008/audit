package com.jn.audit.entityloader.mybatis.spring;

import com.jn.audit.core.model.ResourceDefinition;
import com.jn.langx.util.Emptys;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.DataSourceKeySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceDefinitionDataSourceKeySelector implements DataSourceKeySelector<ResourceDefinition> {
    private static final Logger logger = LoggerFactory.getLogger(ResourceDefinitionDataSourceKeySelector.class);

    @Override
    public DataSourceKey select(ResourceDefinition resourceDefinition) {
        String id = resourceDefinition.getDefinitionAccessor().getString("datasource");
        DataSourceKey key = null;
        if (Emptys.isNotEmpty(id)) {
            try {
                key = DataSources.buildDataSourceKey(id);
            } catch (Throwable ex) {
                logger.error("error occur when parse datasource key string : {}", id);
            }
        }
        return key;
    }
}
