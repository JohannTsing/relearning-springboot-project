package com.johann.customer;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import java.util.List;

/**
 * 一个ApplicationRunner 的实现，它会根据命令行上传入的参数来决定是否等待。
 * 如果我们通过 wait 选项设置了等待时间，则等待时间即为程序里 sleep 对应的秒数，没有 wait就直接结束。
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
//@Component
@Slf4j
public class WaitForOpenRunner implements ApplicationRunner, ApplicationContextAware, Ordered {

    /**
     * Set the {@code ApplicationContext} that this object runs in.
     * Setter注入ApplicationContext
     * ApplicationContextAware接口的作用是让Bean获取ApplicationContext中的所有Bean
     */
    @Setter
    private ApplicationContext applicationContext;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean needWait = args.containsOption("wait");
        if (!needWait) {
            log.info("未营业，无需等待了...");
        }else {
            List<String> waitSeconds = args.getOptionValues("wait");
            if (waitSeconds != null && !waitSeconds.isEmpty()) {
                int seconds = NumberUtils.parseNumber(waitSeconds.get(0),
                        Integer.class);
                log.info("还没开门，先等{}秒。", seconds);
                Thread.sleep(seconds * 1000);
            }
            log.info("其他参数：{}",
                    StringUtils.collectionToCommaDelimitedString(
                            args.getNonOptionArgs()));
        }
        System.exit(SpringApplication.exit(applicationContext));
    }

    /**
     * 通过@0rder 注解或Ordered 接口来指定运行的顺序
     * 获取此对象的顺序值。
     * 数值越大，优先级越低。因此，值最小的对象优先级最高（有点类似于 Servlet 启动时的加载值）。
     * 相同的顺序值将导致受影响对象任意排序。
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 2;
    }
}
