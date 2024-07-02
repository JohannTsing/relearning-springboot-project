### Spring Boot Actuator简介
Spring Boot Actuator是 Spring Boot 的重要功能模块，能为系统提供一系列在生产环境中运行所必需的功能，比如监控、度量、配置管理等。
只需引人`org.springframework.boot:spring-bootstarter-actuator` 起步依赖后，我们就可以通过 HTTP 来访问这些功能(也可以使用JMX来访回)。
Spring Boot 还为我们预留了很多配置，可以根据自己的需求对 Spring Boot Actuator 的功能进行定制。

### Spring Boot Actuator的端点信息
根据功能的不同，我们可以将这些端点划分成四类:信息类端点、监控类端点、操作类端点、集成类端点，其中部分端点需要引人特定的依赖，或者配置特定的 Bean。

#### Actuator信息类端点
Spring Boot Actuator 中的信息类端点用于获取系统运行信息，端点列表如下:

| 端点ID | 是否默认开启HTTP | 是否默认开启JMX | 端点说明 |
|-|-|-|-|
| info | 是 | 是 | 显示事先设置好的系统信息 |
| auditevents | 否 | 是 | 提供系统的审计信息 |
| beans | 否 | 是 | 提供系统中的 Bean 列表 |
| caches | 否 | 是 | 提供系统中的缓存信息 |
| conditions | 否 | 是 | 提供配置类的匹配情况及条件运算结果 |
| configprops | 否 | 是 | 提供 @ConfigurationProperties 的列表 |
| env | 否 | 是 | 提供 ConfigurableEnvironment 中的属性信息 |
| logfile | 否 | 无此功能 | 如果设置了 logging.file.name 或 logging.file.path属性，则显示日志文件内容 |
| mappings | 否 | 是 | 提供 @RequestMapping 的映射列表 |
| scheduledtasks | 否 | 是 | 提供系统中的调度任务列表 |
| httptrace | 否 | 是 | 提供 HTTP 跟踪信息，默认最近 100条 |
| flyway | 否 | 是 | 提供已执行的 Flyway 数据库迁移信息 |
| integrationgraph | 否 | 是 | 提供 Spring Integration 图信息 |
| liquibase | 否 | 是 | 提供已执行的 Liquibase 数据库迁移信息 |

#### Actuator监控类端点
Spring Boot Actuator 中的监控类端点用于监控与度量相关，端点列表如下:

| 端点ID | 是否默认开启HTTP | 是否默认开启JMX | 端点说明 |
|-|-|-|-|
| health | 是 | 是 | 提供系统运行的健康状态 |
| metrics | 否 | 是 | 提供系统的度量信息 |
| prometheus | 否 | 无此功能 | 提供 Prometheus 系统可解析的度量信息 |

#### Actuator操作类端点
Spring Boot Actuator 中的操作类端点可以执行一些实际的操作，例如调整日志级别，端点列表如下:

| 端点ID | 是否默认开启HTTP | 是否默认开启JMX | 端点说明 |
|-|-|-|-|
| heapdump | 否 | 无此功能 | 执行 Heap Dump 操作 |
| loggers | 否 | 是 | 查看并修改日志信息 |
| sessions | 否 | 是 | 针对使用了 Spring Session 的系统，可获取或删除用户的 Session |
| shutdown | 否 | 否 | 优雅地关闭系统 |
| threaddump | 否 | 是 | 执行 Thread Dump 操作 |

#### Actuator集成类端点
最后一类端点比较特殊，它的功能与集成有关，就只有一个 jolokia 端点，用于将 JMX 暴露为 REST 接口。

| 端点ID | 是否默认开启HTTP | 是否默认开启JMX | 端点说明 |
|-|-|-|-|
| jolokia | 否 | 无此功能 | 通过HTTP 来发布JMX Bean |

### 访问Actuator端点
Spring Boot Actuator 的端点默认是开启的，但是并不是所有的端点都能通过 HTTP 来访问，也不是所有的端点都能通过 JMX 来访问。
我们可以通过配置来开启或关闭端点的访问，也可以通过配置来修改端点的访问路径。
```properties
# 开启、禁用端点
management.endpoint.shutdown.enabled=true
# 禁用所有端点，随后开启health端点
# management.endpoints.enabled-by-default=false
# management.endpoint.health.enabled=true

# 通过HTTP访问端点
# 控制哪些端点可以通过http方式发布，哪些不能（如果同一个端点同时出现在include和exclude中，后者的优先级更高，即端点会被排除）
management.endpoints.web.exposure.include=*
# management.endpoints.web.exposure.include=health,info,env,beans,mappings,metrics
# management.endpoints.web.exposure.exclude=shutdown,env

# 针对 Web 和 Actuator 使用不同端口。http://localhost:8081/manage/my-actuator/
# 设置Actuator专属端口
# management.server.port=8081
# 为 Spring Boot Actuator 设置 Servlet 上下文
# management.server.base-path=/manage
# 默认基础路径
# management.endpoints.web.base-path=/my-actuator

# 定制端点
management.endpoint.env.keys-to-sanitize=java.*,sun.*
```

### 定制端点信息
Spring BootActuaor 中的每个端点或多或少会有一些属于自己的配置属性，大家可以在`org.springframework,boot:spring-boot-actuator-autoconfigure` 包中查看各种以 Properties 结尾的属性类。
也可以直接通过 configprops 端点来查看属性类。

例如，`EnvironmentEndpointProperties` 就对应了`management.endpoint.env` 中的属性，其中的keysToSanitize 就是环境中要过滤的自定义敏感信息键名清单。
根据代码注释，其中可以设置匹配的结尾字符串，也可以使用正则表达式。在设置了`management.endpoint.env.keys-to-sanitize=java.*,sun.*`后，env 端点返回的属性中，所有 java 和 sun 打头的属性值都会以* 显示。

#### 定制info端点
根据 `InfoEndpointAutoConfiguration` 可以得知，`InfoEndpoint` 中会注入 Spring 上下文中的所有 `InfoContributor` Bean实例。`InfoContributorAutoConfiguration` 自动注册了env、git、build这几个`InfoContributor`。

Spring Boot Actuator 提供的 `InfoContributor` 列表如表：

| 类名 | 默认开启 | 说明 |
|-|-|-|
| BuildInfoContributor | 是 | 提供BuildProperties 的信息，通过 spring.info.build来设置，默认读取META-INF/build-info.properties |
| EnvironmentInfoContributor | 是 | 将配置中以 info打头的属性通过端点暴露 |
| GitInfoContributor | 是 | 提供 GitProperties 的信息，通过 spring.info.git 来设置，默认读取 git.properties |
| InfoPropertiesInfoContributor | 否 | 抽象类，一般作为其他 InfoContributor 的父类 |
| MapInfoContributor | 否 | 将内置 Map 作为信息输出 |
| SimpleInfoContributor | 否 | 仅包含一对键值对的信息 |

info 端点的信息是通过 `InfoContributor` 来提供的，我们可以通过实现 InfoContributor 接口来定制 info 端点的信息。
```text
@Configuration
public class ActuatorInfoConfig {

    @Bean
    public SimpleInfoContributor simpleInfoContributor() {
        return new SimpleInfoContributor("simple","HelloWorld!");
    }
}
```

#### 定制health端点
健康检查是一个很常用的功能，可以帮助我们了解系统的健康状况，例如，系统在启动后是否准备好对外提供服务了，所依赖的组件是否已就绪等。

健康检查主要是依赖 `HealthIndicator` 的各种实现来完成的。Spring Boot Actuator 内置了近20种不同的实现，其中大部分都是针对系统的各种组件的健康检查，例如，数据库、缓存、消息队列等。

以下是常见的一些`HealthIndicator`实现：

| 实现类 | 说明 |
|-|-|
| DataSourceHealthIndicator | 检查 Spring 上下文中能取到的所有 DataSource 是否健康 |
| DiskSpaceHealthIndicator | 检查磁盘空间 |
| LivenessStateHealthIndicator | 检查系统的存活 (Liveness)情况，一般用于 Kubernetes 中 |
| ReadinessStateHealthIndicator | 检查系统是否处于就绪 (Readiness) 状态，一般用于 Kubernetes 中 |
| RedisHealthIndicator | 检查所依赖的 Redis 健康情况 |

每个 HealthIndicator 检查后都会有自己的状态信息，Spring Boot Actuator 最后会根据所有结果的状态信息综合得出系统的最终状态。
`org.springframework.boot.actuate.health.Status` 定义了几种默认状态，按照优先级降序排列分别为 DOWN、OUT OF SERVICE、UP 和UNKNOWN，所有结果状态中优先级最高的状态会成为 health 端点的最终状态。
如果有需要，我们也可以通过 `management.endpoint.health.status.order` 来更改状态的优先级。

Spring Boot Actuator 默认开启了所有的 HealthIndicator，它们会根据情况自行判断是否生效，也可以通过 `management.health.defaults.enabled=false` 开关(默认关闭)，随后使用`management.health.<name>.enabled`选择性地开启HealthIndicator。

#### 开发自定义HealthIndicator
为了增加自己的健康检查逻辑，我们可以定制一个 `HealthIndicator` 实现，通常会选择扩展 `AbstractHealthIndicator` 类，实现其中的 `doHealthCheck()` 方法。

根据 `HealthEndpointonfiguration` 类的代码，我们可以知道 `healthContributorRegistry()` 会从Spring 上下文获取所有 `HealthContributor` 类型 (`HealthIndicator` 继承了这个接口)的 Bean，并进行注册，所以我们也只需要把写好的 `HealthIndicator` 配置为 Bean 即可。
```java
/**
 * 自定义HealthIndicator
 * `HealthIndicator` 的实现类必须是 Spring Bean，否则不会被注册到 `HealthEndpoint` 中。
 * @author Johann
 * @version 1.0
 * @see
 **/
@Component
public class ShopReadyHealthIndicator extends AbstractHealthIndicator {

    private BinaryTeaProperties binaryTeaProperties;

    /**
     * 构造函数注入BinaryTeaProperties
     * 此处之所以使用ObjectProvider，是因为BinaryTeaProperties可能还没有被创建
     * @param binaryTeaProperties
     */
    public ShopReadyHealthIndicator(ObjectProvider<BinaryTeaProperties> binaryTeaProperties) {
        this.binaryTeaProperties = binaryTeaProperties.getIfAvailable();
    }

    /**
     * 实现doHealthCheck方法，用于检查应用程序的状态。
     * @param builder the {@link Builder} to report health status and details
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception{
        if (binaryTeaProperties==null || !binaryTeaProperties.isReady()) {
            builder.down().withDetail("message", "Shop is not ready!");
        } else {
            builder.up().withDetail("message", "Shop is ready!");
        }
    }
}
```
> 为什么优先使用objectProvider 获取 Bean？
> 
> 当我们需要从Spring 上下文中获取其他 Bean 时，最直接的方法是使用 @Autowired注解，但系统运行时的不确定性太多了。
> 比如不确定是否存在需要的依赖，这时就需要加上required=false;也有可能目标类型的 Bean 不止一个，而我们只需要一个，构造方法有多参数......
> 
> 这时就该 ObjectProvider<T> 上场了，它大多用于构造方法注入的场景，让我们有能力处理那些尴尬的场面。
> 其中的 getIfAvailable() 方法在存在对应 Bean 时返回对象，不存在时则返回null;getIfunique()方法在有且仅有一个对应 Bean 时返回对象，Bean 不存在或不唯一，且唯一时没有标注 Primary 的情况下返回 null。
> 再加上一些检查和遍历的方法，通过明确的编码我们就可以确保自己的代码获取到必要的依赖，或判断出缺少的东西，并加以处理。

#### 开发自己的端点(非内置端点)
首先，在Bean 上添加 `@Endpoint` 注解，其中带有 `@ReadOperation`、`@WriteOperation` 和 `@DeleteOperation` 的方法能被发布出来，而且能通过JMX或者 HTTP 的方式访问到这些方法。

接下来，如果我们希望限制只用其中的一种方式来发布，则可以将 @Endpoint 替换为 `@JmxEndpoint` 或 `@WebEndpoint`。

如果是通过HTTP 方式访问的，默认的URL是 `/actuator/<id>`，其中的d就是@Endpoint 注解中指定的id，而`@ReadOperation`、`@WriteOperation` 和`@DeleteOperation` 的方法分别对应了HTTP的`GET、POST和 DELETE`方法。

HTTP 的响应码则取决于方法的返回值，如果存在返回内容，则响应码是200 OK，否则@ReadOperation 方法会返回 404 Not Found，而另两个则返回 204 No Content ;对于需要参数但又获取不到的情况，方法会返回 400 Bad Request。
```java
/**
 * 内置端点不满足，开发自己的端点
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Component
@Endpoint(id = "shopEndpoint")
public class ShopEndpoint {

    private BinaryTeaProperties binaryTeaProperties;

    public ShopEndpoint(ObjectProvider<BinaryTeaProperties> binaryTeaProperties) {
        this.binaryTeaProperties = binaryTeaProperties.getIfAvailable();
    }

    @ReadOperation
    public String state() {
        if (binaryTeaProperties == null || !binaryTeaProperties.isReady()) {
            return "Shop is not ready!";
        } else {
            return "OpenHours: " + binaryTeaProperties.getOpenHours() + "\n" +
                    "Shop is ready!";
        }
    }
}
```

### Metrics
MicroMeter 是一个应用程序度量工具，它提供了一种简单的方式来收集应用程序的指标，并将这些指标数据发送到各种监控系统中，比如 Prometheus、InfluxDB、Graphite、Elasticsearch 等。

Micrometer 通过 Meter 接口来收集系统的度量数据，由 MeterRegistry 来创建并管理 Meter。Micrometer 支持的各种监控系统都有自己的 MeterRegistry 实现。

内置的Meter 实现分为几种：

| Meter类型 | 说明 |
|-|-|
| Timer | 计时器，用来记录一个事件的耗时 |
| Counter | 计数器，用来表示一个单调递增的值 |
| Gauge | 计量仪，用来表示一个变化的值，通常能用 counter 就不用 Gauge |
| DistributionSummary | 分布统计，用来记录事件的分布情况，可以设置一个范围，获取范围内的直方图和百分位数 |
| LongTaskTimer | 长任务计时器，记录一个长时间任务的耗时，可以记录已经耗费的时间 |
| FunctionCounter | 函数计数器，追踪某个单调递增函数的计数器 |
| FunctionTimer | 函数计时器，追踪两个单调递增函数，一个计数，另一个计时 |

#### 常用的度量指标
Spring Boot Actuator 中提供了 metrics 端点，通过 `/actuator/metrics` 我们可以获取系统的度量值。而且 Spring Boot 还内置了很多实用的指标，可以直接拿来使用。

以下是Micrometer 本身支持的JVM 相关指标：

| 度量指标 | 说明 |
|-|-|
| ClassLoaderMetrics | 收集加载和卸载的类信息 |
| JvmMemoryMetrics | 收集JVM 内存利用情况 |
| JvmGcMetrics | 收集JVM 的 GC 情况 |
| ProcessorMetrics | 收集 CPU负载情况 |
| JvmThreadMetrics | 收集JVM中的线程情况 |

#### 自定义度量指标
有两种绑定 Meter 的方法，一般可以考虑使用后者:
* 注人 Spring 上下文中的 MeterRegistry，通过它来绑定 Meter；
* 让 Bean 实现 MeterBinder，在其 bindTo() 方法中绑定 Meter。
```java
/**
 * 自定义度量指标，监控经营指标
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Component
public class SalesMetrics implements MeterBinder {

    private Counter orderCount;
    private Counter orderAmount;
    private DistributionSummary orderSummary;
    private AtomicInteger averageAmount = new AtomicInteger();

    /**
     * 绑定度量指标
     * @param meterRegistry
     */
    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        this.orderCount = meterRegistry.counter("order.count","direction","income");
        this.orderAmount = meterRegistry.counter("order.amount","direction","income");
        this.orderSummary = meterRegistry.summary("order.summary","direction","income");
        meterRegistry.gauge("order.average.amount",averageAmount);
    }
}
```

#### 度量的输出
##### 输出到日志
```text
/**
 * 自定义MeterRegistry,将度量信息输出到日志中
 * @return
 */
@Bean
public MeterRegistry customMeterRegistry() {
    CompositeMeterRegistry meterRegistry = new CompositeMeterRegistry();
    meterRegistry.add(new SimpleMeterRegistry());
    meterRegistry.add(new LoggingMeterRegistry());
    return meterRegistry;
}
```
##### 输出到 Prometheus
```text
<!-- 添加依赖 -->
<!--将度量信息直接输出到监控系统-->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>

<!-- 添加配置 -->
management.endpoints.web.exposure.include=prometheus

访问:  /actuator/prometheus 
```