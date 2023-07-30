### 事务简介
数据库事务是访问并可能操作各种数据项的一个数据库操作序列，这些操作要么全部执行，要么全部不执行，是一个不可分割的工作单位。事务由事务开始与事务结束之间执
行的全部数据库操作组成。

事务具有以下四个属性，通常称为 ACID 属性：
- 原子性（Atomicity）：事务是一个原子操作单元，其对数据的修改，要么全都执行，要么全都不执行。
- 一致性（Consistency）：在事务开始之前和事务结束以后，系统的状态都是从一个一致状态转换到另一个一致状态，不会出现中间状态。
- 隔离性（Isolation）：数据库允许多个并发事务同时对其数据进行读写和修改的能力，隔离状态可以避免事务间的相互影响。一个事务的执行不能被其他事务干扰，也就是一个事务内部的操作对并发的其他事务而言是隔离的，并发执行的各个事务之间不会相互干扰。
- 持久性（Durability）：事务完成之后，它对于数据的修改是永久性的，即使出现系统故障也能够保持。

#### 原子性（Atomicity）
以转账操作举例，张三给李四转账100元，完整的事务描述应为：“从张三账户转出100元；向李四账户转入100元”。

原子性保证了，这个“转出操作”和“转入操作”要么都执行，要么都不执行。

如果“转出操作”执行了，而“转入操作”没有执行，那么张三的钱就丢了。如果“转出操作”没有执行，而“转入操作”执行了，那么李四就多了100元。

#### 一致性（Consistency）
假设张三账户有1000元，李四账户有500元，则初始状态为“张三账户1000元，李四账户500元”，结束状态为“张三账户900元，李四账户600元”。

这个事务结束后，无论执行成功与否，系统的状态要么是初始状态，要么是结束状态，不会出现中间状态（张三账户900元，李四账户500元）。

#### 隔离性（Isolation）
“张三给李四转账100元”与“王五给赵六转账50元”这两个事务在执行时是相互独立的，互不干扰的。

如果两个事务操作的是同一份数据，如：“张三给李四转账100元”与“张三给王五转账50元”这两个事务，此时就需要考虑隔离性。

设置不同的事务隔离级别会改变事务的实际执行过程，如果设置事务隔离级别为最高隔离级别，当两个并发事务操作同一份数据时，在真正执行时两个事务就成串行执行了，即事务A执行完事务B才会执行，就可以避免影响数据安全的问题发生。

如果所有的事务的隔离级别都设置为最高隔离级别就会使得所有的事务串行执行，也就会降低效率，所以事务的隔离基本设置是要因业务不同而异。

#### 持久性（Durability）
“张三给李四转账100元”这个事务一旦成功提交，则数据库的状态就会永久性的改变，不会因为系统故障而丢失。

### 全局事务和本地事务

| 区别 | 全局事务(Global Transaction) | 本地事务(Local Transaction) |
|-|-|-|
| 作用范围 | 全局事务的作用范围包括多个数据库,可以对多个数据源进行事务管理 | 本地事务的作用范围仅限于一个数据库,只能对单个数据源进行事务管理 |
| 管理方式 | 全局事务需要一个分布式事务管理器(如 JTA)进行协调管理 | 本地事务可以通过 JDBC 或者 JPA/Hibernate 等持久化技术自己管理 |
| 数据源数量 | 全局事务涉及对多个数据库/数据源的访问 | 本地事务只涉及对单个数据库的访问 |
| 性能和复杂度 | 全局事务管理涉及分布式环境,协调和管理更加复杂 | 本地事务只涉及单库,更简单 |

分布式事务的实现方式有两种：基于 XA 协议的两阶段提交和基于消息的最终一致性。
- 基于消息的最终一致性
  基于消息的分布式事务通过消息队列来实现。事务的参与者通过消息队列来发送和接收消息。如果事务成功，则所有消息都会被成功接收。如果事务失败，则所有消息都会被回滚。
- 基于XA协议的两阶段提交
  XA 协议是由 X/Open 组织提出的分布式事务处理的规范。XA 协议规定了事务管理器（Transaction Manager）和资源管理器（Resource Manager）之间的接口。XA 协议的两阶段提交（Two-Phase Commit，2PC）是一种典型的分布式事务协议，它保证了所有节点要么都执行，要么都不执行。

> Two-Phase Commit (2PC) 和 try-confirm-cancel 都是用于在分布式系统中实现分布式事务的技术。它们之间的主要区别在于，2PC 是一个集中式协议，而 try-confirm-cancel 是一个分布式协议。
>
> 2PC 由一个协调器协调。协调器首先向所有参与者发送准备提交的请求。如果所有参与者都准备好提交，协调器就会发送提交请求。如果任何参与者没有准备好提交，协调器就会发送回滚请求。
>
> try-confirm-cancel 没有协调器。参与者自己决定是否提交或回滚事务。如果参与者决定提交，它会发送确认请求。如果参与者决定回滚，它会发送取消请求。
>
> 2PC 和 try-confirm-cancel 都有各自的优缺点。2PC 的优势在于它很简单，容易实现。然而，2PC 也比较脆弱，容易出现死锁。
>
> try-confirm-cancel 的优势在于它更健壮，不容易出现死锁。然而，try-confirm-cancel 也比较复杂，难以实现。
>
> 在选择 2PC 还是 try-confirm-cancel 时，需要权衡这两种技术的优缺点。如果系统对可靠性要求很高，那么 2PC 是更好的选择。如果系统对可扩展性要求很高，那么 try-confirm-cancel 是更好的选择。

### Spring事务管理的核心接口（`TransactionManager`）

Spring 事务管理的核心是事务管理器，即 `TransactionManager`，它是一个空接口，通常都会将 `PlatformTransactionManager` 作为核心接口。其中包含了获取事务、提交事务和回滚事务的方法。
```java
public interface PlatformTransactionManager extends TransactionManager {
    TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException;

    void commit(TransactionStatus status) throws TransactionException;

    void rollback(TransactionStatus status) throws TransactionException;
}
```
Spring 为不同的持久化框架提供了不同的事务管理器实现，如 `DataSourceTransactionManager`、`HibernateTransactionManager`、`JpaTransactionManager` 等。Spring 也支持使用 `JTA` 来管理分布式事务。

### Spring事务定义（`TransactionDefinition`）

用来描述事务定义的``TransactionDefinition` 接口中包含了几个与事务密切相关的属性：
- 传播性
- 隔离级别
- 超时时间
- 是否只读

#### 传播性
事务的传播性(Propagation)指的是，当多个事务同时存在时，当前事务应该如何去管理其他事务的执行。

Spring 事务的传播性有 7 种，分别为：
| 传播性 | 值 | 描述 |
|-|-|-|
| Propagation.REQUIRED | 0 | 如果调用“我”的方法存在事务，则“我”加入这个事务；如果调用“我”的方法没有事务，则“我”新建一个事务。 |
| Propagation.SUPPORTS | 1 | 如果当前调用“我”的方法存在事务，则“我”加入这个事务；如果当前没有事务，“我”就以非事务方法执行。 |
| Propagation.MANDATORY | 2 | 如果当前调用“我”的方法存在事务，则“我”加入这个事务；如果当前调用“我”的方法不存在事务，则“我”抛出异常。 |
| Propagation.REQUIRES_NEW | 3 | 不论当前调用“我”的方法是否存在事务，“我”总是会新建一个事务。如果调用“我”的方法存在一个事务则将其挂起 |
| Propagation.NOT_SUPPORTED | 4 | 不论当前调用“我”的方法是否存在事务，“我”都会以非事务的方式运行。 |
| Propagation.NEVER | 5 | “我”不使用事务，且调用“我”的方法也不允许存在事务，否则“我”抛出异常。 |
| Propagation.NESTED | 6 | 如果调用“我”的方法存在事务，那么“我”会在这个事务中新建一个子事务，否则“我”会新建一个事务。 |
```java
public enum Propagation {
    /**
     * 支持当前事务；如果不存在，则创建一个新事务。类似于 EJB 的同名事务属性。
     * 这通常是事务定义的默认设置，并且通常定义了事务同步范围。
     */
    REQUIRED(TransactionDefinition.PROPAGATION_REQUIRED), //REQUIRED(0),
    /**
     * 支持当前事务，如果不存在则以非事务方式执行。类似于 EJB 的同名事务属性。
     * 注意：对于具有事务同步功能的事务管理器来说，SUPPORTS 与没有事务略有不同，因为它定义了同步功能将适用的事务范围。
     * 因此，相同的资源（JDBC 连接、Hibernate 会话等）将在整个指定范围内共享。请注意，这取决于事务管理器的实际同步配置。
     *
     * 一般来说，请谨慎使用 PROPAGATION_SUPPORTS！ 特别是，不要依赖 PROPAGATION_SUPPORTS 范围内的 PROPAGATION_REQUIRED 或 
     * PROPAGATION_REQUIRES_NEW （这可能会导致运行时同步冲突）。 
     * 如果这种嵌套是不可避免的，请确保正确配置事务管理器（通常切换到“实际事务同步”）。
     */
    SUPPORTS(TransactionDefinition.PROPAGATION_SUPPORTS), //SUPPORTS(1),
    /**
     * 支持当前事务；如果不存在当前事务，则抛出异常。类似于 EJB 的同名事务属性。
     *
     * 请注意，PROPAGATION_MANDATORY 作用域中的事务同步总是由周围的事务驱动。
     */
    MANDATORY(TransactionDefinition.PROPAGATION_MANDATORY), //MANDATORY(2),
    /**
     * 创建一个新事务，暂停当前事务（如果存在）。类似于 EJB 的同名事务属性。
     * 注意：实际事务暂停不会在所有事务管理器上立即生效。这尤其适用于 org.springframework.transaction.jta.JtaTransactionManager，
     * 它要求 javax.transaction.TransactionManager 对其可用（这在标准 Java EE 中是服务器特定的）。
     *
     * PROPAGATION_REQUIRES_NEW 作用域总是定义它自己的事务同步。现有的同步将被适当地暂停和恢复。
     */
    REQUIRES_NEW(TransactionDefinition.PROPAGATION_REQUIRES_NEW), //REQUIRES_NEW(3),
    /**
     * 不支持当前事务；而是始终以非事务方式执行。类似于 EJB 的同名事务属性。
     * 注意：实际事务暂停不会在所有事务管理器上立即生效。这尤其适用于 org.springframework.transaction.jta.JtaTransactionManager，
     * 它要求 javax.transaction.TransactionManager 对其可用（这在标准 Java EE 中是服务器特定的）。
     *
     * 请注意，事务同步在 PROPAGATION_NOT_SUPPORTED 作用域中不可用。现有的同步将被适当地暂停和恢复。
     */
    NOT_SUPPORTED(TransactionDefinition.PROPAGATION_NOT_SUPPORTED), //NOT_SUPPORTED(4),
    /**
     * 不支持当前事务；如果存在当前事务，则抛出异常。类似于 EJB 的同名事务属性。
     * 请注意，事务同步在 PROPAGATION_NEVER 作用域中不可用。
     */
    NEVER(TransactionDefinition.PROPAGATION_NEVER), //NEVER(5),
    /**
     * 如果存在当前事务，则在嵌套事务中执行，否则行为类似于 PROPAGATION_REQUIRED。EJB 中没有类似的功能。
     * 注意：嵌套事务的实际创建只适用于特定的事务管理器。在 JDBC 3.0 驱动程序上运行时，这仅适用于 JDBC org.springframework.jdbc.datasource.DataSourceTransactionManager。某些 JTA 提供商可能也支持嵌套事务。
     */
    NESTED(TransactionDefinition.PROPAGATION_NESTED); //NESTED(6);
}
```

#### 隔离级别
数据库的事务有 4 种隔离级别，隔离级别越高，不同事务相互影响的概率就越小，具体就是出现脏读、不可重复读和幻读的情况。

这三种情况的具体描述如下：
- 脏读(dirty read)：事务A 修改了记录1的值但未提交事务，这时事务B 读取了记录1尚未提交的值，但后来事务A 回滚了，事务B 读到的值并不会存在于数据库中，这就是脏读。
- 不可重复读(non-repeatable read)：事务A 会读取记录1两次，在两次读取之间，事务B 修改了记录1的值并提交了，这时事务A 第一次与第二次读取到的记录1的内容就不一样了，这就是不可重复读。
- 幻读(phantom read)：事务A 以某种条件操作了数据表中的一批数据，这时事务B 往表中插人并提交了1条记录，正好也符合事务A 的操作条件，当事务A 再次以同样的条件操作这批数据时，就会发现操作的数据集变了，这就是幻读。以`SELECT count(*)`为例，发生幻读时，如果两次以同样的条件来执行，结果值就会不同。

> 不可重复读(non-repeatable read) 和 幻读(phantom read) 的区别：
> * 不可重复读的重点是修改，多次读取同一数据得到不同结果。
> * 幻读的重点是新增或者删除，多次读取的记录集合发生了变化。

`TransactionDefinition` 中也对事务隔离级别做了具体的定义，引用了`JDBC Connection` 中的常量，具体信息如下：
| 隔离性 | 值 | 脏读 | 不可重复读 | 幻读 |
|-|-|-|-|-|
| ISOLATION_READ_UNCOMMITTED | 1 | 存在 | 存在 | 存在 |
| ISOLATION_READ_COMMITTED | 2 | 不存在 | 存在 | 存在 |
| ISOLATION_REPEATABLE_READ | 4 | 不存在 | 不存在 | 存在 |
| ISOLATION_SERIALIZABLE | 8 | 不存在 | 不存在 | 不存在 |
```java
public interface TransactionDefinition {
    /**
     * 使用底层数据存储的默认隔离级别。所有其他级别都与 JDBC 隔离级别相对应。
     * MySQL默认的隔离级别是 REPEATABLE_READ，Oracle默认的隔离级别则是 READ_COMMITTED。
     */
    int ISOLATION_DEFAULT = -1;
    /**
     * 表示可能发生脏读、不可重复读和幻读。
     * 该级别允许一个事务更改的记录在提交前被另一个事务读取（"脏读"）。如果任何更改被回滚，则第二个事务将获取一条无效记录。
     */
    int ISOLATION_READ_UNCOMMITTED = 1;
    /**
     * 表示防止脏读；可以进行不可重复读和幻读。
     * 该级别只禁止事务读取有未提交更改的记录。
     */
    int ISOLATION_READ_COMMITTED = 2;
    /**
     * 表示防止脏读和不可重复读；可能发生幻读。
     * 该级别禁止事务读取包含未提交更改的记录，还禁止出现以下情况：一个事务读取记录，第二个事务更改记录，第一个事务重新读取记录，第二次获得不同的值（"不可重复读取"）。
     */
    int ISOLATION_REPEATABLE_READ = 4;
    /**
     * 表示禁止脏读、不可重复读和幻读。
     * 该级别包括 ISOLATION_REPEATABLE_READ 中的禁止规定，并进一步禁止以下情况：一个事务读取了满足 WHERE 条件的所有记录，第二个事务插入了满足该 WHERE 条件的记录，第一个事务针对同一条件重新读取，并在第二次读取中获取了额外的 "幽灵 "记录。
     */
    int ISOLATION_SERIALIZABLE = 8;
}
```
> Oracle数据库支持以下三种事务隔离级别（transaction isolation level）:
> * READ COMMITTED（读已提交）：Oracle 默认使用的事务隔离级别。事务内执行的查询只能看到查询执行前（而非事务开始前）就已经提交的数据。Oracle 的查询永远不会读取脏数据（未提交的数据）。Oracle 不会阻止一个事务修改另一事务中的查询正在访问的数据，因此在一个事务内的两个查询的执行间歇期间，数据有可能被其他事务修改。
> * READ ONLY（只读）：只读事务只能看到事务执行前就已经提交的数据，且事务中不能执行INSERT，UPDATE及DELETE语句。
> * SERIALIZABLE（串行化）：串行化隔离的事务只能看到事务执行前就已经提交的数据，以及事务内 INSERT ，UPDATE及DELETE语句对数据的修改。串行化隔离的事务不会出现不可重复读取或不存在读取的现象。

### Spring事务的基本配置
Spring Framework 的核心类是 `TransactionManager`，并且在上下文中需要一个 `PlatformTransactionManager` Bean，例如，`DataSourceTransactionManager` 或者`JpaTransactionManager`。

可以像下面这样来定义一个 `PlatformTransactionManager` :
```java
@Configuration
public class TransactionConfiguration {
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

在Spring Boot中，提供了一整套事务的自动配置，主要的自动配置类是 `DataSourceTransactionManagerAutoConfiguration`和`TransactionAutoConfiguration`。

`DataSourceTransactionManagerAutoConfiguration` 的作用主要是自动配置 `DataSourceTransactionManager`，它的源码如下：
```java
/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link JdbcTransactionManager}.
 *
 */
// 该自动配置类需要在 TransactionAutoConfiguration 之前执行
@AutoConfiguration(before = TransactionAutoConfiguration.class)
// 检查 JdbcTemplate 和 TransactionManager 类是否在类路径上
@ConditionalOnClass({ JdbcTemplate.class, TransactionManager.class })
// 定义该类的配置顺序，值越小优先级越高，LOWEST_PRECEDENCE = Integer.MAX_VALUE;
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceTransactionManagerAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    // 1, 当Spring 上下文中提供了明确的一个 DataSource (只有一个或者标明了一个主要的 Bean)
    @ConditionalOnSingleCandidate(DataSource.class)
    static class JdbcTransactionManagerConfiguration {

        @Bean
        // 2, 当Spring 上下文中没有提供明确的一个 PlatformTransactionManager Bean
        // 3, Spring Boot会自动创建一个DataSourceTransactionManager Bean
        @ConditionalOnMissingBean(TransactionManager.class)
        DataSourceTransactionManager transactionManager(Environment environment, DataSource dataSource,
                                                        ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
            DataSourceTransactionManager transactionManager = createTransactionManager(environment, dataSource);
            /**
             *  TransactionManagerCustomizers是 Spring Boot 的自动配置留下的扩展点可以让我们通过创建TransactionManagerCustomizers 来对自动配置的 DataSourceTransactionManager进行微调。
             *  在Spring Boot 中类似的 XXXCustomizer 还有很多，比如在 Web 相关章节里会看到的RestTemplateCustomizer.
             */
            transactionManagerCustomizers.ifAvailable((customizers) -> customizers.customize(transactionManager));
            return transactionManager;
        }

        private DataSourceTransactionManager createTransactionManager(Environment environment, DataSource dataSource) {
            return environment.getProperty("spring.dao.exceptiontranslation.enabled", Boolean.class, Boolean.TRUE)
                    ? new JdbcTransactionManager(dataSource) : new DataSourceTransactionManager(dataSource);
        }

    }

}
```

`DataSourceTransactionManagerAutoConfiguration`执行完毕后，`TransactionAutoConfiguration` 会为事务再提供进一步的配置。

它主要做了两件事:
1. 第一是创建了编程式事务需要用到的 `TransactionTemplate`;
2. 第二是开启了基于注解的事务支持，这部分是由内部类`EnableTransactionManagementConfiguration` 来定义

它的源代码如下：
```java
/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring transaction.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */
@AutoConfiguration
@ConditionalOnClass(PlatformTransactionManager.class)
// TransactionProperties 是事务的属性配置，其中只有两个配置: 
// 1, spring.transaction.default.timeout 用于配置默认超时时间，默认单位为秒; 
// 2, spring.transaction.rollback-on-commit-failure 配置在提交失败时是否回滚。
@EnableConfigurationProperties(TransactionProperties.class)
public class TransactionAutoConfiguration {

    // 省略部分代码

    /**
     * 创建了编程式事务需要用到的 TransactionTemplate
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnSingleCandidate(PlatformTransactionManager.class)
    public static class TransactionTemplateConfiguration {

        @Bean
        @ConditionalOnMissingBean(TransactionOperations.class)
        public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
            return new TransactionTemplate(transactionManager);
        }

    }

    /**
     * 开启了基于注解的事务支持，这部分是由内部类`EnableTransactionManagementConfiguration` 来定义
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(TransactionManager.class)
    @ConditionalOnMissingBean(AbstractTransactionManagementConfiguration.class)
    public static class EnableTransactionManagementConfiguration {

        @Configuration(proxyBeanMethods = false)
        @EnableTransactionManagement(proxyTargetClass = false)
        @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "false")
        public static class JdkDynamicAutoProxyConfiguration {

        }

        @Configuration(proxyBeanMethods = false)
        @EnableTransactionManagement(proxyTargetClass = true)
        // 如果没有配置`spring.aop.proxy-target-class=true`,此时默认开启
        @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true",
                matchIfMissing = true)
        public static class CglibAutoProxyConfiguration {

        }

    }

}
```
在配置类上添加 `@EnableTransactionManagement` 注解就能开启事务支持。

Spring Framework 的声明式事务是通过 AOP 来实现的，因此根据 AOP 配置的不同，需要选择是否开启对类的代理。当`spring.aop.proxy-target-class=true` 时，可以直接对没有实现接口的类开启声明式事务支持，这也是默认的配置。

翻看 `AopAutoConfiguration` 的源码，也能看到其中有类似的自动配置。可见，在 Spring Boot 中基于 CGLIB 的 AOP 就是默认的 AOP 代理方式。
```java
/**
 * 自动配置 Spring 的 AOP 支持。相当于在配置中启用 @EnableAspectJAutoProxy。
 * 如果 spring.aop.auto=false，配置将不会被激活。proxyTargetClass 属性默认为 true，但可以通过指定 spring.aop.proxy-target-class=false 进行重载。
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "spring.aop", name = "auto", havingValue = "true", matchIfMissing = true)
public class AopAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(Advice.class)
    static class AspectJAutoProxyingConfiguration {

        @Configuration(proxyBeanMethods = false)
        @EnableAspectJAutoProxy(proxyTargetClass = false)
        @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "false")
        static class JdkDynamicAutoProxyConfiguration {

        }

        @Configuration(proxyBeanMethods = false)
        @EnableAspectJAutoProxy(proxyTargetClass = true)
        @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true",
                matchIfMissing = true)
        static class CglibAutoProxyConfiguration {

        }

    }

    // 省略部分代码

}
```

### 声明式事务

#### 基于注解的声明式事务
Spring Framework提供了一个`@Transactional`注解，它可以在类型和方法上（建议在具体的类而非接口上添加`@Transactional`注解。在方法添加该注解时也请只用在修饰符为 public 的方法上）标注与事务相关的信息。

同时，我们也可以使用`JTA` 中的 `@Transactional`注解(在 `javax.transaction` 包里)，两者的作用基本是一样的。

- `@Transactional` 注解可以设置的事务属性：
  | 属性 | 默认值 | 描述 |
  |-|-|-|
  | transactionManager | 默认会找名为 transactionManager 的事务管理器 | 指定事务管理器 |
  | propagation | Propagation.REQUIRED | 指定事务的传播性 |
  | isolation | Isolation.DEFAULT | 指定事务的隔离性 |
  | timeout | -1，即由具体的底层实现来设置 | 指定事务超时时间 |
  | readOnly | false | 是否为只读事务 |
  | rollbackFor / rollbackForClassName | - | 指定需要回滚事务的异常类型 |
  | noRollbackFor /noRollbackForClassName | - | 指定无须回滚事务的异常类型 |
> 注意：
> 默认情况下，事务只会在遇到 RuntimeException 和 Error 时才会回滚，碰到受检异常(checked exception)时并不会回滚。
> 例如，我们定义了一个业务异常 BizException，它继承的是Exception 类，在代码抛出这个异常时，事务不会自己回滚，但我们可以手动设置回滚，或者在rollbackFor 中进行设置。

- 开启注解驱动的事务支持的两种方式：
    1. 在配置类上添加 `@EnableTransactionManagement` 注解。
    2. 通过`<tx:annotation-driven/>`这个XML标签来启用注解支持。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- 开启事务注解支持,可以明确设置一个TransactionManager -->
    <tx:annotation-driven transaction-manager="txManager"/>

</beans>
```

- 注解驱动事务支持的部分配置：
  | 支持的方式 | 配置项 | 默认值 | 含义 | 补充 |
  |-|-|-|-|
  | `<tx:annotation-driven/>`, @EnableTransactionManagement | mode | proxy | 声明式事务AOP的拦截方式，默认proxy是代理方式也可以改为 aspectj | - |
  | `<tx:annotation-driven/>`, @EnableTransactionManagement | proxy-target-class（XML） / proxyTargetClass | false | 是否使用 CGLIB 的方式拦截类 | 虽然此处的默认值是 false，但是通过`TransactionAutoConfiguration`中的配置得知，在Spring Boot 中，默认会使用 CGLIB 的方式来做拦截。 |
  | `<tx:annotation-driven/>`, @EnableTransactionManagement | order | Ordered.LOWEST_PRECEDENCE | 声明式事务 AOP拦截的顺序，值越小，优先级越高 | - |
  | `<tx:annotation-driven/>` | transaction-manager | transactionManager | 指定事务管理器 | - |

在`<tx:annotation-driven/>` 中还有一个 `transacation-manager` 属性，当事务管理器的名字不是`transactionManager` 时用来指定事务要使用的事务管理器。但 `@EnableTransactionManagement` 里却没有这一属性，它会根据类型来做注入。
如果希望明确指定使用哪个 `TransactionManager`，可以让 `@Configuration` 类实现 `TransactionManagementConfigurer` 接口，在 `annotationDrivenTransactionManager()`方法里返回希望使用的那个 `TransactionManager`。

> 事务加在哪层比较合适?
> 一次业务操作一般都会涉及多张表的数据，因此在单表的 DAO 或 Repository 上增加事务，粒度太细，并不能实现业务的要求。而在对外提供的服务接口上增加事务，整个事务的范围又太大，一个请求从开始到结束都在一个大事务里，着实又有些浪费。
所以，事务一般放在内部的领域服务上，也就是 Service 层上会是比较常见的一个做法，其中的一个方法，也就对应了一个业务操作。

#### 基于XML的声明式事务
Spring Framework 提供了一系列`<tx/>`的XML 来配置事务相关的AOP 通知。有了AOP通知后我们就可以像普通的 AOP 配置那样对方法的执行进行拦截和增强了。

其中，`<tx:advice/>`用来配置事务通知，如果事务管理器的名字是 `transactionManager`，那就可以不用设置 `transaction-manager` 属性了。具体的事务属性则通过 `<tx:attributes/>`和`<tx:method/>`来设置。
`<tx:method/>`可供设置的属性和 `@Transactional`注解的基本一样：
| 属性 | 默认值 | 描述 |
|-|-|-|
| name | 无 | 要拦截的方法名称，可以带通配符，是唯一的必选项 |
| propagation | REQUIRED | 指定事务的传播性 |
| isolation | DEFAULT | 指定事务的隔离性 |
| timeout | -1 | 指定事务超时时间，单位为秒 |
| read-only | false | 是否为只读事务 |
| rollback-for | - | 会触发回滚的异常清单，以逗号分隔，可以是全限定类名，也可以是简单类名 |
| no-rollback-for | - | 不触发回滚的异常清单，以逗号分隔，可以是全限定类名，也可以是简单类名 |

配置步骤如下：
1. 添加基于XML的声明式事务的配置文件`xml-transaction-config.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.springframework.org/schema/tx
            http://www.springframework.org/schema/tx/spring-tx.xsd
            http://www.springframework.org/schema/aop
            http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--配置事务通知-->
    <tx:advice id="demoRepositoryTx">
        <tx:attributes>
            <tx:method name="showNames" read-only="true"/>
            <tx:method name="insertRecordRequired" />
            <tx:method name="insertRecordRequiresNew" propagation="REQUIRES_NEW"/>
            <tx:method name="insertRecordNested" propagation="NESTED"/>
        </tx:attributes>
    </tx:advice>
    <tx:advice id="mixServiceTx">
        <tx:attributes>
            <tx:method name="*" />
        </tx:attributes>
    </tx:advice>

    <!--配置AOP-->
    <aop:config>
        <aop:pointcut id="demoRepositoryPointcut" expression="execution(* com.johann.binarytea.transaction.DemoRepository.*(..))"/>
        <aop:pointcut id="mixServicePointcut" expression="execution(* com.johann.binarytea.transaction.MixService.*(..))"/>
        <aop:advisor advice-ref="demoRepositoryTx" pointcut-ref="demoRepositoryPointcut"/>
        <aop:advisor advice-ref="mixServiceTx" pointcut-ref="mixServicePointcut"/>
    </aop:config>

</beans>
```
2. 在主类上或者 `@Configuration` 配置类上引入该xml配置文件：
```java
//基于XML的声明式事务
@ImportResource("xml-transaction-config.xml")
```
> 声明式事务背后的原理
>
> Spring Framework 的声明式事务，其本质是对目标类和方法进行了AOP 拦截，并在方法的执行前后增加了事务相关的操作，比如启动事务、提交事务和回滚事务。
>
> 既然是通过AOP 实现的，那它就必定遵循了 AOP 的各种规则和限制。Spring Framework的AOP 增强通常都是通过代理的方式来实现的，这就意味着事务也是在代理类上的。我们必须调用增强后的代理类中的方法，而非原本的对象，这样才能拥有事务。也就是说调用下面的methodwithoutTx() 并不会启动一个事务。
>
> public class Demo {
>      @Trasactional
>      public void methodwithTx() {...}
>
>      public void methodwithoutTx() {
>        this.methodwithTx();
>     }
> }
>
> 如果一定要调用自己的方法，可以从 `ApplicationContext` 中获取自己的代理对象，操作这个对象上的方法，而不是使用 this。
> 或者，也可以在适当配置下，通过`AopContext.currentProxy()`来获得当前的代理。

### 编程式事务
Spring Framework 也提供了编程式事务的支持，它的核心是`TransactionTemplate`，它是一个模板类，用来简化编程式事务的使用。

Spring Boot 在 `TransactionAutoConfiguration` 中包含了一个内部类 `TransactionTemplateConfiguration`，会自动基于明确的 `PlatformTransactionManager` 创建 `TransactionTemplate`。

手动创建`TransactionTemplate`：
```java
@Confiquration
public class TxConfiguration {
    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
```
在使用时，我们主要用它的 `execute()` 和 `executeWithoutResult()`方法，方法的声明形式如下所示:
```text
public <T> T execute(TransactionCallback<T> action) throws TransactionException;
public void executeWithoutResult(Consumer<TransactionStatus> action) throws TransactionException:
```
`TransactionCallback` 接口就一个 `doInTransaction()` 方法，通常都是直接写个匿名类，或者是Lambda 表达式。
```java
@Autowired
private TransactionTemplate transactionTemplate;
/**
 * 使用编程式事务
 * 匿名类形式
 * @return
 */
public String showNamesProgrammatically1() {
    return transactionTemplate.execute(new TransactionCallback<String>() {
        @Override
        public String doInTransaction(TransactionStatus status) {
            return jdbcTemplate.queryForList("select name from t demo;", String.class)
                    .stream().collect(Collectors.joining(","));
        }
    });
}
/**
 * 使用编程式事务
 * Lambda形式
 * @return
 */
public String showNamesProgrammatically2() {
    return transactionTemplate.execute(
            status -> jdbcTemplate.queryForList("select name from t demo;", String.class)
                    .stream().collect(Collectors.joining(",")));
}

/**
 * 使用编程式事务
 * 匿名类形式
 * @return
 */
public void insertRecordRequiredProgrammatically1() {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
        @Override
        protected void doInTransactionWithoutResult(TransactionStatus status) {
            jdbcTemplate.update(SQL, "one");
        }
    });
}
/**
 * 使用编程式事务
 * Lambda形式
 * @return
 */
public void insertRecordRequiredProgrammatically2() {
    transactionTemplate.executeWithoutResult(
            status -> jdbcTemplate.update(SQL, "one"));
}
```
如果希望修改事务的属性，可以直接调用 `TransactionTemplate` 的对应方法，或者在创建时将其作为Bean 属性配置进去，这里建议使用对应的常量，而非写成固定的一个数字。
这些属性是设置在对象上的，如果要在不同的代码中复用同一个 `TransactionTemplate` 对象，请确认它们可以使用相同的配置。

在代码中设置传播性与隔离性，可以使用 `setPropagationBehavior()`和 `setIsolationLevel()`方法，如果是在XML配置中设置Bean属性，则可以选择对应的`propagationBehaviorName`和`isolationLevelName` 属性。

### 异常处理
Spring Framework 为我们提供了一套统一的数据库操作异常体系，它独立于具体的数据库产品甚至也不依赖JDBC，支持绝大多数常用数据库。

它能将不同数据库的返回码翻译成特定的类型，开发者只需捕获并处理 Spring Framework 封装后的异常就可以了。

#### 统一的异常抽象
Spring Framework 的数据库操作异常抽象从 `DataAccessException` 这个类开始，所有的异常都是它的子类。

Spring Framework 又是怎么来理解和翻译这么多不同类型的数据库异常的呢？ 其背后的核心接口就是 `SQLExceptionTranslator`，它负责将不同的 `SQLException` 转换为 `DataAccessException`。

`SQLExceptionTranslator`有三个实现类，分别是`SQLStateSQLExceptionTranslator`、`SQLExceptionSubclassTranslator`和`SQLErrorCodeSQLExceptionTranslator`。
- `SQLStateSQLExceptionTranslator` 会分析异常中的 SQLState，根据标准 SQLState 和常见的特定数据库 SQLState 进行转换;
- `SQLExceptionSubclassTranslator` 根据 `java.sql.SQLException` 的子类类型进行转换;
- `SQLErrorCodeSQLExceptionTranslator` 则是根据异常中的错误码进行转换。

`JdbcTemplate` 中会创建一个默认的 `SQLErrorCodeSQLExceptionTranslator`，择不同配置来进行实际的异常转换。

`SQLErrorCodeSQLExceptionTranslator` 会通过 `SQLErrorCodesFactory` 来获取特定数据库的错误码信息，`SQLErrorCodesFactory` 默认从CLASSPATH的`org/springframework/jdbc/support/sql-error-codes.xml`文件中加载错误码配置，这是个Bean的配置文件，其中都是`SQLErrorCodes`类型的 Bean。

以下是MySQL的配置：
```xml
<bean id="MySQL" class="org.springframework.jdbc.support.SQLErrorCodes">
    <property name="databaseProductNames">
        <list>
            <value>MySQL</value>
            <value>MariaDB</value>
        </list>
    </property>
    <property name="badSqlGrammarCodes">
        <value>1054,1064,1146</value>
    </property>
    <property name="duplicateKeyCodes">
        <value>1062</value>
    </property>
    <property name="dataIntegrityViolationCodes">
        <value>630,839,840,893,1169,1215,1216,1217,1364,1451,1452,1557</value>
    </property>
    <property name="dataAccessResourceFailureCodes">
        <value>1</value>
    </property>
    <property name="cannotAcquireLockCodes">
        <value>1205,3572</value>
    </property>
    <property name="deadlockLoserCodes">
        <value>1213</value>
    </property>
</bean>
```
`SQLErrorCodeSQLExceptionTranslator` 会先尝试`SQLErrorCodes` 中的 `CustomSqlExceptionTranslator` 来转换，接着再尝试 `SQLErrorCodes` 中的 `customTranslations`，最后再根据配置的错误码来判断。如果最后还是匹配不上，就降级到其他 `SQLExceptionTranslator` 上。

#### 自定义错误码处理逻辑
在看过了 Spring Framework 处理数据库错误码的逻辑之后，我们很快就能想到去扩展 `SQLErrorCodes`。`SQLErrorCodesFactory` 其实也预留了扩展点，它会加载 CLASSPATH根目录中的 `sql-error-codes.xm`文件，用其中的配置覆盖默认配置。`CustomsQLErrorCodesTranslation` 提供了根据错误码来映射异常的功能。

扩展MySQl的异常配置：
```xml
<bean id="MySQL" class="org.springframework.jdbc.support.SQLErrorCodes">
    <property name="databaseProductNames">
        <list>
            <value>MySQL</value>
            <value>MariaDB</value>
        </list>
    </property>
    <property name="badSqlGrammarCodes">
        <value>1054,1064,1146</value>
    </property>
    <property name="duplicateKeyCodes">
        <value>1062</value>
    </property>
    <property name="dataIntegrityViolationCodes">
        <value>630,839,840,893,1169,1215,1216,1217,1364,1451,1452,1557</value>
    </property>
    <property name="dataAccessResourceFailureCodes">
        <value>1</value>
    </property>
    <property name="cannotAcquireLockCodes">
        <value>1205,3572</value>
    </property>
    <property name="deadlockLoserCodes">
        <value>1213</value>
    </property>
    <property name="customTranslations">
        <bean class="org.springframework.jdbc.support.CustomSQLErrorCodesTranslation">
            <property name="errorCodes" value="123456" />
            <property name="exceptionClass" value="learning.spring.data.DbSwitchingException" />
        </bean>
    </property>
</bean>
```

还有另一种做法，即直接继承`SQLErrorCodeSQLExceptionTranslator`，覆盖其中的 `customTranslate(String task, @Nullable String sql, SQLException sqlEx)`方法，随后在JdbcTemplate中直接注人我们自己写的类实例。
```java
@Autowired
private JdbcTemplate jdbcTemplate;

@Autowired
private MySQLErrorCodeSQLExceptionTranslator mySQLErrorCodeSQLExceptionTranslator;

public void myMethod() {
    jdbcTemplate.setExceptionTranslator(mySQLErrorCodeSQLExceptionTranslator);
}
```