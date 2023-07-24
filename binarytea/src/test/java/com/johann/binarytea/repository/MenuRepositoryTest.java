package com.johann.binarytea.repository;

import com.johann.binarytea.BinaryteaApplication;
import com.johann.binarytea.model.MenuItem;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
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
@SpringBootTest
/**
 * @TestMethodorder 注解用来指定操作的执行顺序，可以选择以下几种顺序进行排序：
 * 1, MethodOrderer.Alphanumeric.class 根据方法名称按字母数字排序【从 JUnit Jupiter 5.7 起使用 MethodOrderer.MethodName；将在 6.0 中移除。】
 * 2, MethodOrderer.OrderAnnotation.class 按照方法上的@Order注解的顺序执行
 * 3, MethodOrderer.Random.class 随机执行
 * 4, MethodOrderer.MethodName.class 根据方法名称按字母数字排序,如果两个方法的名称相同，则其形参列表的字符串表示形式将用作比较方法的后备。
 * 5, MethodOrderer.DisplayName.class 根据方法的显示名称按字母数字排序,此时测试方法上使用@DisplayName注解指定显示名称
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MenuRepositoryTest {

    @Autowired
    private MenuRepository menuRepository;

    /**
     * 测试查询菜单项总数
     */
    @Test
    @Order(0)
    void testCountMenuItems() {
        assertEquals(2, menuRepository.countMenuItems());
    }

    @Test
    @Order(0)
    void testQueryAllItems() {
        List<MenuItem> items = menuRepository.queryAllItems();
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(2, items.size());
    }

    /**
     * 测试根据ID查询菜单项
     */
    @Test
    @Order(0)
    void testQueryForItem() {
        MenuItem item = menuRepository.queryForItem(1L);
        assertItem(item, 1L, "Java咖啡", "中杯", new BigDecimal("10.0"));
    }

    /**
     * 测试插入菜单项
     */
    @Test
    @Order(1)
    void testInsertItem(){
        MenuItem item = MenuItem.builder()
                .name("Go橙汁").size("中杯")
                .price(BigDecimal.valueOf(12.00))
                .build();

        assertEquals(1, menuRepository.insertItem(item));
        assertNull(item.getId());
        MenuItem queryItem = menuRepository.queryForItem(3L);
        assertItem(queryItem, 3L, "Go橙汁", "中杯", BigDecimal.valueOf(12.0));

        assertEquals(1, menuRepository.insertItemWithNamedParameter(item));
        assertNull(item.getId());
        queryItem = menuRepository.queryForItem(4L);
        assertItem(queryItem, 4L, "Go橙汁", "中杯", BigDecimal.valueOf(12.0));


        assertEquals(1, menuRepository.insertItemAndFillId(item));
        queryItem = menuRepository.queryForItem(item.getId());
        assertItem(queryItem, 5L, "Go橙汁", "中杯", BigDecimal.valueOf(12.0));
    }

    /**
     * 测试删除菜单项
     */
    @Test
    @Order(2)
    void testDelete() {
        assertEquals(1, menuRepository.deleteItem(3L));
        assertEquals(1, menuRepository.deleteItem(2L));
    }


    /**
     * 测试批量插入菜单项
     */
    @Test
    @Order(3)
    void testInsertItems() {
        List<MenuItem> items = Stream.of("Go橙汁", "Python气泡水", "JavaScript苏打水")
                .map(n -> MenuItem.builder().name(n).size("中杯").price(BigDecimal.valueOf(12.00)).build())
                .collect(Collectors.toList());
        assertEquals(3, menuRepository.insertItems(items));
        assertItem(menuRepository.queryForItem(6L),
                6L, "Go橙汁", "中杯", BigDecimal.valueOf(12.0));
        assertItem(menuRepository.queryForItem(7L),
                7L, "Python气泡水", "中杯", BigDecimal.valueOf(12.0));
        assertItem(menuRepository.queryForItem(8L),
                8L, "JavaScript苏打水", "中杯", BigDecimal.valueOf(12.0));
    }


    private void assertItem(MenuItem item, Long id, String name, String size, BigDecimal price){
        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(size, item.getSize());
        assertEquals(price, item.getPrice());
    }

}
