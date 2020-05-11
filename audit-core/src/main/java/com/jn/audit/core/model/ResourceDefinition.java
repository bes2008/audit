package com.jn.audit.core.model;

import com.jn.langx.annotation.Nullable;

import java.io.Serializable;

/**
 * 用于在配置文件中定义资源的标识：
 */
public class ResourceDefinition implements Serializable {
    public static final long serialVersionUID = 1L;

    /**
     * 如果一个方法的参数实际就是一个或多个资源，那么可以使用它来标注该参数名称。
     * <p>
     * 也可以这么理解：一个方法的参数名称，如果与 resourceId的值一样，那么该参数就会被认为是 resource的ID
     * <p>
     * 该值指定的数据的类，需要有 @ResourceMapping注解，或者需要 resourceId, resourceName, resourceType 配置
     */
    @Nullable
    String resource;

    /**
     * 如果一个方法的参数，实际上可以去代表一个或多个被操作的资源的ID， 可以使用它来标注该参数名称。
     * <p>
     * 也可以这么理解：一个方法的参数名称，如果与 resourceId的值一样，那么该参数就会被认为是 resource的ID
     * <p>
     * 也就是说，它等同于 @ResourceId 注解
     */
    @Nullable
    String resourceId;

    /**
     * 如果一个方法的参数，实际上可以去代表一个或多个被操作的资源的ID， 可以使用它来标注该参数名称。
     * <p>
     * 也可以这么理解：一个方法的参数名称，如果与 resourceName的值一样，那么该参数就会被认为是 resource的Name
     * <p>
     * 也就是说，它等同于 @ResourceName 注解
     */
    @Nullable
    String resourceName;

    /**
     * 如果一个方法的参数，实际上可以去代表一个或多个被操作的资源的ID， 可以使用它来标注该参数名称。
     * <p>
     * 也可以这么理解：一个方法的参数名称，如果与 resourceType的值一样，那么该参数就会被认为是 resource的Type
     * <p>
     * 也就是说，它等同于 @ResourceType 注解
     */
    @Nullable
    String resourceType;

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
}
