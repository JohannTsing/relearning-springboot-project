package com.johann.customer.runner;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 一个 CommandLineRunner 的实现，它的作用是打印所有的命令行参数
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Component
@Slf4j
// 通过@0rder 注解或Ordered 接口来指定运行的顺序
@Order(1)
public class ArgsPrinterRunner implements CommandLineRunner {
    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("共传入了 {} 个参数。分别是:{}",args.length, StringUtils.arrayToCommaDelimitedString(args));
        // jdbcBasic();
    }

    /**
     * JDBC普通查询操作
     * @throws ClassNotFoundException
     */
    public void jdbcBasic() throws ClassNotFoundException {
        // 1.加载驱动
        // 2.获取连接
        // 3.创建语句
        // 4.执行语句
        // 5.处理结果
        // 6.释放资源
        Class.forName("org.h2.Driver");
        // 此处使用了try-with-resource的语法，因此不用在finally段中关闭资源
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test_db");
             Statement statement = connection.createStatement();
             ResultSet resultSet =
                     statement.executeQuery("SELECT X FROM SYSTEM_RANGE(1, 10)")) {
            while (resultSet.next()) {
                log.info("取值：{}", resultSet.getInt(1));
            }
        } catch (Exception e) {
            log.error("出错啦", e);
        }
    }

}
