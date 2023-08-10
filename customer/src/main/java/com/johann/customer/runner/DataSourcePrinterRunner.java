package com.johann.customer.runner;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 打印数据源信息
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Component
@Slf4j
@Setter
@Order(1)
public class DataSourcePrinterRunner implements CommandLineRunner, ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    /**
     *
     */
    @Override
    public void run(String... args) throws Exception {
        DataSource dataSource = applicationContext.getBean("dataSource", DataSource.class);
        log.info("数据源：{}", applicationContext.getBean("dataSource"));
        if (dataSource instanceof DruidDataSource) {
            DruidDataSource druidDataSource = (DruidDataSource) dataSource;
            log.info("DruidDataSource DriverClassName：{}", druidDataSource.getDriverClassName());
            log.info("DruidDataSource MaxActive：{}", druidDataSource.getMaxActive());
            log.info("DruidDataSource MinIdle：{}", druidDataSource.getMinIdle());
            log.info("DruidDataSource InitialSize：{}", druidDataSource.getInitialSize());

            Connection connection = druidDataSource.getConnection();
            connection.close();
        }
    }
}
