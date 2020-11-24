# OperationDefinition

## 结构

```text
* id 操作ID  {String}， 内部进行业务方法和OperationDefinition关联的纽带
* code 操作码  {String} 如不定义，默认和id一样
* type 操作类型 {String} 如需要，自定义即可 
* module 模块 {String} 如需要，自定义即可
* description 描述 {String} 自定义即可
* importance 重要性 key-value 对，完全支持自定义
* resourceDefinition 资源定义，请参考资源定义文档的说明
```

框架内部会让OperationDefinition与业务调用的方法进行关联。

id目前支持的配置方式：```${classFullName}.${methodName}```, ```${HTTP METHOD}-${URL template}```
。当然了，也可以自定义的。只需要实现 ```com.jn.audit.core.operation.OperationIdGenerator``` 接口即可。



