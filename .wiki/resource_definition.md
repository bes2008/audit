# Resource Definition

使用过程中， 最复杂的要属于 Resource这块的配置了。

ResourceDefinition的构建，可以基于下列方式：
```text
+ 基于注解方式
+ 基于配置文件方式
+ 基于自定义方式
```

Resource的结构
```text
+ resourceId
+ resourceName
+ resourceType
+ ...
```


其实不论是注解方式，还是配置文件方式，最核心的就是配置 resourceId {Required}, resourceName {Required}, resourceType {Optional}

## 注解方式

注解方面，提供了大量的注解：@Resource, @ResourceMapping, @ResourceName, @ResourceId, @ResourceProperty @ResourceType

### 方法一： @ResourceId, @ResourceName, @ResourceType, @ResourceProperty

顾名思义，这几个注解，就是 用于配置 哪个参数、字段可用做 resourceId, resourceName, resourceType, 以及自定义属性的。

@ResourceId, @ResourceName, @ResourceType, @ResourceProperty 可以用在 方法的参数上，也可以用在 Java Bean的 field, 或者 方法上（这里不限定必须是getXxx方法，但是该方法必须是无参的）。


### 方法二： @ResourceMapping
除了上述方式外，也可以在 java bean 的类上直接使用@ResourceMapping注解

### 方法三： @Resource
如果方法的参数，本身就是个Java Bean，也可以在方法的参数上直接使用 @Resource + @ResourceMapping + @ResourceProperty 配合使用。

### 方法四： 简化方式
Java Bean 里的字段名，本身是 id, name，那么上述方法二、三种中，就可以直接使用注解，注解内不需要任何的其他的配置

### 方法五：yaml文件
operation.yml 文件中的 每个操作的 resourceDefinition定义区域内，可以直接使用 resourceId, resourceName, resourceType, resource 这四个关键字来替代 @ResourceId,@ResourceName,@ResourceType

### 方法六：EntityLoader 
前面5种方案，针对的是直接能够通过方法的参数，提取到resource对象里必要的属性时。如果通过方法的参数里只能获取到resourceId怎么办?

为了应对这种场景，提供了 ```com.jn.audit.core.resource.idresource.EntityLoader``` 接口。目前也提供了基于 MyBatis，ElasticSearch, Spring RestTemplate 等3种方式。
如需要其他的EntityLoader，可以自行开发。
