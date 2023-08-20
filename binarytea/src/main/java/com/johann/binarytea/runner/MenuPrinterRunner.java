package com.johann.binarytea.runner;

import com.johann.binarytea.jdbcTemplate.repository.MenuRepositoryJdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动后，打印菜单项
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Component
@Slf4j
@Order(1)
@ConditionalOnProperty(name = "when.test.jpa", havingValue = "false" ,matchIfMissing = false)
public class MenuPrinterRunner implements ApplicationRunner {

    private MenuRepositoryJdbcTemplate menuRepository;

    public MenuPrinterRunner(MenuRepositoryJdbcTemplate menuRepository) {
        this.menuRepository = menuRepository;
    }

    /**
     *
     * @param args incoming application arguments
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("菜单项总数：{}", menuRepository.countMenuItems());
        menuRepository.queryAllItems()
                .forEach(item -> log.info("菜单项：{}", item));
    }
}
