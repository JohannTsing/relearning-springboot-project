```java
@Configuration
@Slf4j
@ConditionalOnProperty(name = "when.test.hibernate", havingValue = "true",matchIfMissing = false)
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
     * @param dataSource
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

### 1, 使用Hibernate时，不手动配置会话工厂，可能产生的异常

#### 1.1, Write operations are not allowed in read-only mode (FlushMode.MANUAL)
```text
org.springframework.dao.InvalidDataAccessApiUsageException: Write operations are not allowed in read-only mode (FlushMode.MANUAL): Turn your Session into FlushMode.COMMIT/AUTO or remove 'readOnly' marker from transaction definition.
```
#### 1.2, No CurrentSessionContext configured!
```text
org.springframework.orm.jpa.JpaSystemException: No CurrentSessionContext configured!; nested exception is org.hibernate.HibernateException: No CurrentSessionContext configured!

# 配置 current_session_context_class
# spring.jpa.properties.hibernate.current_session_context_class=org.springframework.orm.hibernate5.SpringSessionContext
```

### 2, 同时使用Hibernate和SpringDataJPA时，手动配置会话工厂，可能产生的异常

#### 2.1, No bean named 'entityManagerFactory' available
```text
org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'entityManagerFactory' available

@Bean(name = "entityManagerFactory")
```
这是因为Hibernate的会话工厂名称覆盖了`entityManagerFactory`，解决办法是，将Hibernate的会话工厂名称改为`entityManagerFactory`。

#### 2.2, expected single matching bean but found 2: sessionFactory,entityManagerFactory
```text
org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type 'javax.persistence.EntityManagerFactory' available: expected single matching bean but found 2: sessionFactory,entityManagerFactory
```
这是因为同时创建了Hibernate的会话工厂和JPA的实体管理器工厂，解决办法是，删除Hibernate的会话工厂，或删除JPA的实体管理器工厂。

`SessionFactory`或`EntityManagerFactory` 的创建成本比较高，好在它们是线程安全的。一般应用程序中只有一个实例，而且会在程序中共享。


[3 个常见的 Hibernate 性能问题以及如何在日志文件中查找它们](https://www.baeldung.com/hibernate-common-performance-problems-in-logs)
[初始化懒关联的 5 种方法及使用时机](https://thorben-janssen.com/5-ways-to-initialize-lazy-relations-and-when-to-use-them/)