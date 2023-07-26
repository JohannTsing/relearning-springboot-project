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
