package com.johann.binarytea.hibernate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.joda.money.Money;
import org.hibernate.annotations.Parameter;
import javax.persistence.*;
import java.util.Date;

/**
 * 菜单选项
 *
 * @author Johann
 * @version 1.0
 * @see
 **/

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_menu")
public class MenuItem {

    @Id
    @GeneratedValue //默认：(strategy = GenerationType.AUTO)
    /**
     * 除了自增主键，还可以用 @SequenceGenerator 和 @TaleGenerator 来指定基于序列和表生成主键
     * eg.1:
     * @Id
     * @GeneratedValue(strategy=SEQUENCE, generator="CUST_SEQ")
     * @Column(name="CUST_ID")
     *
     * eg.2:
     * @Id
     * @GeneratedValue(strategy=TABLE, generator="CUST_GEN")
     * @Column(name="CUST_ID")
     *
     * eg.3:
     * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator")
     * @SequenceGenerator(name = "sequence-generator", sequenceName = "seq_menu")
     */
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    // 通过 @Enumerated(EnumType.STRING)指明用枚举值来做映射，也就是说数据库里的值会是 SMALL、MEDIUM 和 LARGE。
    @Enumerated(EnumType.STRING)
    private Size size;

    // 通过 @Type 声明了如何将数据中 Long 类型的值转换为 Money。
    // 这里用到了一个开源的转换类，如果数据库里存的是小数类型，可以考虑把 PersistentMoneyMinorAmount 替换为 PersistentMoneyAmount。
    @Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmount",
            parameters = {@Parameter(name = "currencyCode", value = "CNY")})
    private Money price;

    @Column(name = "create_time", updatable = false)
    // @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createTime;

    @Column(name = "update_time")
    // @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updateTime;
}
