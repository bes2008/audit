package com.jn.audit.core.model;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Objects;
import com.jn.langx.util.hash.HashCodeBuilder;

import java.util.HashMap;

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
public class ResourceDefinition extends HashMap<String, String> {
    public static final long serialVersionUID = 1L;


    @Nullable
    String resource;

    /**
     * 如果一个方法的参数实际上可以去代表一个或多个被操作的资源的ID， 可以使用它来标注该参数名称。
     * <p>
     * 也可以这么理解：一个方法的参数名称，如果与 resourceId的值一样，那么该参数就会被认为是 resource的ID
     * <p>
     * 也就是说，它等同于 @ResourceId 注解
     */
    @Nullable
    String resourceId;

    /**
     * 如果一个方法的参数实际上可以去代表一个或多个被操作的资源的name， 可以使用它来标注该参数名称。
     * <p>
     * 也可以这么理解：一个方法的参数名称，如果与 resourceName的值一样，那么该参数就会被认为是 resource的Name
     * <p>
     * 也就是说，它等同于 @ResourceName 注解
     */
    @Nullable
    String resourceName;

    /**
     * 如果一个方法的参数实际上可以去代表一个或多个被操作的资源的type， 可以使用它来标注该参数名称。
     * <p>
     * 也可以这么理解：一个方法的参数名称，如果与 resourceType的值一样，那么该参数就会被认为是 resource的Type
     * <p>
     * 也就是说，它等同于 @ResourceType 注解
     */
    @Nullable
    String resourceType;

    public static final ResourceDefinition DEFAULT_DEFINITION = getDefaultResourceDefinition();

    private static final ResourceDefinition getDefaultResourceDefinition() {
        ResourceDefinition definition = new ResourceDefinition();
        definition.setResourceId("resourceId");
        definition.setResourceName("resourceName");
        definition.setResourceType("resourceType");
        definition.setResource("resource");
        return definition;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceDefinition that = (ResourceDefinition) o;

        if (!Objects.equals(this.resource, that.resource)) {
            return false;
        }
        if (!Objects.equals(this.resourceId, that.resourceId)) {
            return false;
        }
        if (!Objects.equals(this.resourceName, that.resourceName)) {
            return false;
        }
        if (!Objects.equals(this.resourceType, that.resourceType)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .with(resource)
                .with(resourceId)
                .with(resourceName)
                .with(resourceType)
                .build();
    }

    @Override
    public String toString() {
        return "ResourceDefinition{" +
                "resource='" + resource + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", resourceType='" + resourceType + '\'' +
                '}';
    }
}
