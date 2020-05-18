package com.jn.audit.core.model;

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.util.collection.MapAccessor;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于在配置文件中定义资源的标识：
 * 如果一个方法的参数实际就是一个或多个资源，那么可以使用它来标注该参数名称。
 * <p>
 * 也可以这么理解：一个方法的参数名称，如果与 resource的值一样，那么该参数就会被认为是 resource
 * 此时会根据该参数的类型进行区别处理：
 * <pre>
 *     1. 如果是 Map,那么 resourceId, resourceName, resourceType 的值对应的是 map中的key
 *     2. 如果是 Entity，那么  resourceId, resourceName, resourceType 的值对应的是 Entity的字段名称
 *     3. 如果是 List或者数组等，那么resourceId, resourceName, resourceType 的值对应的是 集合的索引
 * </pre>
 * <p>
 * 该值指定的数据的类，需要有 @ResourceMapping注解，或者需要 resourceId, resourceName, resourceType 配置
 */
public class ResourceDefinition extends HashMap<String, Object> {
    public static final long serialVersionUID = 1L;


    public static final ResourceDefinition DEFAULT_DEFINITION = getDefaultResourceDefinition();

    private static final ResourceDefinition getDefaultResourceDefinition() {
        ResourceDefinition definition = new ResourceDefinition();
        definition.setResourceId("id");
        definition.setResourceName("name");
        definition.setResourceType("type");
        definition.setResource("resource");
        return definition;
    }

    public ResourceDefinition() {

    }

    public ResourceDefinition(Map<String, Object> map) {
        this.putAll(map);
        MapAccessor accessor = new MapAccessor(map);
        setResourceId(accessor.getString(Resource.RESOURCE_ID, Resource.RESOURCE_ID_DEFAULT_KEY));
        setResourceName(accessor.getString(Resource.RESOURCE_NAME, Resource.RESOURCE_NAME_DEFAULT_KEY));
        setResourceType(accessor.getString(Resource.RESOURCE_TYPE, Resource.RESOURCE_TYPE_DEFAULT_KEY));
        setResource(accessor.getString("resource"));
    }

    public String getResource() {
        return (String) this.get("resource");
    }

    public void setResource(String resource) {
        this.put("resource", resource);
    }

    public String getResourceId() {
        return (String) this.get(Resource.RESOURCE_ID);
    }

    public void setResourceId(String resourceId) {
        this.put(Resource.RESOURCE_ID, resourceId);
    }

    public String getResourceName() {
        return (String) this.get(Resource.RESOURCE_NAME);
    }

    public void setResourceName(String resourceName) {
        this.put(Resource.RESOURCE_NAME, resourceName);
    }

    public String getResourceType() {
        return (String) this.get(Resource.RESOURCE_TYPE);
    }

    public void setResourceType(String resourceType) {
        this.put(Resource.RESOURCE_TYPE, resourceType);
    }

    @Override
    public String toString() {
        return JSONBuilderProvider.simplest().toJson(this);
    }
}
