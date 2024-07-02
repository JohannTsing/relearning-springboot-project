### Hibernate
Hibernate 是一款基于Java 语言的开源对象关系映射框架。所谓对象关系映射（ORM，Object-Relational-Mapping），简单来说就是将面向对象的领域模型与关系型数据库中的表互相映射起来。
对象关系映射很好地解决了对象与关系间的阻抗不匹配(impedance mismatch)问题。

面向对象与关系型数据库的简单对比：

| 对比维度  | 面向对象概念             | 关系型数据库 |
|-------|--------------------|--------|
| 粒度    | 接口、类               | 表、视图   |
| 继承与多态 | 有                  | 没有     |
| 唯一性   | a == b或a.equals(b) | 主键     |
| 关联关系  | 引用                 | 外键     |

Hibernate 不仅将 Java对象与数据表互相映射起来，还建立了 Java数据类型到SQL数据类型的映射，提供了数据查询与操作的能力，能够自动根据操作来生成 SQL 调用。

2006年，Java 的持久化标准JPA (Java Persistent API，Java 持久化API)正式发布。它的目标就是屏蔽不同持久化 API之间的差异，简化持久化代码的开发工作。当时的JPA 标准基本就是以Hiberate 作为蓝本来制定的。2010 年，Hibermate 3.5 成为了JPA 2.0 的认证实现。

Hibernate 与 JPA 接口的对应关系与实现：

| JPA接口                | Hibernate 接口   | 实现类                | 作用                    |
|----------------------|----------------|--------------------|-----------------------|
| EntityManagerFactory | SessionFactory | SessionFactoryImpl | 管理领域模型与数据库的映射关系       |
| EntityManager        | Session        | SessionImpl        | 基本的工作单元，封装了连接与事务相关的内容 |
| EntityTransaction    | Transaction    | TransactionImpl    | 用来抽象底层的事务细节           |
> 虽然`SessionFactory`或`EntityManagerFactory` 的创建成本比较高，好在它们是线程安全的。一般应用程序中只有一个实例，而且会在程序中共享。

#### 定义实体对象

##### 1, 实体及主键
“对象”与“关系”之间的关系，是由实体对象及其上添加的注解来承载的。

定义实体及其主键的注解：

| 注解              | 作用                       | 属性说明                                                                                               |
|-----------------|--------------------------|----------------------------------------------------------------------------------------------------|
| @Entity         | 标识类是实体类                  | name，默认为非限定类名，用在 HQL(Hibernate query language，Hibernate查询语言) 查询中标识实体                               |
| @Table          | 指定实体对应的数据表，不加的话默认将类名作为表名 | name，默认是实体名; schema，默认是用户的默认Schema                                                                 |
| @Id             | 指定字段是实体的主键               | name                                                                                               |
| @GeneratedValue | 指定主键的生成策略                | strategy，指定生成策略，共有四种策略: TABLE、SEQUENCE、IDENTITY 和 AUTO; generator，指定生成器，用于 TABLE 和 SEQUENCE 这两种策略中 |

##### 2, 字段
一些字段相关的常用注解：

| 注解                 | 作用                                                   | 属性说明                                                                                                       |
|--------------------|------------------------------------------------------|------------------------------------------------------------------------------------------------------------|
| @Basic             | 映射简单类型，例如 Java 原子类型及其封装类、日期时间类型等，一般不用添加该注解，默认就有同样的效果 | -                                                                                                          |
| @Column            | 描述字段信息                                               | name，字段名称，默认同属性名； unique 是否唯一； nullable 是否可为 null； insertable 是否出现在 INSERT 语句中； updatable 是否出现在 UPDATE 语句中 |
| @Enumerated        | 映射枚举类型                                               | value，映射方式，默认是 ORDINAL，使用枚举的序号，也可以用 STRING，使用枚举值                                                           |
| @Type              | 定义Hibernate 的类型映射，这是 Hibernate的注解                    | type，Hibernate 类型实现的全限定类名；parameters，类型所需的参数                                                               |
| @Temporal          | 映射日期与时间类型，适用于 `java.util.Date`和 `java.util.Calendar` | value，要映射的内容，DATE 对应 `java.sql.Date`，TIME 对应 `java.sgl.Time`，TIMESTAMP 对应`java.sql.Timestamp`              |
| @CreationTimestamp | 插入时传入当前时间，这是 Hibernate 的注解                           | -                                                                                                          |
| @UpdateTimestamp   | 更新时传人当前时间，这是 Hibernate 的注解                           | -                                                                                                          |

> 为什么一定要用 Money 类来表示金额
> 
> 在处理金额时，千万不要想当然地使用 float 或者 double 类型。原因是在遇到浮点数运算时，精度的丢失可能带来巨大的差异，甚至会造成资金损失。虽然BigDecimal在计算时能顺利过关，但金额的内容却不止是一个数值，还有与之关联的币种(ISO-4217)、单位等内容。
> 
> 以人民币为例，标准的币种简写是 CNY，最小单位(代码中用Minor 表示)是分，主要单位(代码中用 Major 表示)是元。美元、欧元、日元等货币都有各自的规范。在不同的货币之间，还有货币转换的需求。所以说，我们需要一个专门用来表示金额的类，而Joda-Money 就是一个好的选择。举个例子，我们可以通过 Money.ofMinor(CurrencyUnit.of("CNY")，1234)，创建代表人民币12.34 元的对象，Money.of(CurrencyUnit.of("CNY")，12.34)与之等价。

##### 3, 关联关系
常见关系及其对应的注解：

| 关系类型 | 数据库实现方式 | 注解                               |
|------|---------|----------------------------------|
| 1:1  | 外键      | @OneToOne                        |
| 1:n  | 外键      | @OneToMany、@JoinColumn、 @OrderBy |
| n:1  | 外键      | @ManyToOne、@JoinColumn           |
| n:n  | 关联表     | @ManyToMany、@JoinTable、 @OrderBy |

#### 使用 Hibernate API 

Hibernate 与 JPA 需要配置的内容:

| 配置内容 | Hibernate | JPA                                                                     |
|----|----|-------------------------------------------------------------------------|
| 会话工厂 | LocalSessionFactoryBean | LocalEntityManagerFactoryBean / LocalContainerEntityManagerFactoryBean  | 
| 事务管理器 | HibernateTransactionManager | JpaTransactionManager |

Spring Boot 的 HibernateJpaConfiguration 提供了一整套完整的自动配置。如果我们不想自己动手，可以把配置的工作交给Spring Boot，只需要确保有一个明确的主DataSource Bean 即可。

##### 手动配置Hibermate 的相关Bean
```java
@Configuration
@Slf4j
@ConditionalOnProperty(name = "when.test.hibernate", havingValue = "true")
public class HibernateConfig {

    /**
     * FactoryBean 可以创建一个 Hibernate SessionFactory。
     * 这是在 Spring 应用程序上下文中设置共享 Hibernate SessionFactory 的常用方法；然后，可以通过依赖注入将 SessionFactory 传递给数据访问对象。
     *
     * LocalSessionFactoryBean 是一个工厂 bean，用于创建 Hibernate 的 SessionFactory。
     * 它会自动扫描类路径下的所有实体类，映射到对应的数据库表。
     * 它还可以配置 Hibernate 的各种属性，如数据库方言、是否显示 SQL 等等。
     *
     * 会话工厂（SessionFactory）是一个重量级对象，所以通常情况下一个应用程序只需要一个会话工厂。
     * 会话工厂是创建会话的工厂，会话是持久化操作的主要接口。
     * 会话工厂是线程安全的，所以它可以在多个线程之间共享。它通常在应用程序启动时创建，然后在整个应用程序生命周期中使用。
     * @param dataSource
     * @return
     */
    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource){
        log.info("会话工厂初始化");
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setHibernateProperties(hibernateProperties());
        sessionFactoryBean.setPackagesToScan("com.johann.binarytea.hibernate.model");
        return sessionFactoryBean;
    }

    /**
     * 本地容器实体管理器工厂 bean，用于创建 JPA 的 EntityManagerFactory。
     * 它会自动扫描类路径下的所有实体类，映射到对应的数据库表。
     * 它还可以配置 JPA 的各种属性，如数据库方言、是否显示 SQL 等等。
     *
     * 实体管理器工厂（EntityManagerFactory）是一个重量级对象，所以通常情况下一个应用程序只需要一个实体管理器工厂。
     * 实体管理器工厂和会话工厂类似，都是创建会话的工厂，会话是持久化操作的主要接口。
     * 实体管理器工厂是 JPA 的核心接口，它负责创建 EntityManager。
     * 实体管理器工厂是线程安全的，所以它可以在多个线程之间共享。它通常在应用程序启动时创建，然后在整个应用程序生命周期中使用。
     * @return
     */
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource);
//        em.setPackagesToScan("com.johann.binarytea.hebernate.model");
//        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//        em.setJpaProperties(hibernateProperties());
//        return em;
//    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        // hibernate.hbm2ddl,auto，自动根据实体类生成DDL语句并执行，create-drop表示每次启动时都重新创建表结构
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        // hibernate.show sgl，打印 Hibernate 具体执行的 SQL 语句;
        properties.setProperty("hibernate.show_sql", "true");
        // hibernate.format_sql，格式化 SQL 语句，便于阅读
        properties.setProperty("hibernate.format_sql", "true");
        return properties;
    }

    /**
     * 事务管理器用于配置事务管理器。它需要一个 SessionFactory，因为它需要知道哪个 SessionFactory 是要管理的。
     * @param sessionFactory
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory){
        log.info("事务管理器初始化");
        HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
        log.info("事务管理器是否允许嵌套事务：{}", transactionManager.isNestedTransactionAllowed());
        transactionManager.setNestedTransactionAllowed(true);
        log.info("事务管理器是否允许嵌套事务：{}", transactionManager.isNestedTransactionAllowed());
        return transactionManager;
    }
}
```

##### Hibernate API 操作数据库
```java
/**
 * HibernateDaoSupport 辅助类是一个方便的超类，用于简化 Hibernate 数据访问代码。
 * 它包含一个 HibernateTemplate，可以用于管理 Hibernate Session 的创建和关闭。
 * 通过继承 HibernateDaoSupport，可以将 HibernateTemplate 注入到数据访问对象中。
 *
 * HibernateDaoSupport 与 HibernateTemplate 的区别在于：
 * HibernateTemplate 是一个模板类，它封装了所有的 Hibernate 操作，包括创建和关闭 Session，加载对象，保存对象，删除对象，查询对象等。
 * HibernateDaoSupport 是一个抽象类，它提供了一个 HibernateTemplate 对象，用于简化 Hibernate 数据访问代码。
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Repository
// @Transactional 事务一般加在 Service 层上
@Transactional
public class MenuRepositoryHibernate extends HibernateDaoSupport {

    /**
     * 通过构造函数注入 SessionFactory
     * @param sessionFactory
     */
    public MenuRepositoryHibernate(SessionFactory sessionFactory){
        super.setSessionFactory(sessionFactory);
    }

    /**
     * 获取菜单项数量
     * @return
     */
    public long countMenuItems() {
        // 使用 HQL 查询语言
        return getHibernateTemplate().execute(session ->
                session.createQuery("select count(m) from MenuItem m", Long.class).uniqueResult());
    }

    public long countMenuItems2() {
        // 使用 HQL 查询语言
        return getSessionFactory().getCurrentSession()
                .createQuery("select count(m) from MenuItem m",Long.class).getSingleResult();
    }

    /**
     * 查询所有的菜单项
     * @return
     */
    public List<MenuItem> queryAllItems(){
        return getHibernateTemplate().loadAll(MenuItem.class);
    }

    /**
     * 根据 id 获取菜单项
     * @param id
     * @return
     */
    public MenuItem queryForItem(Long id){
        return getHibernateTemplate().get(MenuItem.class,id);
    }

    /**
     * 插入菜单项
     * @param menuItem
     */
    public void insertItem(MenuItem menuItem){
        getHibernateTemplate().save(menuItem);
    }

    /**
     * 更新菜单项
     * @param item
     */
    public void updateItem(MenuItem item) {
        getHibernateTemplate().update(item);
    }

    /**
     * 删除菜单项
     * @param id
     */
    public void deleteItem(Long id) {
        getHibernateTemplate().delete(Objects.requireNonNull(queryForItem(id)));
    }
}
```

#### 使用 Spring Data JPA
Spring Data 项目为不同的常见数据库提供了统一的 Repository 抽象层。我们可以通过约定好的方式定义接口，使用其中的方法来声明需要的操作，剩下的实现工作完全交由 Spring Data 来完成。

Spring Data的核心接口是 `Repository<T,ID>`，T是实体类型，ID 是主键类型。
一般我们会使用它的子接口 `CrudRepository<T,ID>`或者`PagingAndSortingRepository<T,ID>`。
`CrudRepository<T,ID>`提供了最基本的对实体类的添删改查操作，继承它就拥有了这些方法。`PagingAndSortingRepository<T,ID>`继承`CrudRepository<T,ID>`，在此基础上提供了分页与排序功能。

Spring DataJPA 是专门针对JPA 的，在 pomxml 中引人 org.springframework.boot:spring-bootstarter-data-jpa 就能添加所需的依赖。
其中提供了一个专属的 `JpaRepository<T,ID>`接口，可以在配置类上增加`@EnableJpaRepositories` 来开启 JPA 的支持，通过这个注解还可以配置一些个性化的信息，比如要扫描Repository 接口的包。

Spring Boot 的`JpaRepositoriesAutoConfiguration` 提供了 `JpaRepository` 相关的自动配置，只要符合条件就能完成配置。
它通过 `@Import` 注解导人了 `JpaRepositoriesRegistrar` 类，其中直接定义了一个静态内部类 `EnableJpaRepositoriesConfiguration`，
上面添加了 `@EnableJpaRepositories`，所以在 SpringBoot项目里无须自己添加该注解，只要有相应的依赖，Spring Boot 的自动配置就能帮忙完成剩下的工作

> 在 SpringBoot 2.7 中，是通过`@Import`注解添加了一个 `JpaRepositoriesImportSelector`静态内部类，在这个类中根据需要选择引入`EnversRevisionRepositoriesRegistrar`或`JpaRepositoriesRegistrar`。
> 在这两个类中，都定义了一个静态内部类`EnableJpaRepositoriesConfiguration`，该内部类添加了 `@EnableJpaRepositories`。

##### JPA 查询的基本操作
要定义自己的Repository 只需扩展`CrudRepository<T,ID>`、`PagingAndSortingRepository<T,ID>`或`JpaRepository<T,ID>`，并明确指定泛型类型即可。

> 如果有一些公共的方法希望能剥离到公共接口里，但又不希望这个公共接口被创建成 Repository的Bean，这时就可以在接口上添加`@NoRepositoryBean` 注解。
> JpaRepository 接口就是这样的:它继承了`PagingAndSortingRepository`，但是没有添加`@Repository`注解，所以它不会被创建成Bean。

通用的方法基本能满足大部分需求，但是总会有一些业务所需的特殊查询需要是通用的方法所不能满足的。在 Spring Data 的帮助下，我们只需要根据它的要求定义方法，无须编写具体的实现这就省却了很多工作。
以下几种形式的方法名都可以视为有效的查询方法:
- find...By...
- read...By...
- query...By...
- get...By...
- count...By... (只返回结果数量)

第一段“...”的内容是限定返回的结果条数，比如用`TopN`、`FirstN`表示返回头 N 个结果还可以用 `Distinct` 起到 SQL 语句中 distinct 关键词的效果。
第二段“...”的内容是查询的条件也就是 SQL 语句中 where 的部分，条件所需的内容与方法的参数列表对应，可以通过 And、or 关键词组合多个条件，用 Not 关键词取反。

Spring Data 查询方法支持的关键词:

| 作用 | 关键词 | 示例 | SQL对应 |
|----|----|----|----|
| 相等 | Is、Equals，不写的话默认就是相等 | findByNameIs(String name) | … where x.name = ? |
| 比较 | LessThan、LessThanEqual、GreaterThan、GreaterThanEqual | findByAgeLessThan(int age) | … where x.age < ? |
| 比较 | Between，可用于日期时间的比较 | findByStartDateBetween(Date d1, Date d2) | … where x.startDate between ? and ? |
| 比较 | Before、After，用于日期时间的比较 | findByStartDateBefore(Date d) | … where x.startDate < ? |
| 是否为空 | Null、IsNull、NotNull、NotNull | findByNameIsNull() | … where x.name is null |
| 相似 | Like、NotLike | findByNameLike(String name) | … where x.name like ? |
| 字符串判断 | Startingwith、Endingwith、 Containing | findByNameStartingWith(String name) | … where x.name like ? |
| 忽略字符串大小写 | IgnoreCase、AllIgnoreCase | findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname) | … where UPPER(x.lastname) = UPPER(?) and UPPER(x.firstname) = UPPER(?) |
| 集合 | In、NotIn | findByAgeIn(Collection ages) | … where x.age in ? |
| 布尔判断 | True、False | findByActiveTrue() | … where x.active = true |
| 排序 | OrderBy | findByNameOrderByAgeDescName(String name) | … where x.name = ? order by x.age desc, x.name |

> 排序的时候，也可以在参数中添加一个 Sort 类型的参数灵活地传入期望的排序方式：
> Sort sort = Sort.by("name").descending().and(Sort.by("id").ascending());


另一个常见的需求是分页。方法的返回值可以是 Page<T> 或集合类型，通过传入 `Pageable`类型的参数来指定分页信息。

Spring Data的 Repository 接口方法支持很多种返回类型:单个返回值的，除了常见的T类型也可以是`Optional<T>`;集合类型除了 `Iterable` 相关的类型，还可以是 `Streamable` 的，方便做流式处理。

