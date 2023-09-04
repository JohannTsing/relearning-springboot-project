package com.johann.binaryteamybatis.mapper;

import com.johann.binaryteamybatis.model.Amount;
import com.johann.binaryteamybatis.model.Order;
import com.johann.binaryteamybatis.model.OrderStatus;
import com.johann.binaryteamybatis.model.TeaMaker;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@SpringBootTest
@Slf4j
public class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private MenuItemMapper menuItemMapper;
    @Autowired
    private TeaMakerMapper teaMakerMapper;

    /**
     * 测试插入订单，查询订单
     * 注意：H2数据库的data.sql会插入一条订单数据，所以这里新插入的订单id为2
     */
    @Test
    @Transactional
    // 回滚测试中的操作，默认回滚。如果不希望回滚可以使用 @Commit 或者 @Rollback(false)。
    @Rollback
    public void testSaveAndFind() {
        TeaMaker teaMaker = teaMakerMapper.findById(2L);
        Order order = Order.builder()
                .maker(teaMaker)
                .status(OrderStatus.ORDERED)
                .amount(Amount.builder()
                        .discount(90)
                        .totalAmount(Money.of(CurrencyUnit.of("CNY"), 1200))
                        .payAmount(Money.of(CurrencyUnit.of("CNY"), 1080))
                        .build())
                .build();
        assertEquals(orderMapper.save(order), 1);

        Long orderId = order.getId();
        assertEquals(orderId,2);
        assertEquals(orderMapper.addOrderItem(orderId, menuItemMapper.findById(2L)), 1);

        Order findOrder = orderMapper.findById(orderId);
        TeaMaker findOrderMaker = findOrder.getMaker();
        log.info("【findOrder.maker.getId】: {}", findOrder.getMaker().getId());
        log.info("【findOrder.maker.getName】: {}", findOrder.getMaker().getName());
        log.info("【findOrder.maker.getOrders】: {}", findOrder.getMaker().getOrders().size());
        assertEquals(OrderStatus.ORDERED, findOrder.getStatus());
        assertEquals(90, findOrder.getAmount().getDiscount());
        assertEquals(teaMaker.getId(), findOrder.getMaker().getId());
        assertEquals(1, findOrder.getItems().size());
        assertEquals(2L, findOrder.getItems().get(0).getId());
    }


}
