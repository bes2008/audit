# FAQ

### 内置哪些OperationDefinition ID方式
+ 基于方法名的```${classFullName}.${methodName}```
+ 对于HTTP 请求处理时，可以采用 ```${HTTP METHOD}-${URL template}```，基于此方式，支持原生的Servlet， SpringWeb 相关的注解 （@RequestMapping, @GetMapping, @PostMapping, @PutMapping ...）

### 想要自定义 OperationDefinition ID怎么办？
只需要实现 ```com.jn.audit.core.operation.OperationIdGenerator``` 接口即可


### 对于Swagger Operation的支持情况
+ 目前已支持基于Swagger的 @ApiOperation 进行解析。具体解析情况，请参见 audit-swagger模块

### 基于注解方式，和基于配置文件方式，如何选择？
+ 基于注解方式，通常用于开发阶段直接配置。如果有专门的时间安排，也可以在项目开展到某一个阶段时加入。
+ 基于配置文件方式，优点是灵活，易修改，代码侵入性小。可以在项目开展到一定阶段后加入。
+ 两者互不影响，如果都配置了，优先注解方式。

### 


