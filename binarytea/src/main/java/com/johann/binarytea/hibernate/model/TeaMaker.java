package com.johann.binarytea.hibernate.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 调茶师
 * @Description: TeaMaker
 * @Auther: Johann
 * @Version: 1.0
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_tea_maker")
public class TeaMaker {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    // @OneToMany 的获取方式默认是懒加载，即在使用时才会加载，mappedBy 标明了根据 Order.maker 属性来进行映射。
    @OneToMany(mappedBy = "maker", fetch = FetchType.LAZY) //默认是 LAZY
    // @OrderBy 会对取得的结果进行排序，默认按主键排序，也可以指定多个字段用逗号分隔，默认是升序(即 asc)。
    @OrderBy("id desc")
    private List<Order> orders = new ArrayList<>();

    @Column(name = "create_time", updatable = false)
    @CreationTimestamp
    private Date createTime;

    @Column(name = "update_time")
    @UpdateTimestamp
    private Date updateTime;
}
