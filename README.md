# audit
一个通用的 Audit （审计）框架。如果要接入4A审计时，特别方便哟，同时也高度定制化。

[![maven](https://img.shields.io/badge/maven-v1.0.1-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.audit%20AND%20v:1.0.1)

教程 : https://fangjinuo.gitee.io/docs/index.html

## Example
给一个SpringBoot应用快速加上审计功能，也可以直接去参考 audit-examples-springmvcdemo 

第一步：引入相关Jar包
```xml
    <dependency>
        <groupId>com.github.fangjinuo.audit</groupId>
        <artifactId>audit-spring-boot-starter</artifactId>
        <version>${audit.version}</version>
    </dependency>
```
第二步：在application.yml中配置审计功能
```yaml

audit:
  enabled: true     # 开关
  async-mode: false # 异步模式执行，还是同步模式执行，对于 web应用目前暂时强制采用同步模式
  topics: [DEFAULT, LOGIN_LOGOUT]
  topic-configs:
    - name: DEFAULT
      ring-buffer-size: 1024
      producer-type: MULTI
    - name: LOGIN_LOGOUT      # topic的name
      ring-buffer-size: 512   # topic的 ring buffer size ,强制要求是 pow(2)
      producer-type: SINGLE   # 生产者是单线程，还是多线程，可选值是 SINGLE,MULTI
  http-interceptor-patterns:  # Spring MVC HandlerInterceptor的拦截 pathPatterns
    - /consumers/**
    - /users/**


```

第三步：设置操作定义
参考 audit-examples-springmvcdemo 下的 operation.yml文件

第四步：在application.yml中配置定义文件位置
```yml
operation:
  definition:                           # 操作定义
    location: classpath:/operation.yml  # 目前只内置了 yml风格的配置文件
    reload-interval-in-seconds: 60      # 如果值 >0 则会定时的重新加载，在开发环境下有很有用
```

只需上述4步，然后访问应用就会有相应的日志产生。
如果想把审计日志写入数据库，或者需要自定义审计日志消费者，只需要实现 com.jn.audit.mq.Consumer接口并订阅响应的topic即可。
可以参考：audit-examples/audit-examples-springmvcdemo 中的AuditConfig.java
```java

import com.jn.audit.core.Auditor;
import com.jn.audit.examples.springmvcdemo.service.DbService;
import com.jn.audit.mq.MessageTopicDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfig {

    private DbService dbService;

    public DbService getDbService() {
        return dbService;
    }

    /**
     * 这是一个自定义的写入数据库的 Consumer
     */
    @Autowired
    public void setDbService(Auditor auditor, DbService dbService) {
        this.dbService = dbService;
        MessageTopicDispatcher dispatcher = auditor.getMessageTopicDispatcher();
        dispatcher.subscribe("DEFAULT", dbService);
    }

}

```




##  [推广](https://github.com/fangjinuo)
+ langx 系列
    - [langx-js](https://github.com/fangjinuo/langx-js)：TypeScript, JavaScript tools
    - [langx-java](https://github.com/fangjinuo/langx-java): Java tools ，可以替换guava, apache commons-lang,io, hu-tool等
+ [easyjson](https://github.com/fangjinuo/easyjson): 一个通用的JSON库门面，可以无缝的在各个JSON库之间切换，就像slf4j那样。
+ [sqlhelper](https://github.com/fangjinuo/sqlhelper): SQL工具套件（通用分页、DDL Dump、SQLParser、URL Parser、批量操作工具等）。
+ [esmvc](https://github.com/fangjinuo/es-mvc): ElasticSearch 通用客户端，就像MyBatis Mapper那样顺滑
+ [redisclient](https://github.com/fangjinuo/redisclient): 基于Spring RestTemplate提供的客户端
+ [audit](https://github.com/fangjinuo/audit)：通用的Java应用审计框架

## 鸣谢
最后，感谢 Jetbrains 提供免费License，方便了开源项目的发展。

[![Jetbrains](https://github.com/fangjinuo/sqlhelper/blob/master/_images/jetbrains.png)](https://www.jetbrains.com/zh-cn/)

