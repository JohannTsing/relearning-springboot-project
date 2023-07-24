package com.johann.binarytea.runner;

import com.johann.binarytea.repository.MenuRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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
public class MenuPrinterRunner implements ApplicationRunner {

    private MenuRepository menuRepository;

    public MenuPrinterRunner(MenuRepository menuRepository) {
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
