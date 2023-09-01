package com.johann.binaryteamybatis.mapper;

import com.johann.binaryteamybatis.model.MenuItem;
import com.johann.binaryteamybatis.support.handler.MoneyTypeHandler;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumTypeHandler;

import java.util.List;

/**
 * MenuItemMapper 这是一个 MyBatis Mapper 接口，它使用注解来配置 SQL 语句。
 */
@Mapper
public interface MenuItemMapper {

    @Select("select count(*) from t_menu")
    long count();

    @Insert("insert into t_menu(name, size, price, create_time, update_time) " +
            "values(#{name}, #{size}, #{price}, now(), now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(MenuItem menuItem);

    @Update("update t_menu set name=#{name}, size=#{size}, price=#{price}, update_time=now() where id=#{id}")
    int update(MenuItem menuItem);

    @Select("select * from t_menu where id=#{id}")
    @Results(id = "menuItemMap", value = {
            @Result(property = "id", column = "id", id = true),
            // 通过 TypeHandler 来实现特殊类型的处理的, MyBatis 中默认使用 EnumTypeHandler 来处理枚举类型。
            @Result(property = "size", column = "size", typeHandler = EnumTypeHandler.class),
            // 自定义 TypeHandler 处理 Money 类型
            @Result(property = "price", column = "price", typeHandler = MoneyTypeHandler.class),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    MenuItem findById(@Param("id") Long id);

    @Delete("delete from t_menu where id=#{id}")
    int deleteById(@Param("id") Long id);

    @Select("select * from t_menu")
    @ResultMap("menuItemMap")
    List<MenuItem> findAll();

    @Select("select m.* from t_menu m,t_order_item i where m.id = i.item_id and i.order_id = #{orderId}")
    @ResultMap("menuItemMap")
    List<MenuItem> findByOrderId(Long orderId);
}
