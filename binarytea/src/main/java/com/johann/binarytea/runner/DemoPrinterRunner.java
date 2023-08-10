package com.johann.binarytea.runner;

import com.johann.binarytea.transaction.DemoRepository;
import com.johann.binarytea.transaction.MixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 使用 ApplicationRunner 接口，实现在 Spring Boot 启动后执行的代码
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
//@Component
@Slf4j
@Order(2)
public class DemoPrinterRunner implements ApplicationRunner {

    private DemoRepository demoRepository;
    private MixService mixService;

    @Autowired
    public void setDemoRepository(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    @Autowired
    public void setMixService(MixService mixService) {
        this.mixService = mixService;
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            mixService.doSomething();
        } catch (Exception e) {
            log.error("事务执行失败-：{}", e.getMessage());
        }finally {
            log.info("Names: {}", demoRepository.showNames());
        }
    }
}
