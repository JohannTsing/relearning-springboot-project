### 事务简介
数据库事务是访问并可能操作各种数据项的一个数据库操作序列，这些操作要么全部执行，要么全部不执行，是一个不可分割的工作单位。事务由事务开始与事务结束之间执
行的全部数据库操作组成。

事务具有以下四个属性，通常称为 ACID 属性：
- 原子性（Atomicity）：事务是一个原子操作单元，其对数据的修改，要么全都执行，要么全都不执行。
- 一致性（Consistency）：在事务开始之前和事务结束以后，数据库的完整性没有被破坏。
- 隔离性（Isolation）：数据库允许多个并发事务同时对其数据进行读写和修改的能力，隔离状态可以避免事务间的相互影响。
- 持久性（Durability）：事务完成之后，它对于数据的修改是永久性的，即使出现系统故障也能够保持。

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

### Spring事务管理的核心接口（TransactionManager）
Spring 事务管理的核心是事务管理器，即 TransactionManager，它是一个空接口，通常都会将 PlatformTransactionManager 作为核心接口。其中包含了获取事务、提交事务和回滚事务的方法。
```java
public interface PlatformTransactionManager extends TransactionManager {
    TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException;

    void commit(TransactionStatus status) throws TransactionException;

    void rollback(TransactionStatus status) throws TransactionException;
}
```
Spring 为不同的持久化框架提供了不同的事务管理器实现，如 DataSourceTransactionManager、HibernateTransactionManager、JpaTransactionManager 等。Spring 也支持使用 JTA 来管理分布式事务。

### Spring事务定义（TransactionDefinition）
用来描述事务定义的TransactionDefinition 接口中包含了几个与事务密切相关的属性：
- 传播性
- 隔离级别
- 超时时间
- 是否只读

#### 传播性
事务的传播性是指如果在开始当前事务之前，一个事务上下文已经存在，此时有若干选项可以指定一个事务性方法的执行行为。例如，如果一个事务性方法开始执行之前，Spring 的事务管理器检测到一个现有的事务上下文，则该方法可以选择使用现有的事务。也可以暂停现有的事务，并在方法执行之后恢复它。

事务的传播性(Propagation)指的是,当多个事务同时存在时,当前事务应该如何去管理其他事务的执行。Spring 事务的传播性有 7 种，分别为：
http://te-amo.site/article/24