package com.johann.binarytea.hibernate;

import com.johann.binarytea.BinaryteaApplication;
import com.johann.binarytea.hibernate.model.MenuItem;
import com.johann.binarytea.hibernate.model.Size;
import com.johann.binarytea.hibernate.repository.MenuRepositoryHibernate;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Hibernate 测试类
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@SpringBootTest(classes = BinaryteaApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MenuRepositoryHibernateTest {

    @Autowired
    private MenuRepositoryHibernate menuRepository;
    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    // 添加了 @BeforeEach 和 @AfterEach 的方法会分别在测试方法执行前后被 JUnit 执行；
    @BeforeEach
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @AfterEach
    public void tearDown() {
        jdbcTemplate = null;
    }

    /**
     * 测试插入菜单项
     */
    @Test
    @Order(1)
    public void testInsertItem(){
        List<MenuItem> items = Stream.of("Go橙汁", "Python气泡水", "JavaScript苏打水")
                .map(name -> MenuItem.builder()
                        .name(name)
                        .size(Size.MEDIUM)
                        .price(Money.ofMinor(CurrencyUnit.of("CNY"), 1200))
                        .build())
                .peek(menuItem -> {
                    // 插入数据
                    menuRepository.insertItem(menuItem);
                })
                .collect(Collectors.toList());

        for (int i = 0; i < 3; i++){
            assertEquals(i+1, items.get(i).getId());
            assertItem(i+1L, items.get(i));
        }
    }

    /**
     * 测试查询菜单项
     */
    @Test
    @Order(2)
    void testCountMenuItems() {
        assertEquals(3, menuRepository.countMenuItems());
    }

    @Test
    @Order(3)
    void testQueryForItem() {
        MenuItem item = menuRepository.queryForItem(1L);
        assertNotNull(item);
        assertEquals("Go橙汁", item.getName());
    }

    @Test
    @Order(4)
    void testQueryAllItems() {
        List<MenuItem> items = menuRepository.queryAllItems();
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(3, items.size());
    }

    /**
     * 测试更新
     */
    @Test
    @Order(5)
    void testUpdateItem() {
        MenuItem item = menuRepository.queryForItem(1L);
        item.setPrice(Money.ofMinor(CurrencyUnit.of("CNY"), 1000));
        menuRepository.updateItem(item);
        BigDecimal price = jdbcTemplate.queryForObject("SELECT price FROM t_menu WHERE id = ?", BigDecimal.class, 1L);
        assertEquals(item.getPrice().getAmount(),price);
    }

    /**
     * 测试删除
     */
    @Test
    @Order(6)
    void testDeleteItem() {
        menuRepository.deleteItem(2L);
        assertNull(menuRepository.queryForItem(2L));
    }

    private void assertItem(Long id, MenuItem item){
        Map<String, Object> result = jdbcTemplate.queryForMap("SELECT * FROM t_menu WHERE id = ?", id);
        assertEquals(item.getName(), result.get("name"));
        assertEquals(item.getSize().name(), result.get("size"));
        assertEquals(item.getPrice().getAmount(), new BigDecimal(result.get("price").toString()));
    }

}
