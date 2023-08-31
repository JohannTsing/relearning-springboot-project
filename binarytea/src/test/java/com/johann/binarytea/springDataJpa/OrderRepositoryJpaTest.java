package com.johann.binarytea.springDataJpa;

import com.johann.binarytea.BinaryteaApplication;
import com.johann.binarytea.hibernate.model.*;
import com.johann.binarytea.springDataJpa.repository.OrderRepositoryJpa;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@SpringBootTest(properties = {"when.test.jpa=true",
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"},
    classes = BinaryteaApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderRepositoryJpaTest {

    @Autowired
    private OrderRepositoryJpa orderRepositoryJpa;

    @Test
    // 在取到 order 后，懒加载的内容有可能没有被加载上来，因此我们在访问时需要增加一个事务，保证在操作时能够取得当前会话。
    // 此处若没有 @Transactional注解，则延迟加载属性会报错。此时可以在 JPA 方法上添加 `@EntityGraph(attributePaths = {"maker", "items"})`
    //@Transactional(readOnly = true)
    @org.junit.jupiter.api.Order(1)
    void testFindByStatusOrderByIdAsc() {
        assertTrue(orderRepositoryJpa.findByStatusOrderByIdAsc(OrderStatus.FINISHED).isEmpty());
        List<Order> list = orderRepositoryJpa.findByStatusOrderByIdAsc(OrderStatus.ORDERED);
        assertEquals(2, list.size());
        assertEquals("Go橙汁", list.get(0).getItems().get(0).getName());
        assertTrue(list.get(0).getId() < list.get(1).getId());
        assertEquals("LiLei", list.get(0).getMaker().getName());
        assertEquals("LiLei", list.get(1).getMaker().getName());
    }

    @Test
    @Transactional(readOnly = true)
    void testFindByMaker_NameLikeIgnoreCaseOOrderByUpdateTimeDescId() {
        List<Order> list = orderRepositoryJpa.findByMaker_NameLikeIgnoreCaseOrderByUpdateTimeDescId("LiLei");
        assertEquals(2, list.size());
        assertTrue(list.get(0).getId() > list.get(1).getId());
        assertEquals("Python气泡水", list.get(0).getItems().get(0).getName());
        assertEquals("LiLei", list.get(0).getMaker().getName());
        assertEquals("LiLei", list.get(1).getMaker().getName());
    }

}
