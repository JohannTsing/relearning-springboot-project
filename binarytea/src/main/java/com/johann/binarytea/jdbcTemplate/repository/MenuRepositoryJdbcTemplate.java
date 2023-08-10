package com.johann.binarytea.jdbcTemplate.repository;

import com.johann.binarytea.jdbcTemplate.model.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 使用 JdbcTemplate 操作数据库
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Repository
public class MenuRepositoryJdbcTemplate {

    public static final String SQL_INSERT_ITEM
            = "insert into t_menu_jdbc (name, size, price, create_time, update_time) values (?, ?, ?, now(), now())";
    private JdbcTemplate jdbcTemplate;

    // 2, 【一般】搭配@Autowired注解，使用Setter注入JdbcTemplate
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1, 【建议】使用构造器注入JdbcTemplate
//    public MenuRepository(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }

    // 3, 【不建议】搭配@Autowired注解，使用字段注入NamedParameterJdbcTemplate
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    /**
     * 查询菜单项总数
     * @return
     */
    public long countMenuItems() {
        return jdbcTemplate
                .queryForObject("select count(*) from t_menu_jdbc", Long.class);
    }

    /**
     * 查询所有菜单项
     * @return
     */
    public List<MenuItem> queryAllItems() {
        return jdbcTemplate.query("select * from t_menu_jdbc", rowMapper());
    }

    /**
     * 根据ID查询菜单项
     * @param id
     * @return
     */
    public MenuItem queryForItem(Long id) {
        return jdbcTemplate.queryForObject("select * from t_menu_jdbc where id = ?",
                rowMapper(), id);
    }

    /**
     * 插入菜单项
     * @param item
     * @return
     */
    public int insertItem(MenuItem item) {
        return jdbcTemplate.update(SQL_INSERT_ITEM,
                item.getName(), item.getSize(), item.getPrice().multiply(BigDecimal.valueOf(100)).longValue());
    }

    /**
     * 插入菜单项，并取得自增ID
     * @param item
     * @return
     */
    public int insertItemAndFillId(MenuItem item) {
        // 【不建议】使用 KeyHolder 类来持有生成的键
        KeyHolder keyHolder = new GeneratedKeyHolder();
        // 【不建议】使用 PreparedStatementCreator 来创建 PreparedStatement
        int affected = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement
                    = connection.prepareStatement(SQL_INSERT_ITEM, PreparedStatement.RETURN_GENERATED_KEYS);
            // 也可以使用以下方式
//            PreparedStatement preparedStatement
//                    = connection.prepareStatement(SQL_INSERT_ITEM, new String[]{"id"});
            preparedStatement.setString(1, item.getName());
            preparedStatement.setString(2, item.getSize());
            preparedStatement.setLong(3,
                    item.getPrice().multiply(BigDecimal.valueOf(100)).longValue());
            return preparedStatement;
        }, keyHolder);

        if (affected == 1) {
            item.setId(keyHolder.getKey().longValue());
        }
        return affected;
    }

    /**
     * 删除菜单项
     * @param id
     * @return
     */
    public int deleteItem(Long id) {
        return jdbcTemplate.update("delete from t_menu_jdbc where id = ?", id);
    }


    /**
     * 使用 NamedParameterJdbcTemplate 插入菜单项 [MapSqlParameterSource,BeanPropertySqlParameterSource]
     * @param item
     * @return
     */
    public int insertItemWithNamedParameter(MenuItem item) {
        String insert_sql = "insert into t_menu_jdbc (name, size, price, create_time, update_time) values " +
                "(:name, :size, :price * 100, now(), now())";

        // 1, 使用 MapSqlParameterSource,MapSqlParameterSource会以Map 的形式来提供参数
//        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
//        sqlParameterSource.addValue("name",item.getName());
//        sqlParameterSource.addValue("size",item.getSize());
//        sqlParameterSource.addValue("price",item.getPrice().multiply(BigDecimal.valueOf(100)).longValue());

        // 2, 使用 BeanPropertySqlParameterSource,BeanPropertySglParameterSource 会从 Bean 属性中提取参数。
        BeanPropertySqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(item);

        return namedParameterJdbcTemplate.update(insert_sql, sqlParameterSource);
    }


    /**
     * 批量插入菜单项 batchUpdate(String sql, final BatchPreparedStatementSetter pss)
     * @param items
     * @return
     */
    public int insertItems(List<MenuItem> items) {
        int [] count = jdbcTemplate.batchUpdate(SQL_INSERT_ITEM, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                MenuItem item = items.get(i);
                ps.setString(1, item.getName());
                ps.setString(2, item.getSize());
                ps.setLong(3, item.getPrice().multiply(BigDecimal.valueOf(100)).longValue());
            }

            @Override
            public int getBatchSize() {
                return items.size();
            }
        });
        return Arrays.stream(count).sum();
    }


    /**
     * 批量插入菜单项 batchUpdate(String sql,List<object[]> batchArgs)
     * @param items
     * @return
     */
    public int insertItemsWithBatchArgs(List<MenuItem> items) {
        List<Object[]> batchArgs = items.stream().map(item -> new Object[]{
                        item.getName(), item.getSize(), item.getPrice().multiply(BigDecimal.valueOf(100)).longValue()
                })
                .collect(Collectors.toList());
        int[] count = jdbcTemplate.batchUpdate(SQL_INSERT_ITEM, batchArgs);
        return Arrays.stream(count).sum();
    }

    /**
     * 批量插入菜单项 batchUpdate(String sql, final SqlParameterSource[] batchArgs)
     * @param items
     * @return
     */
    public int insertItemsWithNamedParameter(List<MenuItem> items) {
        String sql = "insert into t_menu_jdbc (name, size, price, create_time, update_time) values " +
                "(:name, :size, :price * 100, now(), now())";
        int[] count = namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(items));
        return Arrays.stream(count).sum();
    }


    /**
     * 使用RowMapper 将字段映射到MenuItem对象
     */
    private RowMapper<MenuItem> rowMapper() {
        return (rs, rowNum) -> MenuItem.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .size(rs.getString("size"))
                .price(BigDecimal.valueOf(rs.getLong("price") / 100.0d))
                .createTime(new Date(rs.getDate("create_time").getTime()))
                .createTime(new Date(rs.getDate("update_time").getTime()))
                .build();
    }
}
