package com.johann.binarytea.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.stream.Collectors;

/**
 * 测试事务
 * 没有在XML文件中配置`<tx:annotation-driven/>`标签，也没有在配置类上添加 `@EnableTransactionManagement` 注解，为什么可以使用`@Transactional`注解？
 * 这是因为SpringBoot提供了支持事务的自动自配功能，在`TransactionAutoConfiguration`中的内部类`EnableTransactionManagementConfiguration`中添加了`@EnableTransactionManagement`注解。
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Repository
public class DemoRepository {
    public static final String SQL =
            "insert into t_demo (name, create_time, update_time) values (?, now(), now())";

    private JdbcTemplate jdbcTemplate;

    public DemoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查询操作使用只读事务
     * @return
     */
    //@Transactional(readOnly = true)
    public String showNames() {
        return jdbcTemplate.queryForList("select name from t_demo;", String.class)
                .stream().collect(Collectors.joining(","));
    }

    private TransactionTemplate transactionTemplate;

    //@Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

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
     * 插入操作使用事务，使用默认的传播行为 Propagation.REQUIRED
     */
    //@Transactional(propagation = Propagation.REQUIRED)
    public void insertRecordRequired() {
        jdbcTemplate.update(SQL, "one");
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


    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertRecordRequiresNew() {
        jdbcTemplate.update(SQL, "two");
    }

    //@Transactional(propagation = Propagation.NESTED)
    public void insertRecordNested() {
        jdbcTemplate.update(SQL, "three");
        throw new RuntimeException();
    }
}
