package com.johann.customer;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    }
}
