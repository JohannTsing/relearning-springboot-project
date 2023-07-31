package com.johann.binarytea.hibernate.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 订单
 * @Description: Order
 * @Auther: Johann
 * @Version: 1.0
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_order")
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id")
    private TeaMaker maker;

    @ManyToMany
    @JoinTable(name = "t_order_item",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id"))
    @OrderBy
    private List<MenuItem> items;

    // @Embedded 注解表示这是一个嵌入类，也就是说 Order 里面有一个嵌入类 Amount，而不是一个单独的表。
    @Embedded
    private Amount amount;

    @Enumerated
    private OrderStatus status;

    @Column(name = "create_time", updatable = false)
    @CreationTimestamp
    private Date createTime;

    @Column(name = "update_time")
    @UpdateTimestamp
    private Date updateTime;
}
