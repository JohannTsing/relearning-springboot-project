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
public class OrderRepositoryTest {

    @Autowired
    private OrderRepositoryJpa orderRepositoryJpa;

    @Test
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

//    void testFindByMaker_NameLikeIgnoreCaseOOrderByUpdateTimeDescId() {
//        // TODO
//    }

}
