package com.johann.customer;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@SpringBootTest
@Slf4j
public class DatasourceApplicationTests {
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 测试Hikari数据源
     * @throws SQLException
     */
//    @Test
//    void testHikariDataSources() throws SQLException {
//        log.info("数据源：{}", applicationContext.getBean("dataSource"));
//        // 断言数据源不为空
//        assertTrue(applicationContext.containsBean("dataSource"));
//        DataSource dataSource = applicationContext.getBean("dataSource", DataSource.class);
//        // 断言数据源是HikariDataSource
//        assertTrue(dataSource instanceof HikariDataSource);
//        // 获取一个数据库连接
//        Connection connection = dataSource.getConnection();
//        // 断言连接是HikariProxyConnection
//        assertTrue(connection instanceof HikariProxyConnection);
//        connection.close();
//
//        // 断言HikariDataSource的最大连接数是10（默认值是10）
//        assertEquals(10, ((HikariDataSource) dataSource).getMaximumPoolSize());
//    }

    /**
     * 测试Druid数据源
     * @throws SQLException
     */
    @Test
    void testDruidDataSources() throws SQLException {
        log.info("数据源：{}", applicationContext.getBean("dataSource"));
        // 断言数据源不为空
        assertTrue(applicationContext.containsBean("dataSource"));
        DataSource dataSource = applicationContext.getBean("dataSource", DataSource.class);
        // 断言数据源是 DruidDataSource
        assertTrue(dataSource instanceof DruidDataSource);
        // 获取一个数据库连接
        Connection connection = dataSource.getConnection();
        // 断言连接是 DruidPooledConnection
        assertTrue(connection instanceof DruidPooledConnection);
        connection.close();

        // 断言DruidDataSource的最大连接数是8（默认值是8）
        assertEquals(DruidDataSource.DEFAULT_MAX_ACTIVE_SIZE, ((DruidDataSource) dataSource).getMaxActive());
    }
}
