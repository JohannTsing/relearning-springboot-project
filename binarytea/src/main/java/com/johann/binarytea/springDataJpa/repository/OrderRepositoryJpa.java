package com.johann.binarytea.springDataJpa.repository;

import com.johann.binarytea.hibernate.model.Order;
import com.johann.binarytea.hibernate.model.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 继承 JpaRepository 接口的子接口无需添加`@Repository`注解，因为 JpaRepository 已经添加了该注解
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
public interface OrderRepositoryJpa extends JpaRepository<Order, Long> {

    /**
     * 通过订单状态查询订单列表，并按照订单 ID 升序排序
     * @param status
     * @return
     */
    // 使用 @EntityGraph 注解，指定需要加载的实体类属性，来急切地获取实体类的关联属性
    @EntityGraph(attributePaths = {"maker", "items"})
    List<Order> findByStatusOrderByIdAsc(OrderStatus status);

    // 通过制作者姓名进行相似匹配，且忽略大小写，获取到的订单列表，按照更新时间降序排序，如果更新时间相同则按照订单 ID 升序排序
    // 对于order.maker.name 这样的属性，可以像这样用“_”明确地表示 name是Maker 的属性，也可以不加“_”，让 Spring Data 自行判断。
    List<Order> findByMaker_NameLikeIgnoreCaseOrderByUpdateTimeDescId(String name);
}
