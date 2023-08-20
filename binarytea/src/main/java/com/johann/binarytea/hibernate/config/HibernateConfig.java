package com.johann.binarytea.hibernate.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/

// 使用 Spring Data JPA 相关的配置,注释掉 Hibernate 相关的配置
//@Configuration
@Slf4j
public class HibernateConfig {

    /**
     * FactoryBean 可以创建一个 Hibernate SessionFactory。
     * 这是在 Spring 应用程序上下文中设置共享 Hibernate SessionFactory 的常用方法；然后，可以通过依赖注入将 SessionFactory 传递给数据访问对象。
     *
     * LocalSessionFactoryBean 是一个工厂 bean，用于创建 Hibernate 的 SessionFactory。
     * 它会自动扫描类路径下的所有实体类，映射到对应的数据库表。
     * 它还可以配置 Hibernate 的各种属性，如数据库方言、是否显示 SQL 等等。
     *
     * 会话工厂是一个重量级对象，所以通常情况下一个应用程序只需要一个会话工厂。
     * 会话工厂是创建会话的工厂，会话是持久化操作的主要接口。
     * 会话工厂是线程安全的，所以它可以在多个线程之间共享。它通常在应用程序启动时创建，然后在整个应用程序生命周期中使用。
     * @param dataSource
     * @return
     */
    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource){
        log.info("会话工厂初始化");
        Properties properties = new Properties();
        // hibernate.hbm2ddl,auto，自动根据实体类生成DDL语句并执行，create-drop表示每次启动时都重新创建表结构
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        // hibernate.show sgl，打印 Hibernate 具体执行的 SQL 语句;
        properties.setProperty("hibernate.show_sql", "true");
        // hibernate.format_sql，格式化 SQL 语句，便于阅读
        properties.setProperty("hibernate.format_sql", "true");

        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setHibernateProperties(properties);
        sessionFactoryBean.setPackagesToScan("com.johann.binarytea.hibernate.model");
        return sessionFactoryBean;
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
