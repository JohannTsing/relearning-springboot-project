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

    @ManyToOne(fetch = FetchType.LAZY) //默认是 EAGER
    @JoinColumn(name = "maker_id",referencedColumnName = "id") // referencedColumnName: 关联表中被引用的列名，默认为被关联表的主键列
    private TeaMaker maker;

    @ManyToMany(fetch = FetchType.LAZY) //默认是 LAZY
    @JoinTable(name = "t_order_item", // 中间表的名称
            joinColumns = @JoinColumn(name = "item_id"), // 连接当前表的外键列
            inverseJoinColumns = @JoinColumn(name = "order_id")) // 连接关联表的外键列
    @OrderBy //("id desc") 默认按照主键升序(ASC)排序
    private List<MenuItem> items;

    // @Embedded 注解表示这是一个嵌入类，也就是说 Order 里面有一个嵌入类 Amount，而不是一个单独的表。
    @Embedded
    private Amount amount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "create_time", updatable = false)
    @CreationTimestamp
    private Date createTime;

    @Column(name = "update_time")
    @UpdateTimestamp
    private Date updateTime;
}
