package com.johann.binaryteamybatis.mapper;

import com.johann.binaryteamybatis.model.MenuItem;
import com.johann.binaryteamybatis.model.Order;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Mapper
public interface OrderMapper {

    /**
     * 保存订单
     * @param order
     * @return
     */
    @Insert("insert into t_order(maker_id, status, amount_discount, amount_pay, amount_total, create_time, update_time) " +
            // 保存 status时, 使用 EnumOrdinalTypeHandler 类型处理器，它能够保存序号。
            "values (#{maker.id}, #{status,typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}, " +
            "#{amount.discount}, #{amount.payAmount}, #{amount.totalAmount}, now(), now()")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Order order);

    /**
     * 新增订单时，需要维护订单与菜单项之间的关联关系, 具体的做法是向【多对多】关联的中间表中插入数据。
     *
     * @param orderId
     * @param item
     * @return
     */
    @Insert("insert into t_order_item (order_id, item_id) values (#{orderId}, #{item.id})")
    int addOrderItem(Long orderId, MenuItem item);

    @Select("select * from t_order where id = #{id}")
    @Results(id = "orderMap", value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "amount.discount", column = "amount_discount"),
            @Result(property = "amount.totalAmount", column = "amount_total"),
            @Result(property = "amount.payAmount", column = "amount_pay"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
            @Result(property = "maker", column = "maker_id",
                    one = @One(select = "com.johann.binaryteamybatis.mapper.TeaMakerMapper.findById",
                            fetchType = FetchType.LAZY,resultMap = "teaMakerMap")
            ),
            @Result(property = "items", column = "id",
                    many = @Many(select = "com.johann.binaryteamybatis.mapper.MenuItemMapper.findByOrderId",
                            fetchType = FetchType.LAZY)
            )
    })
    Order findById(Long id);

    @Select("select * from t_order where maker_id = #{makerId}")
    @ResultMap("orderMap")
    List<Order> findByMakerId(Long makerId);

    @Select("select * from t_order")
    @ResultMap("orderMap")
    List<Order> findAll();
}
