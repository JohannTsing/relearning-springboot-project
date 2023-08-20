package com.johann.binarytea.springDataJpa;

import com.johann.binarytea.hibernate.model.*;
import com.johann.binarytea.springDataJpa.repository.MenuRepositoryJpa;
import com.johann.binarytea.springDataJpa.repository.OrderRepositoryJpa;
import com.johann.binarytea.springDataJpa.repository.TeaMakerRepositoryJpa;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 使用 ApplicationRunner 接口，实现在 Spring Boot 启动后执行的代码【插入Spring Data 待测试的数据】
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Component
@Slf4j
@org.springframework.core.annotation.Order(3)
@ConditionalOnProperty(name = "when.test.jpa", havingValue = "true")
public class SpringDataInitializationRunner implements ApplicationRunner {

    private TeaMakerRepositoryJpa makerRepository;
    private MenuRepositoryJpa menuRepository;
    private OrderRepositoryJpa orderRepository;

    @Autowired
    public void setMakerRepository(TeaMakerRepositoryJpa makerRepository) {
        this.makerRepository = makerRepository;
    }

    @Autowired
    public void setMenuRepository(MenuRepositoryJpa menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Autowired
    public void setOrderRepository(OrderRepositoryJpa orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 插入菜单选项
        List<MenuItem> menuItemList = Stream.of("Go橙汁", "Python气泡水", "JavaScript苏打水")
                .map(n -> MenuItem.builder().name(n)
                        .size(Size.MEDIUM)
                        .price(Money.ofMinor(CurrencyUnit.of("CNY"), 1200))
                        .build())
                .peek(m -> menuRepository.save(m)).collect(Collectors.toList());

        // 插入制作者
        List<TeaMaker> makerList = Stream.of("LiLei", "HanMeimei")
                .map(n -> TeaMaker.builder().name(n).build())
                .peek(m -> makerRepository.save(m)).collect(Collectors.toList());

        // 插入订单
        Order order = Order.builder()
                .maker(makerList.get(0))
                .amount(Amount.builder()
                        .discount(90)
                        .totalAmount(Money.ofMinor(CurrencyUnit.of("CNY"), 1200))
                        .payAmount(Money.ofMinor(CurrencyUnit.of("CNY"), 1080))
                        .build())
                .items(List.of(menuItemList.get(0)))
                .status(OrderStatus.ORDERED)
                .build();
        orderRepository.save(order);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }

        order = Order.builder()
                .maker(makerList.get(0))
                .amount(Amount.builder()
                        .discount(100)
                        .totalAmount(Money.ofMinor(CurrencyUnit.of("CNY"), 1200))
                        .payAmount(Money.ofMinor(CurrencyUnit.of("CNY"), 1200))
                        .build())
                .items(List.of(menuItemList.get(1)))
                .status(OrderStatus.ORDERED)
                .build();
        orderRepository.save(order);
    }
}
