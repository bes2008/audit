# FAQ

### 内置哪些OperationDefinition ID方式
+ ```${classFullName}.${methodName}```
+ 对于HTTP 请求处理时，可以采用 ```${HTTP METHOD}-${URL template}```，基于此方式，支持原生的Servlet， SpringWeb 相关的注解 （@RequestMapping, @GetMapping, @PostMapping, @PutMapping ...）

### 想要自定义 OperationDefinition ID怎么办？
只需要实现 ```com.jn.audit.core.operation.OperationIdGenerator``` 接口即可